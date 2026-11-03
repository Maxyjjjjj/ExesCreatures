package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.JayEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class JayRenderer extends MobRenderer<JayEntity, JayModel<JayEntity>> {
    public JayRenderer(EntityRendererProvider.Context context) {
        super(context, new JayModel<>(context.bakeLayer(JayModel.LAYER_LOCATION)), 0.3f);
        addLayer(new JayHeldItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(JayEntity jay) {
        return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/jay.png");
    }

    private static class JayHeldItemLayer extends RenderLayer<JayEntity, JayModel<JayEntity>> {
        private final ItemInHandRenderer itemInHandRenderer;

        public JayHeldItemLayer(JayRenderer jayRenderer, ItemInHandRenderer itemInHandRenderer) {
            super(jayRenderer);
            this.itemInHandRenderer = itemInHandRenderer;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, JayEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            poseStack.pushPose();

            JayModel<JayEntity> model = this.getParentModel();

            model.translateToBeak(poseStack);

            if (entity.isBaby()) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            this.itemInHandRenderer.renderItem(entity, itemstack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
