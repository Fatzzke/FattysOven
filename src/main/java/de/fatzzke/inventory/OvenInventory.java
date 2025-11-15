package de.fatzzke.inventory;


import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.fattyoven.FattysOven;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class OvenInventory extends AbstractContainerMenu {
    public final OvenBlockEnity ovenEntity;
    private final ContainerLevelAccess levelAccess;
    public final ContainerData data;

    // Client Constructor
    public OvenInventory(int containerId, Inventory playerInv, FriendlyByteBuf additionalData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(additionalData.readBlockPos()),
                new SimpleContainerData(4));
    }

    // Server Constructor
    public OvenInventory(int containerId, Inventory playerInv, BlockEntity blockEntity, ContainerData data) {
        super(FattysOven.OVEN_INVENTORY.get(), containerId);
        if (blockEntity instanceof OvenBlockEnity be) {
            this.ovenEntity = be;
        } else {
            throw new IllegalStateException("Incorrect block entity class (%s) passed into OvenInventory!"
                    .formatted(blockEntity.getClass().getCanonicalName()));
        }

        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);
        createBlockEntityInventory(be);

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            int pInventorySize = player.getInventory().items.size();     
            if (index < pInventorySize ) {
                if (!moveItemStackTo(stack1, pInventorySize, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack1, 0, pInventorySize, false)) {
                return ItemStack.EMPTY;
            }
            if (stack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
                slot.onTake(player, stack);
            } else {
                slot.setChanged();
            }
        }
        this.ovenEntity.setChanged();
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, FattysOven.OVEN_BLOCK.get());
    }

    private void createBlockEntityInventory(OvenBlockEnity be) {
        var itemHandler = be.getInventory();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new SlotItemHandler(itemHandler,
                        column + (row * 3),
                        97 + (column * 18),
                        20 + (row * 18)));
            }
        }
        addSlot(new SlotItemHandler(itemHandler, 9, 26, 38));
        //upgrade Slots
        addSlot(new SlotItemHandler(itemHandler, 10, 173, 7));
        addSlot(new SlotItemHandler(itemHandler, 11, 173, 25));
        addSlot(new SlotItemHandler(itemHandler, 12, 173, 43));
        addSlot(new SlotItemHandler(itemHandler, 13, 173, 61));
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
