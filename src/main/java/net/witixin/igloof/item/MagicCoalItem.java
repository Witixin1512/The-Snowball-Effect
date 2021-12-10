package net.witixin.igloof.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.witixin.igloof.entity.EntityIgloof;

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
