package com.execcexrrvycvtvtv.exes_creatures.item;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExesCreatures.MODID);

    public static final DeferredItem<DeferredSpawnEggItem> SAOLA_SPAWN_EGG = ITEMS.register("saola_spawn_egg", () ->
        new DeferredSpawnEggItem(ModEntityTypes.SAOLA, 0x5d4125, 0xc7c7c7, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> SERVAL_SPAWN_EGG = ITEMS.register("serval_spawn_egg", () ->
        new DeferredSpawnEggItem(ModEntityTypes.SERVAL, 0xd3a961, 0x171717, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> JAY_SPAWN_EGG = ITEMS.register("jay_spawn_egg", () ->
        new DeferredSpawnEggItem(ModEntityTypes.JAY, 0xab8e76, 0x65a3c5, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> MONKEY_SPAWN_EGG = ITEMS.register("monkey_spawn_egg", () ->
        new DeferredSpawnEggItem(ModEntityTypes.MONKEY, 0xa45c3a, 0x72a3c1, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> PHILIPPINE_EAGLE_SPAWN_EGG = ITEMS.register("philippine_eagle_spawn_egg", () ->
        new DeferredSpawnEggItem(ModEntityTypes.PHILIPPINE_EAGLE, 0x403a36, 0xe5e5e5, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
