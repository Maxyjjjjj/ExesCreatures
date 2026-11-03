package com.execcexrrvycvtvtv.exes_creatures.entity.abstr;

import com.execcexrrvycvtvtv.exes_creatures.entity.ai.BirdWanderGoal;
import com.execcexrrvycvtvtv.exes_creatures.entity.ai.EagleAttackGoal;
import com.execcexrrvycvtvtv.exes_creatures.entity.ai.ModFollowOwnerGoal;
import com.execcexrrvycvtvtv.exes_creatures.entity.interfaces.SemiFlyer;
import com.execcexrrvycvtvtv.exes_creatures.entity.path.BirdMoveControl;
import com.execcexrrvycvtvtv.exes_creatures.entity.path.BirdPathNavigation;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractEagleEntity extends TamableAnimal implements NeutralMob, SemiFlyer {
    private static final EntityDataAccessor<Boolean> DATA_EAGLE_FLYING =
        SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    protected float flapStrength;

    public float getFlapStrength() {
        return this.flapStrength;
    }

    protected AbstractEagleEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new BirdMoveControl<>(this, 10);
        this.setPathfindingMalus(PathType.OPEN, 1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        BirdPathNavigation navigation = new BirdPathNavigation(this, level);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new EagleAttackGoal(this, 1.0f, true));
        this.goalSelector.addGoal(3, new ModFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new BirdWanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, this::isSpeciesPrey));
        this.targetSelector.addGoal(6, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public abstract boolean isSpeciesPrey(LivingEntity target);

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

        if (stack.isEmpty()) {
            if (!clientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.sidedSuccess(clientSide);
        }

        return super.mobInteract(player, hand);
    }

    public boolean isForbiddenTarget(@Nullable LivingEntity target) {
        if (target == null || target == this || !target.isAlive()) return true;
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) return true;
        if (this.isTame() && target == this.getOwner()) return true;
        return target instanceof TamableAnimal pet && pet.isTame() && pet.getOwner() != null && this.isOwnedBy(pet.getOwner());
    }

    public boolean canHunt(LivingEntity target) {
        return !this.isForbiddenTarget(target) && this.isSpeciesPrey(target);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast) && !(target instanceof ArmorStand)) {
            if (!(target instanceof AbstractEagleEntity eagle)) {
                if (target instanceof Player player) {
                    if (owner instanceof Player player1) {
                        if (!player1.canHarmPlayer(player)) {
                            return false;
                        }
                    }
                }

                if (target instanceof AbstractHorse abstracthorse) {
                    if (abstracthorse.isTamed()) {
                        return false;
                    }
                }

                if (target instanceof TamableAnimal tamableanimal) {
                    return !tamableanimal.isTame();
                }

                return true;
            } else {
                return !eagle.isTame() || eagle.getOwner() != owner;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.EAGLE_FOOD);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_30400_) {
        this.persistentAngerTarget = p_30400_;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_EAGLE_FLYING, false);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_EAGLE_FLYING);
    }

    @Override
    public void setFlying(boolean flying) {
        this.entityData.set(DATA_EAGLE_FLYING, flying);
    }
}
