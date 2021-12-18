package net.witixin.snowballeffect.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.witixin.snowballeffect.entity.EntityIgloof;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagicCoalItem extends Item {
    public MagicCoalItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof EntityIgloof){
            if (((EntityIgloof)entity).isIcey()){
                ((EntityIgloof)entity).setIcey(false);
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(new TextComponent("Seems to react oddly around snowy blocks...").setStyle(Style.EMPTY.withColor(TextColor.parseColor("yellow"))));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (EntityIgloof.matchesSnow(ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock())){
            EntityIgloof entityIgloof = EntityIgloof.of(ctx.getLevel(), ctx.getPlayer());
            entityIgloof.setPos(ctx.getClickLocation());
            ctx.getLevel().destroyBlock(ctx.getClickedPos(), true);
            ctx.getLevel().addFreshEntity(entityIgloof);
            ctx.getItemInHand().setCount(0);
        }
        return super.useOn(ctx);
    }
}
