package com.execcexrrvycvtvtv.exes_creatures.entity;

import com.execcexrrvycvtvtv.exes_creatures.entity.ai.BirdWanderGoal;
import com.execcexrrvycvtvtv.exes_creatures.entity.ai.ModFollowOwnerGoal;
import com.execcexrrvycvtvtv.exes_creatures.entity.interfaces.SemiFlyer;
import com.execcexrrvycvtvtv.exes_creatures.entity.path.BirdMoveControl;
import com.execcexrrvycvtvtv.exes_creatures.entity.path.BirdPathNavigation;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class JayEntity extends ShoulderRidingEntity implements SemiFlyer {
    private static final EntityDataAccessor<Boolean> DATA_JAY_FLYING =
        SynchedEntityData.defineId(JayEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int EAT_TICKS = 40;
    private int eatTicks;
    private int ticksSinceEaten;

    public JayEntity(EntityType<? extends ShoulderRidingEntity> type, Level level) {
        super(type, level);
        this.moveControl = new BirdMoveControl<>(this, 10);
        this.setPathfindingMalus(PathType.OPEN, 1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        BirdPathNavigation navigation = new BirdPathNavigation(this, level);
        navigation.setCanFloat(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0)
            .add(Attributes.FLYING_SPEED, 0.4)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_JAY_FLYING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.25));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0, 1.3,
            target -> !this.isTame()
                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
                && !isHoldingJayFood(target)));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, stack -> stack.is(ModTags.Items.JAY_FOOD), false) {
            @Override
            public boolean canUse() {
                return !JayEntity.this.isTame() && super.canUse();
            }
        });

        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            public boolean canUse() {
                return JayEntity.this.isArmed() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return JayEntity.this.isArmed() && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(6, new ModFollowOwnerGoal(this, 1.0, 5.0F, 1.0F));
        this.goalSelector.addGoal(6, new LandOnOwnersShoulderGoal(this));

        this.goalSelector.addGoal(7, new FetchItemGoal(this));
        this.goalSelector.addGoal(8, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(9, new BirdWanderGoal<>(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return JayEntity.this.isArmed() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return JayEntity.this.isArmed() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                return JayEntity.this.isArmed() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return JayEntity.this.isArmed() && super.canContinueToUse();
            }
        });
    }

    private static boolean isHoldingJayFood(LivingEntity entity) {
        return entity.getMainHandItem().is(ModTags.Items.JAY_FOOD)
            || entity.getOffhandItem().is(ModTags.Items.JAY_FOOD);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_JAY_FLYING);
    }

    @Override
    public void setFlying(boolean flying) {
        this.entityData.set(DATA_JAY_FLYING, flying);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.JAY_FOOD);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.JAY_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.JAY_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.JAY_DEATH.get();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        JayEntity chick = ModEntityTypes.JAY.get().create(level);
        if (chick != null && this.isTame()) {
            chick.setOwnerUUID(this.getOwnerUUID());
            chick.setTame(true, true);
        }
        return chick;
    }

    public boolean isArmed() {
        return this.getMainHandItem().is(ModTags.Items.JAY_WEAPONS);
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return !this.isBaby() && this.getMainHandItem().isEmpty();
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.isTame() && !this.isInSittingPose() && this.canHoldItem(stack);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (!this.canHoldItem(stack)) {
            return;
        }
        this.onItemPickup(itemEntity);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack.split(1));
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        this.take(itemEntity, 1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        ItemStack held = this.getMainHandItem();
        FoodProperties food = held.getFoodProperties(this);
        if (food == null || this.getHealth() >= this.getMaxHealth()) {
            this.eatTicks = 0;
            return;
        }
        if (++this.eatTicks >= EAT_TICKS) {
            this.eatTicks = 0;
            this.heal(food.nutrition());
            this.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
            held.shrink(1);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && this.isArmed()) {
            this.getMainHandItem().hurtAndBreak(1, this, EquipmentSlot.MAINHAND);
        }
        return hit;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean clientSide = this.level().isClientSide();

        if (!this.isTame()) {
            if (this.isFood(stack)) {
                if (!clientSide) {
                    this.usePlayerItem(player, hand, stack);
                    if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                        this.tame(player);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                    this.setPersistenceRequired();
                }
                return InteractionResult.sidedSuccess(clientSide);
            }
            return super.mobInteract(player, hand);
        }

        if (!this.isOwnedBy(player)) {
            return super.mobInteract(player, hand);
        }

        boolean sneaking = player.isSecondaryUseActive();

        if (this.isFood(stack) && !sneaking) {
            if (this.getHealth() < this.getMaxHealth()) {
                if (!clientSide) {
                    FoodProperties food = stack.getFoodProperties(this);
                    this.heal(food != null ? (float) food.nutrition() : 1.0F);
                    this.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
                    this.usePlayerItem(player, hand, stack);
                }
                return InteractionResult.sidedSuccess(clientSide);
            }
            return super.mobInteract(player, hand);
        }

        if (!stack.isEmpty() && this.canHoldItem(stack)) {
            if (!clientSide) {
                this.setItemSlot(EquipmentSlot.MAINHAND, stack.copyWithCount(1));
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                stack.consume(1, player);
                this.setPersistenceRequired();
            }
            return InteractionResult.sidedSuccess(clientSide);
        }

        if (stack.isEmpty()) {
            ItemStack held = this.getMainHandItem();
            if (!held.isEmpty() && !sneaking) {
                if (!clientSide) {
                    player.setItemInHand(hand, held.copy());
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
                return InteractionResult.sidedSuccess(clientSide);
            }
            if (!clientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.sidedSuccess(clientSide);
        }

        return super.mobInteract(player, hand);
    }

    public static boolean checkJaySpawnRules(EntityType<JayEntity> jay, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getBlockState(pos.below()).is(BlockTags.PARROTS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    static class FetchItemGoal extends Goal {
        private final JayEntity jay;
        @Nullable
        private ItemEntity item;

        FetchItemGoal(JayEntity jay) {
            this.jay = jay;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.jay.isTame()
                || this.jay.isInSittingPose()
                || this.jay.getTarget() != null
                || !this.jay.getMainHandItem().isEmpty()) {
                return false;
            }
            if (this.jay.getRandom().nextInt(reducedTickDelay(10)) != 0) {
                return false;
            }
            this.item = this.findItem();
            return this.item != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.item != null
                && this.item.isAlive()
                && !this.jay.isInSittingPose()
                && this.jay.getMainHandItem().isEmpty()
                && !this.jay.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.item != null) {
                this.jay.getNavigation().moveTo(this.item, 1.0);
            }
        }

        @Override
        public void stop() {
            this.item = null;
            this.jay.getNavigation().stop();
        }

        @Nullable
        private ItemEntity findItem() {
            List<ItemEntity> items = this.jay.level().getEntitiesOfClass(
                ItemEntity.class,
                this.jay.getBoundingBox().inflate(8.0, 4.0, 8.0),
                entity -> !entity.hasPickUpDelay() && this.jay.wantsToPickUp(entity.getItem()));
            return items.stream()
                .min(Comparator.comparingDouble(this.jay::distanceToSqr))
                .orElse(null);
        }
    }
}