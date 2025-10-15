package de.fatzzke.client;

import javax.annotation.Nonnull;

import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class OvenMenuScreen extends AbstractContainerScreen<OvenInventory> {
    
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FattysOven.MODID, "textures/gui/oven_inventory.png");

    public OvenMenuScreen(OvenInventory menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float arg1, int arg2, int arg3) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    }

    @Override
    public void render(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
