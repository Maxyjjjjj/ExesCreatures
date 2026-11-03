package com.execcexrrvycvtvtv.exes_creatures.entity;

import com.execcexrrvycvtvtv.exes_creatures.entity.abstr.AbstractEagleEntity;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class PhilippineEagleEntity extends AbstractEagleEntity {
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState hoverAnimationState = new AnimationState();

    public PhilippineEagleEntity(EntityType<? extends AbstractEagleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isSpeciesPrey(LivingEntity target) {
        return target.getType().is(ModTags.EntityTypes.PHILIPPINE_EAGLE_PREY);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    public boolean flapping;

    private void setupAnimationStates() {
        boolean sitting = this.isInSittingPose();
        float pitch = this.getXRot();
        if (pitch < -12.0F) this.flapping = true;
        else if (pitch > -4.0F) this.flapping = false;
        this.flapStrength = Mth.lerp(0.15F, this.flapStrength, this.isFlying() && this.flapping ? 1.0F : 0.0F);
        boolean flying = this.isFlying() && !sitting;

        this.sitAnimationState.animateWhen(sitting, this.tickCount);
        this.hoverAnimationState.animateWhen(flying && this.flapping && getDeltaMovement().horizontalDistanceSqr() <= 0.0025D, this.tickCount);
        this.flyAnimationState.animateWhen(flying && this.flapping && getDeltaMovement().horizontalDistanceSqr() > 0.0025D, this.tickCount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 22.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.24F)
            .add(Attributes.ATTACK_DAMAGE, 6.0F)
            .add(Attributes.FLYING_SPEED, 0.4D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        PhilippineEagleEntity chick = ModEntityTypes.PHILIPPINE_EAGLE.get().create(serverLevel);
        if (chick != null && this.isTame()) {
            chick.setOwnerUUID(this.getOwnerUUID());
            chick.setTame(true, true);
        }
        return chick;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.PHILIPPINE_EAGLE_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.PHILIPPINE_EAGLE_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.PHILIPPINE_EAGLE_DEATH.get();
    }

    public static boolean checkPEagleSpawnRules(EntityType<PhilippineEagleEntity> philippineEagleEntityEntityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getBlockState(pos.below()).is(BlockTags.PARROTS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }
}
