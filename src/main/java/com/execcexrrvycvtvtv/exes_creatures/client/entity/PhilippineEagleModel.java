package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.PhilippineEagleEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PhilippineEagleModel<T extends PhilippineEagleEntity> extends AgeableHierarchicalModel<T> {
    private static final float FLAP_SPEED = 0.45F;
    private static final float FLAP_AMPLITUDE = 0.6F;

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "philippine_eagle"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart head_base;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingright;
    private final ModelPart legleft;
    private final ModelPart legright;

    public PhilippineEagleModel(ModelPart root) {
        super(0.5F, 24.0F);
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.tail = this.body.getChild("tail");
        this.head_base = this.body.getChild("head_base");
        this.head = this.head_base.getChild("head");
        this.wingleft = this.body.getChild("wingleft");
        this.wingright = this.body.getChild("wingright");
        this.legleft = this.base.getChild("legleft");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(40, 0).addBox(-3.5F, -3.0F, -6.0F, 7.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 56).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(40, 19).addBox(-5.0F, 0.0F, 1.0F, 10.0F, 0.0F, 9.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, 6.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition head_base = body.addOrReplaceChild("head_base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -6.0F));

        PartDefinition head = head_base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(32, 56).addBox(-0.5F, -7.0F, -5.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(30, 63).addBox(0.0F, -5.0F, -5.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 67).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -1.0F, 0.1309F, -0.9599F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(66, 28).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -6.0F, -1.0F, 0.0F, -0.0873F, 0.0F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 67).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -1.0F, 0.1309F, 0.9599F, 0.0F));

        PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 66).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -6.0F, -1.0F, 0.0F, 0.0873F, 0.0F));

        PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(13, 71).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, 0.9599F, 0.0F, 0.0F));

        PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 63).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(40, 45).addBox(0.0F, -1.0F, -1.0F, 1.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 28).addBox(0.5F, -4.0F, -1.0F, 0.0F, 8.0F, 20.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(3.5F, -1.0F, -5.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingright = body.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(40, 28).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-0.5F, -4.0F, -1.0F, 0.0F, 8.0F, 20.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-3.5F, -1.0F, -5.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(64, 62).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.025F))
            .texOffs(66, 35).addBox(-0.5F, 3.0F, 0.5F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.025F))
            .texOffs(32, 62).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -6.0F, 0.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(16, 66).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.025F))
            .texOffs(30, 65).addBox(-0.5F, 3.0F, 0.5F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.025F))
            .texOffs(48, 62).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(PhilippineEagleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head_base.xRot = headPitch * ((float)Math.PI / 180F);
        this.head_base.yRot = netHeadYaw * ((float)Math.PI / 180F);

        animate(entity.flyAnimationState, PhilippineEagleAnimations.FLY, ageInTicks);
        animate(entity.sitAnimationState, PhilippineEagleAnimations.SIT, ageInTicks);
        animate(entity.hoverAnimationState, PhilippineEagleAnimations.HOVER, ageInTicks);

        if (!entity.isInSittingPose() || !entity.isFlying()) {
            this.legleft.xRot += Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.legright.xRot += Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        } else if (!entity.isInSittingPose() && entity.isFlying()) {
            this.applyStatic(PhilippineEagleAnimations.FLY);

            float strength = entity.getFlapStrength();
            if (strength > 0.01F) {
                float t = ageInTicks * FLAP_SPEED;
                float down = (Mth.sin(t) + 0.2F * Mth.sin(2.0F * t)) * FLAP_AMPLITUDE * strength;
                this.wingleft.zRot += down;
                this.wingright.zRot -= down;
            }
        }
    }

    @Override
    public ModelPart root() {
        return base;
    }
}
