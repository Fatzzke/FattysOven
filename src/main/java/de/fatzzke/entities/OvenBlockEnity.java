package de.fatzzke.entities;

import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;

import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OvenBlockEnity extends BaseContainerBlockEntity {

    public static final int SIZE = 10;

    // thanks neoforge docu for that? no other mod extends BaseContainerBlockEntity
    // but whatever
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    public int goldStorage = 0;
    public int energyStorage = 220;
    public boolean isWorking = false;
    // i hope java pass it by reference
    private final ItemStackHandler itemHandler = new ItemStackHandler(this.items) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            OvenBlockEnity.this.setChanged();
        }
    };

    public OvenBlockEnity(BlockPos pos, BlockState blockState) {
        super(FattysOven.OVEN_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        OvenBlockEnity tile = (OvenBlockEnity) be;
        if (tile.isWorking) {
            boolean stillWorking = false;
            for (var i = 0; i < SIZE - 1; i++) {
                var item = tile.itemHandler.getStackInSlot(i);
                if (item.isDamaged() && item.isDamageableItem()) {
                    item.setDamageValue(item.getDamageValue() - 1);
                    stillWorking = item.isDamaged() ? true : stillWorking;
                    tile.goldStorage -= 50;
                }
            }
            tile.consumeGold();
            tile.goldStorage = tile.goldStorage < 0 ? 0: tile.goldStorage;
            tile.isWorking = tile.hasResources() && stillWorking;
        }
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
        LogUtils.getLogger().debug("createMenu");
        return new OvenInventory(windowId, playerInventory, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("fattysoven.ovenentity");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    private boolean hasResources() {
        if (goldStorage > 0 && energyStorage > 0)
            return true;
        return false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        consumeGold();
        isWorking = false;
        if (!hasResources()) {
            return;
        }
        boolean hasRepairableItem = false;
        for (var i = 0; i < SIZE - 1; i++) {
            var itemStack = itemHandler.getStackInSlot(i);
            if (itemStack.isDamageableItem() && itemStack.isDamaged()) {
                hasRepairableItem = true;
            }
        }
        if (!hasRepairableItem) {
            return;
        }
        isWorking = true;
        return;
    }

    protected void consumeGold() {
        var itemStack = itemHandler.getStackInSlot(9);
        if (goldStorage <= 1 && itemStack.is(Items.GOLD_INGOT)) {
            itemStack.shrink(1);
            goldStorage = 1000;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        tag.put("oven_inventory", getInventory().serializeNBT(lookupProvider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        var serializedInventory = tag.getCompound("oven_inventory");
        this.itemHandler.deserializeNBT(lookupProvider, serializedInventory);
    }
}
