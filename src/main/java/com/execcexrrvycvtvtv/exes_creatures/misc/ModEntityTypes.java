package com.execcexrrvycvtvtv.exes_creatures.misc;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ExesCreatures.MODID);

    public static final Supplier<EntityType<SaolaEntity>> SAOLA =
        ENTITY_TYPES.register("saola", () -> EntityType.Builder.of(SaolaEntity::new, MobCategory.CREATURE)
            .sized(1.2F, 1.4F).eyeHeight(1.325F).passengerAttachments(0.75F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH).build("saola"));
    public static final Supplier<EntityType<ServalEntity>> SERVAL =
        ENTITY_TYPES.register("serval", () -> EntityType.Builder.of(ServalEntity::new, MobCategory.CREATURE)
            .sized(0.7F, 0.875F).eyeHeight(0.875F).build("serval"));
    public static final Supplier<EntityType<JayEntity>> JAY =
        ENTITY_TYPES.register("jay", () -> EntityType.Builder.of(JayEntity::new, MobCategory.CREATURE)
            .sized(0.35F, 0.6F).eyeHeight(0.5F).immuneTo(Blocks.SWEET_BERRY_BUSH).build("jay"));
    public static final Supplier<EntityType<MonkeyEntity>> MONKEY =
        ENTITY_TYPES.register("monkey", () -> EntityType.Builder.of(MonkeyEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 1.25F).eyeHeight(1.1F).immuneTo(Blocks.SWEET_BERRY_BUSH).build("monkey"));
    public static final Supplier<EntityType<PhilippineEagleEntity>> PHILIPPINE_EAGLE =
        ENTITY_TYPES.register("philippine_eagle", () -> EntityType.Builder.of(PhilippineEagleEntity::new, MobCategory.CREATURE)
            .sized(1F, 1.2F).eyeHeight(1.1F).build("philippine_eagle"));

    private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
