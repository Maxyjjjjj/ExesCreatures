package com.execcexrrvycvtvtv.exes_creatures.worldgen;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> SPAWN_SAOLA = registerKey("spawn_saola");
    public static final ResourceKey<BiomeModifier> SPAWN_SERVAL = registerKey("spawn_serval");
    public static final ResourceKey<BiomeModifier> SPAWN_JAY = registerKey("spawn_jay");
    public static final ResourceKey<BiomeModifier> SPAWN_MONKEY = registerKey("spawn_monkey");
    public static final ResourceKey<BiomeModifier> SPAWN_PHILIPPINE_EAGLE = registerKey("spawn_philippine_eagle");

    public static void bootstrap(BootstrapContext<BiomeModifier> context){
        var biomes = context.lookup(Registries.BIOME);

        context.register(SPAWN_SAOLA, new BiomeModifiers.AddSpawnsBiomeModifier(
            HolderSet.direct(biomes.getOrThrow(Biomes.BAMBOO_JUNGLE)),
            List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.SAOLA.get(), 2, 1,2))
        ));
        context.register(SPAWN_SERVAL, new BiomeModifiers.AddSpawnsBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_SAVANNA),
            List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.SERVAL.get(), 5, 1,2))
        ));
        context.register(SPAWN_JAY, new BiomeModifiers.AddSpawnsBiomeModifier(
            biomes.getOrThrow(Tags.Biomes.IS_DECIDUOUS_TREE),
            List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.JAY.get(), 4, 2,6))
        ));
        context.register(SPAWN_MONKEY, new BiomeModifiers.AddSpawnsBiomeModifier(
            biomes.getOrThrow(Tags.Biomes.IS_CONIFEROUS_TREE),
            List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.MONKEY.get(), 6, 2,6))
        ));
        context.register(SPAWN_PHILIPPINE_EAGLE, new BiomeModifiers.AddSpawnsBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_JUNGLE),
            List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.PHILIPPINE_EAGLE.get(), 3, 1,1))
        ));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, name));
    }
}
