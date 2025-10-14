package de.fatzzke.blocks;

import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class OvenBlock extends Block {

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
}
