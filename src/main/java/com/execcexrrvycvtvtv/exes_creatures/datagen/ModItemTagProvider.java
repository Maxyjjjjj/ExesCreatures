package com.execcexrrvycvtvtv.exes_creatures.datagen;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.ItemTags.*;
import static net.minecraft.world.item.Items.*;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ExesCreatures.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.SAOLA_FOOD)
            .add(BAMBOO)
            .add(FERN)
            .add(LARGE_FERN);
        tag(ModTags.Items.SERVAL_FOOD)
            .add(CHICKEN)
            .add(RABBIT);
        tag(ModTags.Items.JAY_FOOD)
            .add(SWEET_BERRIES);
        tag(ModTags.Items.MONKEY_FOOD)
            .add(SWEET_BERRIES);
        tag(ModTags.Items.EAGLE_FOOD)
            .addTag(MEAT);

        tag(ModTags.Items.JAY_WEAPONS)
            .addTag(SWORDS)
            .addTag(PICKAXES)
            .addTag(AXES)
            .addTag(HOES)
            .addTag(SHOVELS);
    }
}
