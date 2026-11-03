package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.ServalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ServalRenderer extends MobRenderer<ServalEntity, ServalModel<ServalEntity>> {
    public ServalRenderer(EntityRendererProvider.Context context) {
        super(context, new ServalModel<>(context.bakeLayer(ServalModel.LAYER_LOCATION)), 0.3125f);
        addLayer(new ServalCollarLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ServalEntity serval) {
        return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/serval/serval.png");
    }

    public static class ServalCollarLayer extends RenderLayer<ServalEntity, ServalModel<ServalEntity>> {
        private static final ResourceLocation SERVAL_COLLAR_LOCATION = ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/serval/serval_collar.png");
        public ServalCollarLayer(ServalRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, ServalEntity serval, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (serval.isTame()) {
                int color = serval.getCollarColor().getTextureDiffuseColor();
                VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SERVAL_COLLAR_LOCATION));
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY, color);
            }
        }
    }
}
