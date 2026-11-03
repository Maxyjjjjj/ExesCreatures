package com.execcexrrvycvtvtv.exes_creatures.entity.ai;

import com.execcexrrvycvtvtv.exes_creatures.entity.interfaces.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BirdWanderGoal<T extends PathfinderMob & SemiFlyer> extends WaterAvoidingRandomStrollGoal {
    private static final float TAKEOFF_CHANCE = 0.2F;
    private static final float STAY_AIRBORNE_CHANCE = 0.75F;
    private static final int HORIZONTAL_RANGE = 8;
    private static final int MIN_HEIGHT = 2;
    private static final int HEIGHT_VARIANCE = 5;
    private static final int ATTEMPTS = 10;

    private final T bird;

    public BirdWanderGoal(T bird, double speedModifier) {
        super(bird, speedModifier);
        this.bird = bird;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (this.canFly() && this.rollForAir()) {
            Vec3 air = this.findAirPosition();
            if (air != null) {
                return air;
            }
        }
        return super.getPosition();
    }

    private boolean canFly() {
        return this.mob.getPathfindingMalus(PathType.OPEN) >= 0.0F;
    }

    private boolean rollForAir() {
        float chance = this.bird.isFlying() ? STAY_AIRBORNE_CHANCE : TAKEOFF_CHANCE;
        return this.mob.getRandom().nextFloat() < chance;
    }

    @Nullable
    private Vec3 findAirPosition() {
        RandomSource random = this.mob.getRandom();
        Level level = this.mob.level();
        BlockPos origin = this.mob.blockPosition();

        for (int i = 0; i < ATTEMPTS; i++) {
            int x = origin.getX() + random.nextInt(HORIZONTAL_RANGE * 2 + 1) - HORIZONTAL_RANGE;
            int z = origin.getZ() + random.nextInt(HORIZONTAL_RANGE * 2 + 1) - HORIZONTAL_RANGE;
            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos pos = new BlockPos(x, surface + MIN_HEIGHT + random.nextInt(HEIGHT_VARIANCE), z);

            if (this.isValidAirPosition(pos)) {
                return Vec3.atBottomCenterOf(pos);
            }
        }
        return null;
    }

    private boolean isValidAirPosition(BlockPos pos) {
        Vec3 destination = Vec3.atBottomCenterOf(pos);
        return this.mob.level().noCollision(
            this.mob,
            this.mob.getBoundingBox().move(
                destination.subtract(this.mob.position())
            )
        );
    }
}