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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OvenBlockEnity extends BaseContainerBlockEntity {

    public static final int SIZE = 9;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);


    public OvenBlockEnity(BlockPos pos, BlockState blockState) {
        super(FattysOven.OVEN_BLOCK_ENTITY.get(), pos, blockState);
            }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        OvenBlockEnity tile = (OvenBlockEnity) be;
        tile.setItem(0, new ItemStack(Items.DIRT));
        

    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
        return new OvenInventory(windowId,playerInventory, this);
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

    public ItemStackHandler getInventory(){
        return this.getInventory();
    }

}
