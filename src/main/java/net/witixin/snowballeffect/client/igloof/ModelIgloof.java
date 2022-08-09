package net.witixin.snowballeffect.client.igloof;

import net.minecraft.resources.ResourceLocation;
import net.witixin.snowballeffect.Reference;
import net.witixin.snowballeffect.entity.EntityIgloof;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelIgloof extends AnimatedGeoModel<EntityIgloof> {
    
    private static final ResourceLocation MODEL_LOCATION = Reference.rl("geo/igloofy.geo.json");
    private static final ResourceLocation ANIM_LOCATION = Reference.rl("animations/igloof.animation.json");
    private static final ResourceLocation ICEY_TEXTURE_LOCATION = Reference.rl("textures/ice_igloof.png"));
    private static final ResourceLocation REGULAR_TEXTURE_LOCATION = Reference.rl("textures/igloof.png");

    @Override
    public ResourceLocation getModelLocation(EntityIgloof object)
    {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityIgloof object)
    {
        return (object.isIcey() ? ICEY_TEXTURE_LOCATION : REGULAR_TEXTURE_LOCATION);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityIgloof object)
    {
        return ANIM_LOCATION;
    }
}
