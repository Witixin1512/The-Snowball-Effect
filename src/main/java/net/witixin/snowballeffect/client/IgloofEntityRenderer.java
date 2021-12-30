package net.witixin.snowballeffect.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.witixin.snowballeffect.entity.EntityIgloof;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class IgloofEntityRenderer extends GeoEntityRenderer<EntityIgloof> {

    public IgloofEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ModelIgloof());
    }

    @Override
    public RenderType getRenderType(EntityIgloof animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        animatable.refreshDimensions();
        stack.scale(animatable.getSize(), animatable.getSize(), animatable.getSize());
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
