package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.ServalEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ServalModel<T extends ServalEntity> extends AgeableHierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "serval"), "main");
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart backLegL;
    private final ModelPart backLegR;
    private final ModelPart frontLegL;
    private final ModelPart frontLegR;

    public ServalModel(ModelPart root) {
        super(0.5F, 24.0F);
        this.body = root.getChild("body");
        this.belly = this.body.getChild("belly");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.backLegL = this.body.getChild("backLegL");
        this.backLegR = this.body.getChild("backLegR");
        this.frontLegL = this.body.getChild("frontLegL");
        this.frontLegR = this.body.getChild("frontLegR");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -6.0F, 5.0F, 6.0F, 12.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(34, 0).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, -6.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(34, 7).addBox(0.0F, -4.5F, -1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(34, 3).addBox(-2.0F, -4.5F, -1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 5.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition backLegL = body.addOrReplaceChild("backLegL", CubeListBuilder.create().texOffs(8, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, 4.5F));

        PartDefinition backLegR = body.addOrReplaceChild("backLegR", CubeListBuilder.create().texOffs(26, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 3.0F, 4.5F));

        PartDefinition frontLegL = body.addOrReplaceChild("frontLegL", CubeListBuilder.create().texOffs(18, 18).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 1.0F, -4.5F));

        PartDefinition frontLegR = body.addOrReplaceChild("frontLegR", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 1.0F, -4.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return body;
    }

    @Override
    public void setupAnim(ServalEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        if (!entity.isInSittingPose() || !entity.isPouncing()) {
            if (entity.getMoveControl().getSpeedModifier() >= 1.33) {
                this.backLegR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
                this.backLegL.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
                this.frontLegR.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
                this.frontLegL.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
            } else {
                this.backLegR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
                this.backLegL.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
                this.frontLegR.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
                this.frontLegL.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            }
        }
        animate(entity.sitAnimationState, ServalAnimations.SIT, ageInTicks);
        if (entity.isInSittingPose()) {
            body.y += 6F;
            body.xRot = 30F * Mth.DEG_TO_RAD;
        }
        animate(entity.pounceAnimationState, ServalAnimations.POUNCE, ageInTicks);
        if (entity.isPouncing()) {
            body.xRot = 17.5F * Mth.DEG_TO_RAD;
        }
        animate(entity.crouchAnimationState, ServalAnimations.CROUCH, ageInTicks);
    }
}
