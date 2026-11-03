package com.execcexrrvycvtvtv.exes_creatures.datagen;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExesCreatures.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.SAOLA_SPAWNABLE_ON)
            .addTag(BlockTags.ANIMALS_SPAWNABLE_ON)
            .add(Blocks.PODZOL);
    }
}
