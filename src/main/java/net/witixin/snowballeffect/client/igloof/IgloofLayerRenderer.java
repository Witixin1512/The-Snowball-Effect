package net.witixin.snowballeffect.client.igloof;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.witixin.snowballeffect.Reference;
import net.witixin.snowballeffect.entity.EntityIgloof;
import net.witixin.snowballeffect.entity.EntitySantaHat;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import javax.annotation.Nullable;

import java.awt.*;

import static software.bernie.geckolib3.renderers.geo.GeoEntityRenderer.getPackedOverlay;

public class IgloofLayerRenderer extends GeoLayerRenderer<EntityIgloof> {
    public IgloofLayerRenderer(IGeoRenderer<EntityIgloof> entityRendererIn) {
        super(entityRendererIn);
    }

    private static final ResourceLocation SANTA_HAT_MODEL = Reference.rl("geo/santa_hat.geo.json");
    private static final ResourceLocation SANTA_HAT_TEXTURE = Reference.rl("textures/santa_hat.png");

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityIgloof entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Color renderColor = new Color(255, 255, 255, 255);
        @Nullable IAccessory accessory = getAccessoryInternal(entityLivingBaseIn.getAccessory(), entityLivingBaseIn.level, entityLivingBaseIn);
        if (accessory != null){
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0d, -entityLivingBaseIn.dimensions.height  - getSittingOffset(entityLivingBaseIn), 0.0d);
            this.getRenderer().render(this.getEntityModel().getModel(SANTA_HAT_MODEL), entityLivingBaseIn, partialTicks, RenderType.entityTranslucent(SANTA_HAT_TEXTURE), matrixStackIn, bufferIn, null, packedLightIn, getPackedOverlay(entityLivingBaseIn, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                    (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
            matrixStackIn.popPose();
        }
    }
    private IAccessory getAccessoryInternal(String accessory, Level level, EntityIgloof holder){
        if (accessory.matches("NONE"))return null;
        if (accessory.matches("santa_hat")){
            //return EntitySantaHat.of(level, holder); this will need to hold on to the next update.
            return null;
        }
        return null;
    }
    private double getSittingOffset(EntityIgloof igloof){
        return igloof.isRenderSitting() ? igloof.getAge() * 0.25 : 0;
    }
}
