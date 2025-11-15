package de.fatzzke.items;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class GoldSticker extends Item {

    public GoldSticker(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context,
            @Nonnull List<Component> tooltipComponents,
            @Nonnull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.fattysoven.gold_sticker").withStyle(ChatFormatting.BLUE));
    }
}
