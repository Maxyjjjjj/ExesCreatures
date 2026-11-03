package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.SaolaEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SaolaModel<T extends SaolaEntity> extends AgeableHierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "saola"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck_base;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legrightback;
    private final ModelPart legleftback;
    private final ModelPart halo;

    public SaolaModel(ModelPart root) {
        super(0.5F, 24.0F);
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck_base = this.body.getChild("neck_base");
        this.neck = this.neck_base.getChild("neck");
        this.head = this.neck.getChild("head");
        this.horns = this.head.getChild("horns");
        this.halo = this.head.getChild("halo");
        this.legleftfront = this.base.getChild("legleftfront");
        this.legrightfront = this.base.getChild("legrightfront");
        this.legrightback = this.base.getChild("legrightback");
        this.legleftback = this.base.getChild("legleftback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -7.0F, 8.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 42).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition neck_base = body.addOrReplaceChild("neck_base", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

        PartDefinition neck = neck_base.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(20, 38).addBox(-1.5F, -7.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, -3.75F, -3.75F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(40, 24).addBox(-1.5F, -3.75F, -6.75F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(44, 31).addBox(1.5F, -4.0F, 1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(44, 33).addBox(-3.5F, -4.0F, 1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.5F, -0.15F));

        PartDefinition horns = head.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(20, 22).addBox(0.5F, -0.75F, -0.75F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32).addBox(-1.5F, -0.75F, -0.75F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.65F, 0.1309F, 0.0F, 0.0F));

        PartDefinition legleftfront = base.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(44, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -9.0F, -5.0F));

        PartDefinition legrightfront = base.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(44, 12).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -9.0F, -5.0F));

        PartDefinition legrightback = base.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(34, 38).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -9.0F, 6.0F));

        PartDefinition legleftback = base.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 42).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -9.0F, 6.0F));

        PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(20, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, -10.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SaolaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.neck_base.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.legrightback.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.legleftfront.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.halo.yRot = ageInTicks * 0.1f;
        this.halo.y = -6f + (Mth.sin(ageInTicks * 0.05f) * 0.2f);

        this.horns.visible = !entity.isBaby();
    }

    @Override
    public ModelPart root() {
        return base;
    }
}