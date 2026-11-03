package com.execcexrrvycvtvtv.exes_creatures.client.layer;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.client.entity.JayModel;
import com.execcexrrvycvtvtv.exes_creatures.misc.ModEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JayOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {

    private final JayModel<?> model;

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/jay.png");

    public JayOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new JayModel<>(modelSet.bakeLayer(JayModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderShoulder(poseStack, bufferSource, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        this.renderShoulder(poseStack, bufferSource, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    private void renderShoulder(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T player, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulder) {
        CompoundTag shoulderTag = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.byString(shoulderTag.getString("id"))
            .filter(type -> type == ModEntityTypes.JAY.get())
            .ifPresent(type -> {
                poseStack.pushPose();
                poseStack.translate(leftShoulder ? 0.4F : -0.4F, player.isCrouching() ? -1.3F : -1.5F, 0.0F);
                if (shoulderTag.getInt("Age") < 0) {
                    poseStack.translate(0.0F, 0.15F, 0.0F);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                }
                VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(TEXTURE));
                this.model.renderOnShoulder(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, player.tickCount);
                poseStack.popPose();
            });
    }
}