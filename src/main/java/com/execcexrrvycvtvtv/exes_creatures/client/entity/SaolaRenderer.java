package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.SaolaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class SaolaRenderer extends MobRenderer<SaolaEntity, SaolaModel<SaolaEntity>> {
    public SaolaRenderer(EntityRendererProvider.Context context) {
        super(context, new SaolaModel<>(context.bakeLayer(SaolaModel.LAYER_LOCATION)), 0.6f);
        addLayer(new SaolaHaloLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SaolaEntity saolaEntity) {
        return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/saola/saola.png");
    }

    private static class SaolaHaloLayer extends RenderLayer<SaolaEntity, SaolaModel<SaolaEntity>> {
        private static final ResourceLocation HALO_LOCATION = ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/saola/martha.png");

        public SaolaHaloLayer(SaolaRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SaolaEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.hasCustomName() && Objects.equals(entity.getCustomName(), Component.literal("Martha"))) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.beaconBeam(HALO_LOCATION, true));

                int fullBrightLight = 15728880;
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, fullBrightLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            }
        }
    }
}
