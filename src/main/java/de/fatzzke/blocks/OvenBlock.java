package de.fatzzke.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import de.fatzzke.entities.OvenBlockEnity;
import de.fatzzke.fattyoven.FattysOven;
import de.fatzzke.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos,
            @Nonnull CollisionContext context) {
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
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos arg0, @Nonnull BlockState arg1) {
        return FattysOven.OVEN_BLOCK_ENTITY.get().create(arg0, arg1);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state,
            @Nonnull BlockEntityType<T> type) {
        return TickableBlockEntity.getTickerHelper(level);
    }

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState pState, @Nonnull Level world, @Nonnull BlockPos pos,
            @Nonnull Player player,
            @Nonnull BlockHitResult pHitResult) {

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof OvenBlockEnity blockEnity)) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer sPlayer) {
            sPlayer.openMenu(blockEnity, pos);
            world.playSound(null, pos, FattysOven.OVEN_OPEN_SOUND.value(), SoundSource.BLOCKS);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos,@Nonnull BlockState newState,
            boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof OvenBlockEnity blockEnity) {
                for (int i = 0; i < OvenBlockEnity.SIZE; i++) {
                    ItemStack stack = blockEnity.getInventory().getStackInSlot(i);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    float dX = world.random.nextFloat() * 0.8F + 0.1F;
                    float dY = world.random.nextFloat() * 0.8F + 0.1F;
                    float dZ = world.random.nextFloat() * 0.8F + 0.1F;
                    ItemEntity entityItem = new ItemEntity(world, pos.getX() + dX, pos.getY() + dY, pos.getZ() + dZ,
                            stack.copy());
                    world.addFreshEntity(entityItem);

                }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
