package com.execcexrrvycvtvtv.exes_creatures.entity.path;

import com.execcexrrvycvtvtv.exes_creatures.entity.interfaces.SemiFlyer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BirdPathNavigation extends PathNavigation {
    private static final double FLIGHT_WAYPOINT_RADIUS_SQR = 1.2 * 1.2;

    public BirdPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new BirdNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.canFloat() && this.mob.isInLiquid() || !this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.position();
    }

    @Override
    protected boolean canMoveDirectly(Vec3 start, Vec3 end) {
        return isClearForMovementBetween(this.mob, start, end, true);
    }

    @Override
    protected void followThePath() {
        super.followThePath();

        if (this.path != null
            && !this.path.isDone()
            && this.mob instanceof SemiFlyer flyer
            && flyer.isFlying()) {
            Vec3i next = this.path.getNextNodePos();
            if (this.mob.position().distanceToSqr(Vec3.atBottomCenterOf(next)) < FLIGHT_WAYPOINT_RADIUS_SQR) {
                this.path.advance();
            }
        }
    }

    @Nullable
    @Override
    public Path createPath(Entity entity, int accuracy) {
        return this.createPath(entity.blockPosition(), accuracy);
    }
}