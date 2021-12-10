package net.witixin.igloof.client;

import net.minecraft.resources.ResourceLocation;
import net.witixin.igloof.Reference;
import net.witixin.igloof.entity.EntityIgloof;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelIgloof extends AnimatedGeoModel<EntityIgloof> {
    @Override
    public ResourceLocation getModelLocation(EntityIgloof object)
    {
        return Reference.rl("geo/igloofy.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityIgloof object)
    {
        return (!object.isIcey() ? Reference.rl("textures/igloof.png") : Reference.rl("textures/ice_igloof.png"));
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityIgloof object)
    {
        return Reference.rl("animations/igloof.animation.json");
    }
}
