package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.MonkeyEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Collections;

public class MonkeyModel<T extends MonkeyEntity> extends AgeableListModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "monkey"), "main");
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart tail;
    private final ModelPart legleft;
    private final ModelPart legright;

    public MonkeyModel(ModelPart root) {
        super(true, 5F, 0F);

        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.armleft = this.body.getChild("armleft");
        this.armright = this.body.getChild("armright");
        this.tail = this.body.getChild("tail");
        this.legleft = root.getChild("legleft");
        this.legright = root.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 12).addBox(-3.5F, -6.0F, -2.5F, 7.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(24, 13).addBox(-4.0F, -4.5F, -0.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 16).addBox(-1.5F, -3.0F, -3.25F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 23).addBox(-3.0F, -8.0F, -2.0F, 6.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition armleft = body.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(20, 23).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offset(3.5F, -8.0F, -0.5F));

        PartDefinition armright = body.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offset(-3.5F, -8.0F, -0.5F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, -0.6109F, 0.0F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(32, 16).addBox(-1.75F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 18.0F, 0.0F));

        PartDefinition legright = partdefinition.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(32, 25).addBox(-1.25F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.75F, 18.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(MonkeyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.resetPose();
        this.body.getAllParts().forEach(ModelPart::resetPose);
        this.legleft.resetPose();
        this.legright.resetPose();

        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        float idleSpeed = ageInTicks * 0.05F;

        this.armright.xRot += (float) (Math.cos(idleSpeed) * 0.05F);
        this.armright.zRot += (float) (Math.sin(idleSpeed) * 0.02F);

        this.armleft.xRot -= (float) (Math.cos(idleSpeed) * 0.05F);
        this.armleft.zRot -= (float) (Math.sin(idleSpeed) * 0.02F);

        this.legleft.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        if (!entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && entity.getItemBySlot(EquipmentSlot.MAINHAND).isDamaged() && entity.getItemBySlot(EquipmentSlot.MAINHAND).isDamageableItem() && entity.hasTrustedPlayerNearby()) {
            armleft.xRot = 52.5F * Mth.DEG_TO_RAD;
            armright.xRot = 52.5F * Mth.DEG_TO_RAD;

            armleft.zRot = 30F * Mth.DEG_TO_RAD;
            armright.zRot = -30F * Mth.DEG_TO_RAD;

            armleft.yRot = 2.5F * Mth.DEG_TO_RAD;
            armright.yRot = -2.5F * Mth.DEG_TO_RAD;
        } else {
            this.armleft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.armright.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return Collections.singleton(head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, legleft, legright);
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {

        this.body.translateAndRotate(poseStack);

        ModelPart arm = humanoidArm == HumanoidArm.RIGHT
            ? this.armright
            : this.armleft;

        arm.translateAndRotate(poseStack);

        poseStack.translate(0.0F, 0.5F, 0.0F);
    }
}
