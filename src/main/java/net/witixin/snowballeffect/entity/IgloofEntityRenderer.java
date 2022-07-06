package net.witixin.snowballeffect.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.witixin.snowballeffect.client.igloof.IgloofLayerRenderer;
import net.witixin.snowballeffect.client.igloof.ModelIgloof;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class IgloofEntityRenderer extends GeoEntityRenderer<EntityIgloof> {

    public IgloofEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelIgloof());
        //this.addLayer(new IgloofLayerRenderer(this));
    }
    @Override
    public RenderType getRenderType(EntityIgloof animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        animatable.refreshDimensions();
        stack.scale(animatable.getSize(), animatable.getSize(), animatable.getSize());
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
