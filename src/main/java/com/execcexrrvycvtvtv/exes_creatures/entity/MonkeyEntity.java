package com.execcexrrvycvtvtv.exes_creatures.entity;

import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MonkeyEntity extends Animal {
    private static final byte EVENT_NOT_TRUSTED = 40;
    private static final byte EVENT_TRUSTED = 41;
    private static final int REPAIR_INTERVAL = 40;

    private final Set<UUID> trustedPlayers = new HashSet<>();
    private int repairCooldown = 0;

    public MonkeyEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1.2D, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(ModTags.Items.MONKEY_FOOD), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.05D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 14.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.28D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean clientSide = this.level().isClientSide();
        boolean trusted = this.isTrusted(player.getUUID());

        if (this.isFood(stack) && !trusted) {
            if (!clientSide) {
                this.usePlayerItem(player, hand, stack);
                if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                    this.addTrustedPlayer(player.getUUID());
                    this.level().broadcastEntityEvent(this, EVENT_TRUSTED);
                } else {
                    this.level().broadcastEntityEvent(this, EVENT_NOT_TRUSTED);
                }
            }
            return InteractionResult.sidedSuccess(clientSide);
        }

        if (trusted) {
            ItemStack held = this.getMainHandItem();

            if (held.isEmpty() && stack.isDamaged()) {
                if (!clientSide) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, stack.copyWithCount(1));
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    stack.consume(1, player);
                }
                return InteractionResult.sidedSuccess(clientSide);
            }

            if (stack.isEmpty() && !held.isEmpty()) {
                if (!clientSide) {
                    ItemStack returned = held.copy();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (!player.getInventory().add(returned)) {
                        player.drop(returned, false);
                    }
                }
                return InteractionResult.sidedSuccess(clientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_TRUSTED) {
            this.spawnTrustingParticles(true);
        } else if (id == EVENT_NOT_TRUSTED) {
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
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            return;
        }

        ItemStack held = this.getMainHandItem();
        if (!held.isDamaged()) {
            this.repairCooldown = 0;
            return;
        }

        if (++this.repairCooldown >= REPAIR_INTERVAL) {
            this.repairCooldown = 0;
            if (this.hasTrustedPlayerNearby()) {
                held.setDamageValue(held.getDamageValue() - 1);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.6D, this.getZ(), 5, 0.3D, 0.3D, 0.3D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.MONKEY_FOOD);
    }

    public boolean hasTrustedPlayerNearby() {
        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0D));
        for (Player player : players) {
            if (this.isTrusted(player.getUUID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        ListTag trustedList = new ListTag();

        for (UUID uuid : this.trustedPlayers) {
            trustedList.add(NbtUtils.createUUID(uuid));
        }

        compound.put("TrustedPlayers", trustedList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.trustedPlayers.clear();

        if (compound.contains("TrustedPlayers", Tag.TAG_LIST)) {
            ListTag trustedList = compound.getList(
                "TrustedPlayers", Tag.TAG_INT_ARRAY
            );

            for (Tag tag : trustedList) {
                this.trustedPlayers.add(
                    NbtUtils.loadUUID(tag)
                );
            }
        }
    }

    public void inheritTrust(MonkeyEntity parent1, MonkeyEntity parent2) {
        this.trustedPlayers.clear();
        this.trustedPlayers.addAll(parent1.trustedPlayers);
        this.trustedPlayers.addAll(parent2.trustedPlayers);
    }

    public void addTrustedPlayer(UUID uuid) {
        this.trustedPlayers.add(uuid);
    }

    public boolean isTrusted(UUID uuid) {
        return this.trustedPlayers.contains(uuid);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        MonkeyEntity baby = ModEntityTypes.MONKEY.get().create(serverLevel);
        if (baby != null && ageableMob instanceof MonkeyEntity otherParent) {
            baby.inheritTrust(this, otherParent);
        }
        return baby;
    }

    public static boolean checkMonkeySpawnRules(EntityType<MonkeyEntity> monkeyEntityEntityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return serverLevelAccessor.getBlockState(pos.below()).is(BlockTags.FOXES_SPAWNABLE_ON) && isBrightEnoughToSpawn(serverLevelAccessor, pos);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.MONKEY_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.MONKEY_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.MONKEY_DEATH.get();
    }
}