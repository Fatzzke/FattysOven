package de.fatzzke.client;

import javax.annotation.Nonnull;

import com.mojang.logging.LogUtils;

import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class OvenMenuScreen extends AbstractContainerScreen<OvenInventory> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FattysOven.MODID,
            "textures/gui/oven_inventory.png");
    private long startTime = System.currentTimeMillis();
    private OvenBlockEnity ovenEntity;

    public OvenMenuScreen(OvenInventory menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        ovenEntity = menu.ovenEntity;

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float arg1, int arg2, int arg3) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        drawBars(guiGraphics, 0, this.leftPos + 160, this.topPos + 38, ovenEntity.goldStorage / 100);
        // drawBars(guiGraphics, 1, this.leftPos + 160, this.topPos + 71,
        //         (ovenEntity.energyStorage.getEnergyStored() / ovenEntity.energyStorage.getMaxEnergyStored()) * 20);
                drawBars(guiGraphics, 1, this.leftPos + 160, this.topPos + 71,
               10);
        drawFeudel(guiGraphics);
        // FattysOven.LOGGER.debug(String.valueOf(ovenEntity.energyStorage.getEnergyStored()));
      //   FattysOven.LOGGER.debug(String.valueOf(ovenEntity.goldStorage));

    }

    private void drawBars(GuiGraphics guiGraphics, int type, int startX, int startY, int count) {
        switch (type) {
            case 0:
                for (int i = 0; i < count; i++) {
                    guiGraphics.blit(TEXTURE, startX, startY - (i * 2), 177, 0, 7, 2);
                }
                break;
            case 1:
                for (int i = 0; i < count; i++) {
                    guiGraphics.blit(TEXTURE, startX, startY - (i * 2), 177, 2, 7, 2);
                }
                break;
            default:
                return;
        }
    }

    private void drawFeudel(GuiGraphics guiGraphics) {
        if (ovenEntity.isWorking) {
            if (System.currentTimeMillis() - startTime < 200) {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 136, 60, 60);
            } else if (System.currentTimeMillis() - startTime < 400) {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 196, 60, 60);
            } else {
                guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 10, 196, 136, 60, 60);
                startTime = System.currentTimeMillis();
            }
        }
    }
}
