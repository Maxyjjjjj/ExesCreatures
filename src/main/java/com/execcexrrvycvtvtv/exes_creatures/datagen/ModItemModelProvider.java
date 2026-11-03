package com.execcexrrvycvtvtv.exes_creatures.datagen;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExesCreatures.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        spawnEggItem(ModItems.SAOLA_SPAWN_EGG.get());
        spawnEggItem(ModItems.SERVAL_SPAWN_EGG.get());
        spawnEggItem(ModItems.JAY_SPAWN_EGG.get());
        spawnEggItem(ModItems.MONKEY_SPAWN_EGG.get());
        spawnEggItem(ModItems.PHILIPPINE_EAGLE_SPAWN_EGG.get());
    }
}
