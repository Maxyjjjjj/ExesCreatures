package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.JayEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class JayModel<T extends JayEntity> extends AgeableHierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "jay"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart wingright;
    private final ModelPart wingleft;
    final ModelPart head;
    private final ModelPart legright;
    private final ModelPart legleft;

    public JayModel(ModelPart root) {
        super(0.5F, 24.0F);
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.tail = this.body.getChild("tail");
        this.wingright = this.body.getChild("wingright");
        this.wingleft = this.body.getChild("wingleft");
        this.head = this.body.getChild("head");
        this.legright = this.base.getChild("legright");
        this.legleft = this.base.getChild("legleft");
    }

    @Override
    public ModelPart root() {
        return base;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(18, 10).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, -0.5F, 2.0F));

        PartDefinition wingright = body.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(0, 13).addBox(0.05F, -2.5F, -0.5F, 0.0F, 4.0F, 9.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-2.05F, 0.0F, -2.0F, 0.0F, -0.0175F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(0, 0).addBox(-0.05F, -2.5F, -0.5F, 0.0F, 4.0F, 9.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(2.05F, 0.0F, -2.0F, 0.0F, 0.0175F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 26).addBox(-0.5F, -3.25F, -3.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(18, 17).addBox(-1.5F, -4.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.1745F, 0.0F, 0.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(18, 25).addBox(-1.5F, 0.0F, -1.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 2.0F, 0.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(0, 26).addBox(-0.5F, 0.0F, -1.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void renderOnShoulder(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, int tickCount) {
        this.setupAnim(State.ON_SHOULDER, tickCount, limbSwing, limbSwingAmount, tickCount, netHeadYaw, headPitch);
        this.root().render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private void prepare(State state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    public void setupAnim(State state, float tickCount, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.prepare(state);

        this.head.yRot += netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot += headPitch * ((float)Math.PI / 180F);

        switch (state) {

            case STANDING -> {
                float walkSpeed = 1.8F;
                float walkDegree = 1.2F;

                this.legleft.xRot += Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;

                this.legright.xRot += Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
            }

            case FLYING -> {
                float flapSpeed = 1.8F;
                float flap = Mth.sin(ageInTicks * flapSpeed);

                this.wingright.zRot = flap * 1.45F;
                this.wingleft.zRot = -flap * 1.45F;

                this.applyStatic(JayAnimations.FLY);
            }

            case HOVERING -> {
                float flapSpeed = 1.8F;
                float flap = Mth.sin(ageInTicks * flapSpeed);

                this.wingright.zRot = flap * 1.45F;
                this.wingleft.zRot = -flap * 1.45F;

                this.applyStatic(JayAnimations.HOVER);
            }

            case SITTING -> this.applyAnimation(JayAnimations.SIT, ageInTicks);

            case ON_SHOULDER -> this.applyAnimation(JayAnimations.SHOULDER, ageInTicks);
        }
    }

    private void applyAnimation(AnimationDefinition animation, float ageInTicks) {
        AnimationState animationState = new AnimationState();
        animationState.start(0);
        this.animate(animationState, animation, ageInTicks);
    }

    public void translateToBeak(PoseStack poseStack) {
        this.base.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);

        poseStack.translate(0.0F, -2.75F / 16.0F, -3.0F / 16.0F);
    }

    @Override
    public void setupAnim(JayEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setupAnim(getState(entity), entity.tickCount, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private static State getState(JayEntity jay) {

        if (jay.isInSittingPose()) {
            return State.SITTING;
        }

        if (jay.isFlying()) {

            double horizontalSpeed =
                jay.getDeltaMovement().horizontalDistanceSqr();

            if (horizontalSpeed < 0.0025D) {
                return State.HOVERING;
            }

            return State.FLYING;
        }

        return State.STANDING;
    }

    @OnlyIn(Dist.CLIENT)
    public enum State {
        FLYING,
        STANDING,
        SITTING,
        HOVERING,
        ON_SHOULDER
    }
}
