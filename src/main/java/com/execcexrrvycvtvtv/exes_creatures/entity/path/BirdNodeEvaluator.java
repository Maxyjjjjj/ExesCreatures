package com.execcexrrvycvtvtv.exes_creatures.entity.path;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class BirdNodeEvaluator extends WalkNodeEvaluator {

    @Override
    public Node getStart() {
        if (this.mob.onGround() || this.mob.isInLiquid()) {
            return super.getStart();
        }

        int y = Mth.floor(this.mob.getY() + 0.5);
        BlockPos pos = BlockPos.containing(this.mob.getX(), y, this.mob.getZ());
        if (!this.canStartInAir(pos)) {
            AABB box = this.mob.getBoundingBox();
            BlockPos[] corners = {
                BlockPos.containing(box.minX, y, box.minZ),
                BlockPos.containing(box.minX, y, box.maxZ),
                BlockPos.containing(box.maxX, y, box.minZ),
                BlockPos.containing(box.maxX, y, box.maxZ)
            };
            for (BlockPos corner : corners) {
                if (this.canStartInAir(corner)) {
                    return this.getStartNode(corner);
                }
            }
        }
        return this.getStartNode(pos);
    }

    private boolean canStartInAir(BlockPos pos) {
        return this.isPassable(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int getNeighbors(Node[] output, Node node) {
        boolean airborne = node.type == PathType.OPEN;

        int count = airborne ? 0 : super.getNeighbors(output, node);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    if (dy == 0 && !airborne) {
                        continue;
                    }
                    if (!this.canCutThrough(node, dx, dy, dz)) {
                        continue;
                    }
                    Node next = this.findAirNode(node.x + dx, node.y + dy, node.z + dz);
                    if (next != null && !next.closed) {
                        output[count++] = next;
                    }
                }
            }
        }
        return count;
    }

    private boolean canCutThrough(Node from, int dx, int dy, int dz) {
        int axes = (dx != 0 ? 1 : 0) | (dy != 0 ? 2 : 0) | (dz != 0 ? 4 : 0);
        for (int sub = (axes - 1) & axes; sub > 0; sub = (sub - 1) & axes) {
            int sx = (sub & 1) != 0 ? dx : 0;
            int sy = (sub & 2) != 0 ? dy : 0;
            int sz = (sub & 4) != 0 ? dz : 0;
            if (!this.isPassable(from.x + sx, from.y + sy, from.z + sz)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPassable(int x, int y, int z) {
        return this.mob.getPathfindingMalus(this.getCachedPathType(x, y, z)) >= 0.0F;
    }

    @Nullable
    private Node findAirNode(int x, int y, int z) {
        PathType type = this.getCachedPathType(x, y, z);
        float malus = this.mob.getPathfindingMalus(type);
        if (malus < 0.0F) {
            return null;
        }

        Node node = this.getNode(x, y, z);
        node.type = type;
        node.costMalus = Math.max(node.costMalus, malus);
        return node;
    }
}