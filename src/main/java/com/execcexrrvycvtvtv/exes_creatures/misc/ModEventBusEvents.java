package com.execcexrrvycvtvtv.exes_creatures.misc;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.client.entity.*;
import com.execcexrrvycvtvtv.exes_creatures.client.layer.JayOnShoulderLayer;
import com.execcexrrvycvtvtv.exes_creatures.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = ExesCreatures.MODID)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SaolaModel.LAYER_LOCATION, SaolaModel::createBodyLayer);
        event.registerLayerDefinition(ServalModel.LAYER_LOCATION, ServalModel::createBodyLayer);
        event.registerLayerDefinition(JayModel.LAYER_LOCATION, JayModel::createBodyLayer);
        event.registerLayerDefinition(MonkeyModel.LAYER_LOCATION, MonkeyModel::createBodyLayer);
        event.registerLayerDefinition(PhilippineEagleModel.LAYER_LOCATION, PhilippineEagleModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.SAOLA.get(), SaolaEntity.createAttributes().build());
        event.put(ModEntityTypes.SERVAL.get(), ServalEntity.createAttributes().build());
        event.put(ModEntityTypes.JAY.get(), JayEntity.createAttributes().build());
        event.put(ModEntityTypes.MONKEY.get(), MonkeyEntity.createAttributes().build());
        event.put(ModEntityTypes.PHILIPPINE_EAGLE.get(), PhilippineEagleEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void registerSpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntityTypes.SAOLA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SaolaEntity::checkSaolaSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.SERVAL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.JAY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, JayEntity::checkJaySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.MONKEY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MonkeyEntity::checkMonkeySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.PHILIPPINE_EAGLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PhilippineEagleEntity::checkPEagleSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer defaultRenderer = event.getSkin(PlayerSkin.Model.WIDE);
        if (defaultRenderer != null) {
            defaultRenderer.addLayer(new JayOnShoulderLayer<>(defaultRenderer, event.getEntityModels()));
        }
        PlayerRenderer slimRenderer = event.getSkin(PlayerSkin.Model.SLIM);
        if (slimRenderer != null) {
            slimRenderer.addLayer(new JayOnShoulderLayer<>(slimRenderer, event.getEntityModels()));
        }
    }
}
