package de.fatzzke.blocks;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;

import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.fattyoven.FattysOven;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class OvenBlock extends Block implements EntityBlock {

    // look into rotating these things or put then into enums
    static final VoxelShape[] OVEN_SHAPE = {
            Stream.of(Block.box(0, 0, 0, 15, 14, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(Block.box(0, 0, 1, 16, 14, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),

            Stream.of(Block.box(0, 0, 0, 16, 14, 15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(Block.box(1, 0, 0, 16, 14, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    };

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public OvenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    // therese gotta be a better way
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch (state.getValue(HORIZONTAL_FACING)) {
            case EAST:
                return OVEN_SHAPE[0];

            case NORTH:
                return OVEN_SHAPE[1];

            case SOUTH:
                return OVEN_SHAPE[2];

            case WEST:
                return OVEN_SHAPE[3];

            default:
                return OVEN_SHAPE[0];

        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos arg0, BlockState arg1) {
        return FattysOven.OVEN_BLOCK_ENTITY.get().create(arg0, arg1);
    }

    @SuppressWarnings("unchecked") // Due to generics, an unchecked cast is necessary here.
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return type == FattysOven.OVEN_BLOCK_ENTITY.get() ? (BlockEntityTicker<T>) OvenBlockEnity::tick : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level world, BlockPos pos, Player player,
            BlockHitResult pHitResult) {

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof OvenBlockEnity blockEnity)) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer sPlayer) {
            sPlayer.openMenu(blockEnity, pos);
        }

        return InteractionResult.CONSUME;

    }

}
