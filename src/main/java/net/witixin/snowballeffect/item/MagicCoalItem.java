package net.witixin.snowballeffect.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.witixin.snowballeffect.entity.EntityIgloof;

import java.util.List;

public class MagicCoalItem extends Item {
    public MagicCoalItem(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity instanceof EntityIgloof){
            if (((EntityIgloof)entity).isIcey()){
                ((EntityIgloof)entity).setIcey(false);
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, World p_41422_, List<ITextComponent> p_41423_, ITooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(new StringTextComponent("Seems to react oddly around snowy blocks...").setStyle(Style.EMPTY.withColor(Color.parseColor("yellow"))));
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        if (EntityIgloof.matchesSnow(ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock())){
            EntityIgloof entityIgloof = EntityIgloof.of(ctx.getLevel(), ctx.getPlayer());
            entityIgloof.setPos(ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
            ctx.getLevel().destroyBlock(ctx.getClickedPos(), true);
            ctx.getLevel().addFreshEntity(entityIgloof);
            ctx.getItemInHand().setCount(0);
        }
        return super.useOn(ctx);
    }
}
