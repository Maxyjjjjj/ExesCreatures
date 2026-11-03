package com.execcexrrvycvtvtv.exes_creatures.misc;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> SAOLA_SPAWNABLE_ON = create("saola_spawnable_on");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, name));
        }
    }

    public static class EntityTypes {

        public static final TagKey<EntityType<?>> SAOLA_AVOID = create("saola_avoid");
        public static final TagKey<EntityType<?>> SERVAL_PREY = create("serval_prey");
        public static final TagKey<EntityType<?>> PHILIPPINE_EAGLE_PREY = create("philippine_eagle_prey");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> SAOLA_FOOD = create("saola_food");
        public static final TagKey<Item> SERVAL_FOOD = create("serval_food");
        public static final TagKey<Item> JAY_FOOD = create("jay_food");
        public static final TagKey<Item> MONKEY_FOOD = create("monkey_food");
        public static final TagKey<Item> EAGLE_FOOD = create("eagle_food");

        public static final TagKey<Item> JAY_WEAPONS = create("jay_weapons");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, name));
        }
    }
}
