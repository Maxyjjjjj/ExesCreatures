package com.execcexrrvycvtvtv.exes_creatures.entity;

import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ServalEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR =
        SynchedEntityData.defineId(ServalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CROUCHING =
        SynchedEntityData.defineId(ServalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_POUNCING =
        SynchedEntityData.defineId(ServalEntity.class, EntityDataSerializers.BOOLEAN);

    private ServalAvoidEntityGoal<Player> avoidPlayersGoal;
    private ServalTemptGoal temptGoal;

    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState crouchAnimationState = new AnimationState();
    public final AnimationState pounceAnimationState = new AnimationState();

    public ServalEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 12.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.3F)
            .add(Attributes.ATTACK_DAMAGE, 3.0F);
    }

    @Override
    protected void registerGoals() {
        this.temptGoal = new ServalTemptGoal(this, 0.6, stack -> stack.is(ModTags.Items.SERVAL_FOOD), true);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, this.temptGoal);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
        this.goalSelector.addGoal(8, new ServalPounceGoal(this));
        this.goalSelector.addGoal(9, new ServalAttackGoal(this));
        this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8, 1.0000001E-5F));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
            target -> target.getType().is(ModTags.EntityTypes.SERVAL_PREY)));
        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        builder.define(DATA_CROUCHING, false);
        builder.define(DATA_POUNCING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("CollarColor", (byte) this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        }
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            double d0 = this.getMoveControl().getSpeedModifier();
            if (d0 <= 0.6) {
                this.setPose(Pose.CROUCHING);
                this.setIsCrouching(true);
                this.setSprinting(false);
            } else if (d0 >= 1.33) {
                this.setPose(Pose.STANDING);
                this.setIsCrouching(false);
                this.setSprinting(true);
            } else {
                this.setPose(Pose.STANDING);
                this.setIsCrouching(false);
                this.setSprinting(false);
            }
        } else {
            this.setPose(Pose.STANDING);
            this.setIsCrouching(false);
            this.setSprinting(false);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.isInSittingPose()) {
            this.pounceAnimationState.stop();
            this.crouchAnimationState.stop();
            this.sitAnimationState.startIfStopped(this.tickCount);
        } else {
            this.sitAnimationState.stop();
            this.pounceAnimationState.animateWhen(this.isPouncing(), this.tickCount);
            this.crouchAnimationState.animateWhen(this.isCrouching(), this.tickCount);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SERVAL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SERVAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SERVAL_DEATH.get();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.SERVAL_FOOD);
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (this.isFood(stack)) {
            this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
        }
        super.usePlayerItem(player, hand, stack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        ServalEntity kitten = ModEntityTypes.SERVAL.get().create(level);
        if (kitten != null && partner instanceof ServalEntity otherParent && this.isTame()) {
            kitten.setOwnerUUID(this.getOwnerUUID());
            kitten.setTame(true, true);
            kitten.setCollarColor(this.random.nextBoolean() ? this.getCollarColor() : otherParent.getCollarColor());
        }
        return kitten;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (stack.getItem() instanceof DyeItem dyeItem) {
                    DyeColor color = dyeItem.getDyeColor();
                    if (color != this.getCollarColor()) {
                        if (!this.level().isClientSide()) {
                            this.setCollarColor(color);
                            stack.consume(1, player);
                            this.setPersistenceRequired();
                        }
                        return InteractionResult.sidedSuccess(this.level().isClientSide());
                    }
                } else if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                    if (!this.level().isClientSide()) {
                        FoodProperties food = stack.getFoodProperties(this);
                        this.heal(food != null ? (float) food.nutrition() : 1.0F);
                        this.usePlayerItem(player, hand, stack);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }

                InteractionResult result = super.mobInteract(player, hand);
                if (!result.consumesAction()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
                return result;
            }
        } else if (this.isFood(stack)) {
            if (!this.level().isClientSide()) {
                this.usePlayerItem(player, hand, stack);
                this.tryToTame(player);
                this.setPersistenceRequired();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) {
            this.setPersistenceRequired();
        }
        return result;
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {
        if (this.avoidPlayersGoal == null) {
            this.avoidPlayersGoal = new ServalAvoidEntityGoal<>(this, Player.class, 16.0F, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.avoidPlayersGoal);
        if (!this.isTame()) {
            this.goalSelector.addGoal(4, this.avoidPlayersGoal);
        }
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    private void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    public boolean isCrouching() {
        return this.entityData.get(DATA_CROUCHING);
    }

    public void setIsCrouching(boolean crouching) {
        this.entityData.set(DATA_CROUCHING, crouching);
    }

    public boolean isPouncing() {
        return this.entityData.get(DATA_POUNCING);
    }

    public void setIsPouncing(boolean pouncing) {
        this.entityData.set(DATA_POUNCING, pouncing);
    }

    @Override
    public boolean isSteppingCarefully() {
        return this.isCrouching() || super.isSteppingCarefully();
    }

    static class ServalAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final ServalEntity serval;

        public ServalAvoidEntityGoal(ServalEntity serval, Class<T> avoidClass, float maxDist,
                                     double walkSpeed, double sprintSpeed) {
            super(serval, avoidClass, maxDist, walkSpeed, sprintSpeed, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.serval = serval;
        }

        @Override
        public boolean canUse() {
            return !this.serval.isTame() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.serval.isTame() && super.canContinueToUse();
        }
    }

    static class ServalTemptGoal extends TemptGoal {
        @Nullable
        private Player selectedPlayer;
        private final ServalEntity serval;

        public ServalTemptGoal(ServalEntity serval, double speed, Predicate<ItemStack> items, boolean canScare) {
            super(serval, speed, items, canScare);
            this.serval = serval;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.selectedPlayer == null && this.mob.getRandom().nextInt(this.adjustedTickDelay(600)) == 0) {
                this.selectedPlayer = this.player;
            } else if (this.mob.getRandom().nextInt(this.adjustedTickDelay(500)) == 0) {
                this.selectedPlayer = null;
            }
        }

        @Override
        protected boolean canScare() {
            return (this.selectedPlayer == null || !this.selectedPlayer.equals(this.player)) && super.canScare();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.serval.isTame();
        }
    }

    static class ServalAttackGoal extends Goal {
        private static final double TROT_SPEED = 0.8;
        private static final double STALK_SPEED = 0.6;
        private static final double DASH_SPEED = 1.33;
        private static final double STALK_RANGE_SQR = 10.0 * 10.0;
        private static final double DASH_RANGE_SQR = 3.0 * 3.0;
        private static final double GIVE_UP_RANGE_SQR = 15.0 * 15.0;
        private static final int BITE_INTERVAL = 20;

        private final ServalEntity serval;
        @Nullable
        private LivingEntity target;
        private int biteCooldown;
        private int repathTime;

        public ServalAttackGoal(ServalEntity serval) {
            this.serval = serval;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity candidate = serval.getTarget();
            if (candidate == null || !candidate.isAlive()) {
                return false;
            }
            this.target = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                && this.target.isAlive()
                && serval.getTarget() == this.target
                && serval.distanceToSqr(this.target) <= GIVE_UP_RANGE_SQR;
        }

        @Override
        public void start() {
            this.repathTime = 0;
        }

        @Override
        public void stop() {
            this.target = null;
            serval.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }
            serval.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            double distSqr = serval.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            double reach = serval.getBbWidth() * 2.0;
            double reachSqr = reach * reach;

            double speed;
            if (distSqr <= DASH_RANGE_SQR) {
                speed = DASH_SPEED;
            } else if (distSqr <= STALK_RANGE_SQR) {
                speed = STALK_SPEED;
            } else {
                speed = TROT_SPEED;
            }

            if (--this.repathTime <= 0) {
                this.repathTime = 4 + serval.getRandom().nextInt(7);
                serval.getNavigation().moveTo(this.target, speed);
            }
            serval.getNavigation().setSpeedModifier(speed);

            this.biteCooldown = Math.max(this.biteCooldown - 1, 0);
            if (distSqr <= reachSqr && this.biteCooldown == 0) {
                this.biteCooldown = BITE_INTERVAL;
                serval.doHurtTarget(this.target);
            }
        }
    }

    static class ServalPounceGoal extends JumpGoal {
        private static final int CROUCH_TICKS = 20;
        private static final int COOLDOWN_TICKS = 60;
        private static final int MIN_AIRTIME_TICKS = 5;
        private static final double MIN_RANGE_SQR = 2.0 * 2.0;
        private static final double MAX_RANGE_SQR = 5.0 * 5.0;
        private static final double MAX_CROUCH_RANGE_SQR = MAX_RANGE_SQR * 1.5;
        private static final double MAX_HEIGHT_DIFF = 2.5;
        private static final double LEAP_Y = 0.85;
        private static final double LEAP_XZ_PER_BLOCK = 0.11;
        private static final double LEAP_XZ_MIN = 0.3;
        private static final double LEAP_XZ_MAX = 0.7;

        private final ServalEntity serval;
        private int crouchTime;
        private int leapTicks;
        private int nextPounceTick;
        private boolean leaping;
        private boolean hasHit;

        public ServalPounceGoal(ServalEntity serval) {
            this.serval = serval;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (serval.isTame() || serval.tickCount < nextPounceTick || !serval.onGround()) {
                return false;
            }
            LivingEntity target = serval.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double distSqr = serval.distanceToSqr(target);
            return distSqr >= MIN_RANGE_SQR
                && distSqr <= MAX_RANGE_SQR
                && Math.abs(target.getY() - serval.getY()) <= MAX_HEIGHT_DIFF
                && serval.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            if (leaping) {
                return !(leapTicks > MIN_AIRTIME_TICKS && serval.onGround());
            }
            LivingEntity target = serval.getTarget();
            return target != null
                && target.isAlive()
                && serval.distanceToSqr(target) <= MAX_CROUCH_RANGE_SQR
                && serval.hasLineOfSight(target);
        }

        @Override
        public boolean isInterruptable() {
            return !leaping;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            crouchTime = 0;
            leapTicks = 0;
            leaping = false;
            hasHit = false;
            serval.getNavigation().stop();
            serval.setIsCrouching(true);
        }

        @Override
        public void tick() {
            LivingEntity target = serval.getTarget();
            if (target != null) {
                serval.getLookControl().setLookAt(target, 60.0F, 30.0F);
            }

            if (!leaping) {
                serval.getNavigation().stop();
                if (++crouchTime >= CROUCH_TICKS && target != null) {
                    leap(target);
                }
                return;
            }

            leapTicks++;

            Vec3 vel = serval.getDeltaMovement();
            if (vel.lengthSqr() > 1.0E-4) {
                double pitch = Math.signum(-vel.y)
                    * Math.acos(Mth.clamp(vel.horizontalDistance() / vel.length(), -1.0, 1.0))
                    * (180.0 / Math.PI);
                serval.setXRot((float) pitch);
            }

            if (!hasHit && target != null
                && serval.getBoundingBox().inflate(0.3).intersects(target.getBoundingBox())) {
                serval.doHurtTarget(target);
                hasHit = true;
            }
        }

        private void leap(LivingEntity target) {
            Vec3 flat = new Vec3(target.getX() - serval.getX(), 0.0, target.getZ() - serval.getZ());
            double dist = flat.length();
            Vec3 dir = dist > 1.0E-4 ? flat.scale(1.0 / dist) : Vec3.ZERO;
            double horizontal = Mth.clamp(dist * LEAP_XZ_PER_BLOCK, LEAP_XZ_MIN, LEAP_XZ_MAX);
            serval.setDeltaMovement(dir.x * horizontal, LEAP_Y, dir.z * horizontal);

            leaping = true;
            serval.setIsCrouching(false);
            serval.setIsPouncing(true);
            serval.setJumping(true);
        }

        @Override
        public void stop() {
            serval.setIsCrouching(false);
            serval.setIsPouncing(false);
            serval.setXRot(0.0F);
            nextPounceTick = serval.tickCount + COOLDOWN_TICKS;
            leaping = false;
        }
    }
}