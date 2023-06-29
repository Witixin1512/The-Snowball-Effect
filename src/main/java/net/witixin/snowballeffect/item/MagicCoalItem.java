package net.witixin.snowballeffect.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.witixin.snowballeffect.entity.Igloof;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagicCoalItem extends Item {
    public MagicCoalItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!player.level().isClientSide && entity instanceof Igloof igloof){
            if (igloof.isIcey()) {
                igloof.setIcey(false);
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, tooltips, tooltipFlag);
        tooltips.add(Component.translatable("item.snowballeffect.magic_coal.tooltip").setStyle(Style.EMPTY.withColor(
                ChatFormatting.YELLOW)));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getPlayer() == null) return InteractionResult.PASS;
        final Level level = ctx.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            //If we are on the serverside, process our logic.
            final BlockPos clickedPos = ctx.getClickedPos();
            if (Igloof.matchesSnow(serverLevel.getBlockState(clickedPos).getBlock())){
                final Igloof igloof = Igloof.of(serverLevel, ctx.getPlayer());
                igloof.setPos(ctx.getClickLocation());
                serverLevel.destroyBlock(clickedPos, true);
                serverLevel.addFreshEntity(igloof);
                if (!ctx.getPlayer().getAbilities().instabuild) ctx.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
            return super.useOn(ctx);
        }
        return super.useOn(ctx);
    }
}
