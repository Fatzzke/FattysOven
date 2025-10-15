package de.fatzzke.inventory;

import java.util.Objects;

import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.fattyoven.FattysOven;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class OvenInventory extends AbstractContainerMenu {
    public final OvenBlockEnity ovenEntity;
    private final ContainerLevelAccess levelAccess;

    // Client Constructor
    public OvenInventory(int containerId, Inventory playerInv, FriendlyByteBuf additionalData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(additionalData.readBlockPos()));
    }

    // Server Constructor
    public OvenInventory(int containerId, Inventory playerInv, BlockEntity blockEntity) {
        super(FattysOven.OVEN_INVENTORY.get(), containerId);
        if(blockEntity instanceof OvenBlockEnity be) {
            this.ovenEntity = be;
        } else {
            throw new IllegalStateException("Incorrect block entity class (%s) passed into ExampleMenu!"
                    .formatted(blockEntity.getClass().getCanonicalName()));
        }

        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);
        createBlockEntityInventory(be);
    }


    @Override
    public ItemStack quickMoveStack(Player arg0, int arg1) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
       // return stillValid(this.levelAccess, player, FattysOven.OVEN_BLOCK.get());
    }

    
    private void createBlockEntityInventory(OvenBlockEnity be) {
   
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 9; column++) {
                    addSlot(new SlotItemHandler(be.getInventory(),
                            column + (row * 9),
                            8 + (column * 18),
                            18 + (row * 18)));
                }
            }

    }

    private void createPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv,
                        9 + column + (row * 9),
                        8 + (column * 18),
                        84 + (row * 18)));
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv,
                    column,
                    8 + (column * 18),
                    142));
        }
    }

}
