package com.execcexrrvycvtvtv.exes_creatures.client.entity;

import com.execcexrrvycvtvtv.exes_creatures.ExesCreatures;
import com.execcexrrvycvtvtv.exes_creatures.entity.PhilippineEagleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PhilippineEagleRenderer extends MobRenderer<PhilippineEagleEntity, PhilippineEagleModel<PhilippineEagleEntity>> {
    public PhilippineEagleRenderer(EntityRendererProvider.Context context) {
        super(context, new PhilippineEagleModel<>(context.bakeLayer(PhilippineEagleModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(PhilippineEagleEntity philippineEagleEntity) {
        if (philippineEagleEntity.isAggressive()) {
            return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/philippine_eagle/philippine_eagle_angry.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(ExesCreatures.MODID, "textures/entity/philippine_eagle/philippine_eagle.png");
        }
    }
}
