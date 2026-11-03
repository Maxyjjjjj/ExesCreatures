package com.execcexrrvycvtvtv.exes_creatures.datagen;

import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags; // <- Додайте цей імпорт
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {

    public ModEntityLootTableProvider(HolderLookup.Provider provider) {
        super(FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    public void generate() {
        add(ModEntityTypes.SAOLA.get(), LootTable.lootTable());
        add(ModEntityTypes.SERVAL.get(), LootTable.lootTable());
        add(ModEntityTypes.JAY.get(), LootTable.lootTable());
        add(ModEntityTypes.MONKEY.get(), LootTable.lootTable());
        add(ModEntityTypes.PHILIPPINE_EAGLE.get(), LootTable.lootTable());
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(
            ModEntityTypes.SAOLA.get(),
            ModEntityTypes.SERVAL.get(),
            ModEntityTypes.JAY.get(),
            ModEntityTypes.MONKEY.get(),
            ModEntityTypes.PHILIPPINE_EAGLE.get()
        );
    }
}
