package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.MonkeyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class MonkeyRenderer extends MobRenderer<MonkeyEntity, MonkeyModel<MonkeyEntity>> {
    public MonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new MonkeyModel<>(context.bakeLayer(MonkeyModel.LAYER_LOCATION)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(MonkeyEntity monkey) {
        return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/monkey.png");
    }
}
