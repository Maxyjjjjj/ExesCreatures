package com.execcexrrvycvtvtv.exes_creatures.datagen;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends EntityTypeTagsProvider {
    public ModEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExesCreatures.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.EntityTypes.SAOLA_AVOID)
            .addTag(EntityTypeTags.RAIDERS)
            .add(EntityType.VEX)
            .add(EntityType.WOLF);
        tag(ModTags.EntityTypes.SERVAL_PREY)
            .add(EntityType.CHICKEN)
            .add(EntityType.RABBIT);
        tag(ModTags.EntityTypes.PHILIPPINE_EAGLE_PREY)
            .add(EntityType.CHICKEN)
            .add(ModEntityTypes.MONKEY.get());
    }
}
