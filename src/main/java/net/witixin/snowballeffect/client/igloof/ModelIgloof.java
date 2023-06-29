package net.witixin.snowballeffect.client.igloof;

import net.minecraft.resources.ResourceLocation;
import net.witixin.snowballeffect.SnowballEffect;
import net.witixin.snowballeffect.entity.Igloof;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ModelIgloof extends DefaultedEntityGeoModel<Igloof> {
    
    private static final ResourceLocation MODEL_LOCATION = SnowballEffect.rl("geo/igloof.geo.json");
    private static final ResourceLocation ANIM_LOCATION = SnowballEffect.rl("animations/igloof.animation.json");
    private static final ResourceLocation ICEY_TEXTURE_LOCATION = SnowballEffect.rl("textures/entity/ice_igloof.png");
    private static final ResourceLocation REGULAR_TEXTURE_LOCATION = SnowballEffect.rl("textures/entity/igloof.png");

    public ModelIgloof(ResourceLocation assetSubpath){
        super(assetSubpath);
    }

    @Override
    public ResourceLocation getModelResource(Igloof animatable)
    {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Igloof animatable)
    {
        return (animatable.isIcey() ? ICEY_TEXTURE_LOCATION : REGULAR_TEXTURE_LOCATION);
    }

    @Override
    public ResourceLocation getAnimationResource(Igloof animatable)
    {
        return ANIM_LOCATION;
    }
}
