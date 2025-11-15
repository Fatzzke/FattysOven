package de.fatzzke.entities;

import javax.annotation.Nullable;

import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import de.fatzzke.util.CustomEnergyStorage;
import de.fatzzke.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OvenBlockEnity extends BlockEntity implements TickableBlockEntity, MenuProvider {

    public static final int SIZE = 14;

    // thanks neoforge docu for that? no other mod extends BaseContainerBlockEntity
    // but whatever
    private int goldStorage = 0;
    private int baseGoldPerTick = 50;
    private int baseEnergyPerTick = 200;
    private int baseRepairPerTick = 1;
    private int calculatedGoldPerTick = 50;
    private int calculatedEnergyPerTick = 200;
    private int calculateRepairPerTick = 200;
    public boolean isWorking = false;
    // i hope java pass it by reference
    private final ItemStackHandler itemHandler = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            OvenBlockEnity.this.setChanged();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.is(Items.GOLD_INGOT)) {
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            FattysOven.LOGGER.debug("a");
            return super.extractItem(slot, amount, simulate);
        }

    };
    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(20000, 1000, 0);

    public final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> OvenBlockEnity.this.energyStorage.getEnergyStored();
                case 1 -> OvenBlockEnity.this.energyStorage.getMaxEnergyStored();
                case 2 -> OvenBlockEnity.this.goldStorage;
                case 3 -> OvenBlockEnity.this.isWorking();
                default -> throw new UnsupportedOperationException("Unexpected value: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> OvenBlockEnity.this.energyStorage.changeEnergy(pValue);
                case 1 -> OvenBlockEnity.this.energyStorage.setEnerrgy(pValue);
                case 2 -> OvenBlockEnity.this.goldStorage = pValue;
                case 3 -> OvenBlockEnity.this.isWorking = pValue == 0 ? false : true;
            }

        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public OvenBlockEnity(BlockPos pos, BlockState blockState) {
        super(FattysOven.OVEN_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void tick() {
        if (level == null || this.level.isClientSide()) {
            return;
        }
        if (isWorking) {
            boolean stillWorking = false;
            for (var i = 0; i < SIZE - 1; i++) {
                var item = itemHandler.getStackInSlot(i);
                if (item.isDamaged() && item.isDamageableItem()) {
                    item.setDamageValue(item.getDamageValue() - calculateRepairPerTick);
                    stillWorking = item.isDamaged() ? true : stillWorking;
                    goldStorage -= calculatedGoldPerTick;
                    energyStorage.changeEnergy(-calculatedEnergyPerTick);
                    FattysOven.LOGGER.debug(String.valueOf(energyStorage.getEnergyStored()));
                }
            }
            consumeGold();
            goldStorage = goldStorage < 0 ? 0 : goldStorage;
            isWorking = hasResources() && stillWorking;
            // sendUpdate();
        }
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    private boolean hasResources() {
        if (goldStorage > 0 && energyStorage.getEnergyStored() > 0)
            return true;
        return false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        consumeGold();
        calculateStats();
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

        var tagData = new CompoundTag();
        tagData.put("oven_inventory", getInventory().serializeNBT(lookupProvider));
        tagData.putInt("gold_storage", goldStorage);
        tagData.putInt("energy", energyStorage.getEnergyStored());

        tag.put("oven_data", tagData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);

        var tagData = tag.getCompound("oven_data");

        itemHandler.deserializeNBT(lookupProvider, tagData.getCompound("oven_inventory"));
        energyStorage.setEnerrgy(tagData.getInt("energy"));
        goldStorage = tagData.getInt("gold_storage");

    }

    public IEnergyStorage getEnergyStorage(Direction facing) {
        return energyStorage;
    }

    private void sendUpdate() {
        FattysOven.LOGGER.debug("sendUpdate");
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private int isWorking() {
        return isWorking ? 1 : 0;
    }

    private void calculateStats() {
        int mult = 1;
        for (int i = 0; i < 4; i++) {
            mult *= itemHandler.getStackInSlot(10 + i).is(FattysOven.UPGRADE_ITEM) ? 2 : 1;
        }

        calculateRepairPerTick = baseRepairPerTick * mult;
        calculatedEnergyPerTick = baseEnergyPerTick * mult;
        calculatedGoldPerTick = baseGoldPerTick * mult;
        energyStorage.setCapacity(10000 * mult);
        energyStorage.setMaxRecieve(1000 * mult);
    }

    public int getCalculatedEnergyPerTick() {
        return calculatedEnergyPerTick;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new OvenInventory(windowId, playerInventory, this, containerData);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("fattysoven.ovenentity");
    }
}
