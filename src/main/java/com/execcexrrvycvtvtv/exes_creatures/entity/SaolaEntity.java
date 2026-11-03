package com.execcexrrvycvtvtv.exes_creatures.entity;

import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class SaolaEntity extends Animal {
    private static final EntityDataAccessor<Boolean> DATA_TRUSTING = SynchedEntityData.defineId(SaolaEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private SaolaAvoidEntityGoal<Player> saolaAvoidPlayersGoal;

    public SaolaEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.reassessTrustingGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TRUSTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Trusting", this.isTrusting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTrusting(compound.getBoolean("Trusting"));
    }

    public boolean isTrusting() {
        return this.entityData.get(DATA_TRUSTING);
    }

    private void setTrusting(boolean trusting) {
        this.entityData.set(DATA_TRUSTING, trusting);
        this.reassessTrustingGoals();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.05D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(4, new SaolaTemptGoal(this, 0.6, (stack) -> stack.is(ModTags.Items.SAOLA_FOOD), true));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, LivingEntity.class, 24, 1.1, 1.4, livingEntity -> livingEntity.getType().is(ModTags.EntityTypes.SAOLA_AVOID)));
        this.goalSelector.addGoal(8, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));

        this.reassessTrustingGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 15.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.23F)
            .add(Attributes.FOLLOW_RANGE, 30F);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.SAOLA_FOOD);
    }

    protected void reassessTrustingGoals() {
        if (this.saolaAvoidPlayersGoal == null) {
            this.saolaAvoidPlayersGoal = new SaolaAvoidEntityGoal<>(this, Player.class, 24.0F, 1.1, 1.4);
        }

        this.goalSelector.removeGoal(this.saolaAvoidPlayersGoal);
        if (!this.isTrusting()) {
            this.goalSelector.addGoal(5, this.saolaAvoidPlayersGoal);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        SaolaEntity calf = ModEntityTypes.SAOLA.get().create(serverLevel);
        if (calf != null && ageableMob instanceof SaolaEntity otherSaola) {
            if (this.isTrusting() || otherSaola.isTrusting()) {
                calf.setTrusting(this.isTrusting());
            }
        }
        return calf;
    }

    public static boolean checkSaolaSpawnRules(EntityType<? extends SaolaEntity> saola, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        boolean flag = MobSpawnType.ignoresLightRequirements(spawnType) || isBrightEnoughToSpawn(level, pos);
        return level.getBlockState(pos.below()).is(ModTags.Blocks.SAOLA_SPAWNABLE_ON) && flag;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.SAOLA_AMBIENT.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 450;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.SAOLA_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.SAOLA_DEATH.get();
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (this.isFood(stack)) {
            this.playSound(SoundEvents.GOAT_EAT, 1.0F, 1.0F);
        }
        super.usePlayerItem(player, hand, stack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isFood(itemstack)) {
            if (!this.isTrusting()) {
                this.usePlayerItem(player, hand, itemstack);
                if (!this.level().isClientSide) {
                    if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                        this.setTrusting(true);
                        this.spawnTrustingParticles(true);
                        this.level().broadcastEntityEvent(this, (byte) 41);
                    } else {
                        this.spawnTrustingParticles(false);
                        this.level().broadcastEntityEvent(this, (byte) 40);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                super.mobInteract(player, hand);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 41) {
            this.spawnTrustingParticles(true);
        } else if (id == 40) {
            this.spawnTrustingParticles(false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnTrustingParticles(boolean isTrusted) {
        ParticleOptions particleoptions = isTrusted ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.0F), this.getRandomY() + 0.5D, this.getRandomZ(1.0F), d0, d1, d2);
        }
    }

    @Override
    public boolean isSteppingCarefully() {
        return this.isCrouching() || super.isSteppingCarefully();
    }

    static class SaolaAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final SaolaEntity saola;

        public SaolaAvoidEntityGoal(SaolaEntity saola, Class<T> entityClassToAvoid, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(saola, entityClassToAvoid, maxDist, walkSpeedModifier, sprintSpeedModifier, Objects.requireNonNull(EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
            this.saola = saola;
        }

        @Override
        public boolean canUse() {
            return !this.saola.isTrusting() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.saola.isTrusting() && super.canContinueToUse();
        }
    }

    static class SaolaTemptGoal extends TemptGoal {
        private final SaolaEntity saola;

        public SaolaTemptGoal(SaolaEntity saola, double speedModifier, Predicate<ItemStack> items, boolean canScare) {
            super(saola, speedModifier, items, canScare);
            this.saola = saola;
        }

        protected boolean canScare() {
            return super.canScare() && !this.saola.isTrusting();
        }
    }
}
