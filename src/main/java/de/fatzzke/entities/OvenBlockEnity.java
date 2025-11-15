package de.fatzzke.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.inventory.OvenInventory;
import de.fatzzke.util.CustomEnergyStorage;
import de.fatzzke.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
    private int maxgoldStorage = 10000;
    private int baseGoldPerTick = 50;
    private int baseEnergyPerTick = 200;
    private int baseRepairPerTick = 1;
    private int calculatedGoldPerTick = 50;
    private int calculatedEnergyPerTick = 200;
    private int calculateRepairPerTick = 200;
    public boolean isWorking = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            OvenBlockEnity.this.setChanged();
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            FattysOven.LOGGER.debug("A");
            // 3x3 slot
            if (slot < 9 && !stack.is(Items.GOLD_INGOT)) {
                return super.insertItem(slot, stack, simulate);
            }
            // gold slot
            if (slot == 9 && stack.is(Items.GOLD_INGOT)) {
                return super.insertItem(slot, stack, simulate);
            }
            // other slots dont insert
            return stack;
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
        if (goldStorage <= 9000 && itemStack.is(Items.GOLD_INGOT)) {
            itemStack.shrink(1);
            goldStorage += 1000;
        } else if (goldStorage <= 1000 && itemStack.is(Items.GOLD_BLOCK)) {
            itemStack.shrink(1);
            goldStorage += 9000;
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);

        var tagData = new CompoundTag();
        tagData.put("oven_inventory", getInventory().serializeNBT(lookupProvider));
        tagData.putInt("gold_storage", goldStorage);
        tagData.putInt("energy", energyStorage.getEnergyStored());

        tag.put("oven_data", tagData);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookupProvider) {
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
        FattysOven.LOGGER.debug("calcStats");
        int repairMult = 1, energyMult = 1, goldMult = 1, storeMult = 1;
        for (int i = 0; i < 4; i++) {
            var stack = itemHandler.getStackInSlot(10 + i);
            if (stack.is(FattysOven.UPGRADE_ITEM)) {
                repairMult *= 2;
                energyMult *= 2;
                goldMult *= 2;
                storeMult *= 2;
            } else if (stack.is(FattysOven.GOLD_ITEM)) {

            } else if (stack.is(FattysOven.ENERGY_ITEM)) {

            } else if (stack.is(FattysOven.UPGRADIGER_ITEM)) {
                repairMult *= 4;
                energyMult *= 4;
                goldMult *= 4;
                storeMult *= 4;
            }

        }
        calculateRepairPerTick = baseRepairPerTick * repairMult;
        calculatedEnergyPerTick = baseEnergyPerTick * energyMult;
        calculatedGoldPerTick = baseGoldPerTick * goldMult;
        energyStorage.setCapacity(10000 * storeMult);
        energyStorage.setMaxRecieve(1000 * storeMult);
    }

    public int getCalculatedEnergyPerTick() {
        return calculatedEnergyPerTick;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new OvenInventory(windowId, playerInventory, this, containerData);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("fattysoven.ovenentity");
    }
}
