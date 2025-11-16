package de.fatzzke.client;

import javax.annotation.Nonnull;

import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OvenMenuScreen extends AbstractContainerScreen<OvenInventory> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FattysOven.MODID,
            "textures/gui/oven_inventory.png");
    private long startTime = System.currentTimeMillis();

    public OvenMenuScreen(OvenInventory menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 195;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float arg1, int arg2, int arg3) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        drawBars(guiGraphics, 0, this.leftPos + 160, this.topPos + 38, this.menu.data.get(2) / 1000);
        drawBars(guiGraphics, 1, this.leftPos + 160, this.topPos + 71,
                ((this.menu.data.get(0))) * 10 / (this.menu.data.get(1) == 0 ? 10000 : this.menu.data.get(1))); // this.menu.data.get(1)
                                                                                                                // sometimes
                                                                                                                // seems
                                                                                                                // to be
                                                                                                                // 0
                                                                                                                //WTF ist this autoformatting
                                                                                                                

        drawFeudel(guiGraphics);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, "FE/T: " + String.valueOf(this.menu.ovenEntity.getCalculatedEnergyPerTick()), 8, 50, 0xff00ff);
        guiGraphics.drawString(font, "GOLD/T: " + String.valueOf(this.menu.ovenEntity.getCalculatedGoldPerTick()), 8, 60, 0xff00ff);
        guiGraphics.drawString(font, "REPAIR/5T: " + String.valueOf(this.menu.ovenEntity.getCalculatedRepairPerTick()), 8, 70, 0xff00ff);
    }

    private void drawBars(GuiGraphics guiGraphics, int type, int startX, int startY, int count) {
        count = count > 10 ? 10 : count;
        switch (type) {
            case 0:
                for (int i = 0; i < count; i++) {
                    guiGraphics.blit(TEXTURE, startX, startY - (i * 2), 250, 0, 6, 2);
                }
                break;
            case 1:
                for (int i = 0; i < count; i++) {
                    guiGraphics.blit(TEXTURE, startX, startY - (i * 2), 250, 2, 6, 2);
                }
                break;
            default:
                return;
        }
    }

    private void drawFeudel(GuiGraphics guiGraphics) {
           //     FattysOven.LOGGER.debug(String.valueOf(this.menu.data.get(4)));
        if (this.menu.data.get(3) != 0 && this.menu.data.get(4) == 0) {
            if (System.currentTimeMillis() - startTime < 200) {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 136, 60, 60);
            } else if (System.currentTimeMillis() - startTime < 400) {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 196, 60, 60);
            } else {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 136, 60, 60);
                startTime = System.currentTimeMillis();
            }
        }
        else if(this.menu.data.get(4) != 0){
            guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 136, 60, 60);
        }
    }
}
