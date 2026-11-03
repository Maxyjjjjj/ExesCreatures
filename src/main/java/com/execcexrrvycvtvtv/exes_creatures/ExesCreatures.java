package com.execcexrrvycvtvtv.exes_creatures;

import com.execcexrrvycvtvtv.exes_creatures.client.entity.*;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.execcexrrvycvtvtv.exes_creatures.item.ModItems;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModSoundEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(ExesCreatures.MODID)
public class ExesCreatures {
    public static final String MODID = "exes_creatures";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExesCreatures(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        ModEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModSoundEvents.register(modEventBus);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.SAOLA_SPAWN_EGG);
            event.accept(ModItems.SERVAL_SPAWN_EGG);
            event.accept(ModItems.JAY_SPAWN_EGG);
            event.accept(ModItems.MONKEY_SPAWN_EGG);
            event.accept(ModItems.PHILIPPINE_EAGLE_SPAWN_EGG);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntityTypes.SAOLA.get(), SaolaRenderer::new);
            EntityRenderers.register(ModEntityTypes.SERVAL.get(), ServalRenderer::new);
            EntityRenderers.register(ModEntityTypes.JAY.get(), JayRenderer::new);
            EntityRenderers.register(ModEntityTypes.MONKEY.get(), MonkeyRenderer::new);
            EntityRenderers.register(ModEntityTypes.PHILIPPINE_EAGLE.get(), PhilippineEagleRenderer::new);
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
