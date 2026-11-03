package com.execcexrrvycvtvtv.exes_creatures.entity.path;

import com.execcexrrvycvtvtv.exes_creatures.entity.interfaces.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BirdMoveControl<T extends Mob & SemiFlyer> extends MoveControl {
    private static final float SPEED_SCALE = 0.7F;
    private static final float THRUST_RESPONSE = 0.3F;
    private static final float APPROACH_RADIUS = 5.0F;
    private static final float MIN_APPROACH_SPEED = 0.3F;

    private static final float MAX_YAW_TURN = 20.0F;
    private static final float MAX_CLIMB = 35.0F;
    private static final float MAX_DIVE = 60.0F;
    private static final float TAKEOFF_PITCH = -25.0F;

    private static final float TAKEOFF_LIFT = 0.35F;
    private static final int MIN_FLIGHT_TICKS = 10;
    private static final double FALL_RECOVERY_SPEED = 0.6;

    private static final int CIRCLE_TICKS = 160;
    private static final float CIRCLE_TURN = 5.0F;
    private static final float CIRCLE_SPEED = 0.6F;
    private static final float GLIDE_SPEED = 0.6F;
    private static final float GLIDE_PITCH = 25.0F;

    private static final double ARRIVED_DIST_SQR = 0.01;

    private final T bird;
    private final int maxTurn;
    private int flightTicks;
    private int idleTicks;
    private int circleDirection = 1;

    public BirdMoveControl(T bird, int maxTurn) {
        super(bird);
        this.bird = bird;
        this.maxTurn = maxTurn;
    }

    @Override
    public void tick() {
        if (!this.bird.isFlying() && this.shouldTakeOff()) {
            this.takeOff();
        }

        if (this.bird.isFlying()) {
            this.tickFlying();
        } else {
            this.mob.setNoGravity(false);
            super.tick();
        }
    }

    private boolean shouldTakeOff() {
        if (this.operation == Operation.MOVE_TO && this.isAirborne(this.wantedX, this.wantedY, this.wantedZ)) {
            return true;
        }
        return !this.mob.onGround()
            && !this.mob.isInLiquid()
            && this.mob.getDeltaMovement().y < -FALL_RECOVERY_SPEED;
    }

    private void takeOff() {
        this.bird.setFlying(true);
        this.flightTicks = 0;
        this.idleTicks = 0;
        this.circleDirection = this.mob.getRandom().nextBoolean() ? 1 : -1;

        if (this.mob.onGround()) {
            Vec3 velocity = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(velocity.x, Math.max(velocity.y, TAKEOFF_LIFT), velocity.z);
        }
    }

    private void land() {
        this.bird.setFlying(false);
        this.mob.setNoGravity(false);
        this.mob.setXRot(0.0F);
    }

    private boolean isAirborne(double x, double y, double z) {
        Level level = this.mob.level();
        BlockPos below = BlockPos.containing(x, y, z).below();
        return level.getBlockState(below).getCollisionShape(level, below).isEmpty();
    }

    private void tickFlying() {
        this.flightTicks++;

        this.mob.setNoGravity(true);
        this.mob.setXxa(0.0F);
        this.mob.setYya(0.0F);
        this.mob.setZza(0.0F);

        if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            this.idleTicks = 0;
            this.flyTowardTarget();
        } else {
            this.flyIdle();
        }
    }

    private void flyTowardTarget() {
        if (this.flightTicks > MIN_FLIGHT_TICKS
            && this.mob.onGround()
            && !this.isAirborne(this.wantedX, this.wantedY, this.wantedZ)) {
            this.land();
            return;
        }

        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        if (dx * dx + dy * dy + dz * dz < ARRIVED_DIST_SQR) {
            return;
        }
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float targetPitch = (float) -(Mth.atan2(dy, horizontal) * (180.0 / Math.PI));
        targetPitch = Mth.clamp(targetPitch, -MAX_CLIMB, MAX_DIVE);
        if (this.mob.onGround()) {
            targetPitch = Math.min(targetPitch, TAKEOFF_PITCH);
        }
        if (this.mob.horizontalCollision) {
            targetPitch = Math.min(targetPitch, -MAX_CLIMB);
        }

        float yaw = this.rotlerp(this.mob.getYRot(), targetYaw, MAX_YAW_TURN);
        float pitch = this.rotlerp(this.mob.getXRot(), targetPitch, this.maxTurn);
        float yawError = Math.abs(Mth.wrapDegrees(targetYaw - yaw));

        float speed = this.cruiseSpeed(this.speedModifier)
            * (1.0F + pitch / 90.0F * 0.6F)
            * (0.5F + 0.5F * Math.max(0.0F, Mth.cos(yawError * Mth.DEG_TO_RAD)))
            * this.approachFactor();
        this.steer(yaw, pitch, speed);
    }

    private void flyIdle() {
        this.idleTicks++;
        boolean circling = this.idleTicks <= CIRCLE_TICKS;

        if (!circling && (this.mob.onGround() || this.mob.isInLiquid())) {
            this.land();
            return;
        }
        if (this.mob.horizontalCollision) {
            this.circleDirection = -this.circleDirection;
        }

        float targetPitch = circling ? 0.0F : GLIDE_PITCH;
        if (circling && this.mob.onGround()) {
            targetPitch = TAKEOFF_PITCH;
        }

        float yaw = this.mob.getYRot() + (circling ? this.circleDirection * CIRCLE_TURN : 0.0F);
        float pitch = this.rotlerp(this.mob.getXRot(), targetPitch, this.maxTurn);
        float speed = this.cruiseSpeed(1.0) * (circling ? CIRCLE_SPEED : GLIDE_SPEED);
        this.steer(yaw, pitch, speed);
    }

    private float cruiseSpeed(double modifier) {
        return (float) (modifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED)) * SPEED_SCALE;
    }

    private float approachFactor() {
        if (this.mob.isAggressive()) return 1.0F;
        PathNavigation navigation = this.mob.getNavigation();
        BlockPos destination = navigation.getTargetPos();
        if (destination == null || navigation.isDone()) {
            return 1.0F;
        }
        double distance = this.mob.position().distanceTo(Vec3.atCenterOf(destination));
        return (float) Mth.clamp(distance / APPROACH_RADIUS, MIN_APPROACH_SPEED, 1.0);
    }

    private void steer(float yaw, float pitch, float speed) {
        this.mob.setYRot(yaw);
        this.mob.setXRot(pitch);

        Vec3 wanted = direction(yaw, pitch).scale(speed);
        Vec3 velocity = this.mob.getDeltaMovement();
        this.mob.setDeltaMovement(velocity.add(wanted.subtract(velocity).scale(THRUST_RESPONSE)));
    }

    private static Vec3 direction(float yaw, float pitch) {
        float yawRad = yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;
        float cosPitch = Mth.cos(pitchRad);
        return new Vec3(-Mth.sin(yawRad) * cosPitch, -Mth.sin(pitchRad), Mth.cos(yawRad) * cosPitch);
    }
}