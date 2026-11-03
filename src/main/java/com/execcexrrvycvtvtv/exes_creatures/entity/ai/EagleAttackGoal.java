package com.execcexrrvycvtvtv.exes_creatures.entity.ai;

import com.execcexrrvycvtvtv.exes_creatures.entity.abstr.AbstractEagleEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.pathfinder.PathType;

public class EagleAttackGoal extends MeleeAttackGoal {

    protected final AbstractEagleEntity eagle;

    private static final double DIVE_SPEED = 1.25D;
    private static final double DIVE_DISTANCE = 5.0D;
    private static final double ATTACK_REACH = 0.4D;
    private static final float HUNTING_GROUND_MALUS = 1.0F;

    private final double baseSpeed;
    private float oldOpenMalus;
    private float oldWalkableMalus;
    private boolean flightCosts;

    public EagleAttackGoal(AbstractEagleEntity eagle, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(eagle, speedModifier, followingTargetEvenIfNotSeen);
        this.eagle = eagle;
        this.baseSpeed = speedModifier;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.eagle.getTarget();

        return target != null
            && target.isAlive()
            && this.eagle.canHunt(target)
            && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.eagle.getTarget();

        return target != null
            && target.isAlive()
            && this.eagle.canHunt(target)
            && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();

        this.oldOpenMalus = this.eagle.getPathfindingMalus(PathType.OPEN);
        this.oldWalkableMalus = this.eagle.getPathfindingMalus(PathType.WALKABLE);
        this.flightCosts = this.oldOpenMalus >= 0.0F;
        if (this.flightCosts) {
            this.eagle.setPathfindingMalus(PathType.OPEN, 0.0F);
            this.eagle.setPathfindingMalus(PathType.WALKABLE, HUNTING_GROUND_MALUS);
        }
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.eagle.getTarget();
        if (target != null) {
            boolean far = this.eagle.distanceToSqr(target) > DIVE_DISTANCE * DIVE_DISTANCE;
            this.eagle.getNavigation().setSpeedModifier(far ? DIVE_SPEED : this.baseSpeed);
        }
    }

    @Override
    protected boolean canPerformAttack(LivingEntity target) {
        return this.isTimeToAttack()
            && this.eagle.getBoundingBox().inflate(ATTACK_REACH).intersects(target.getBoundingBox())
            && this.eagle.getSensing().hasLineOfSight(target);
    }

    @Override
    public void stop() {
        super.stop();

        if (this.flightCosts) {
            this.eagle.setPathfindingMalus(PathType.OPEN, this.oldOpenMalus);
            this.eagle.setPathfindingMalus(PathType.WALKABLE, this.oldWalkableMalus);
            this.flightCosts = false;
        }
    }
}