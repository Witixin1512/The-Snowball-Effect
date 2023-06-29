package net.witixin.snowballeffect.client.igloof;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.witixin.snowballeffect.SnowballEffect;
import net.witixin.snowballeffect.entity.Igloof;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IgloofRenderer extends GeoEntityRenderer<Igloof> {

    public IgloofRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelIgloof(SnowballEffect.rl("igloof")));
        //this.addLayer(new IgloofLayerRenderer(this)); //TODO accessories
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, Igloof animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay){
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
        if (!isReRender) {
            float size = animatable.getSize();
            poseStack.scale(size, size, size);
        }
    }

    @Override
    public RenderType getRenderType(Igloof animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick){
        return RenderType.entityTranslucent(texture);
    }
}
