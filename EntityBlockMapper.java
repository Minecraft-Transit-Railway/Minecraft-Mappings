package @package@;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface EntityBlockMapper extends EntityBlock {

	@Override
	default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return createBlockEntity(pos, state);
	}

	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return getType() == type ? (world1, pos, state1, blockEntity) -> tick(world1, pos, (BlockEntityMapper) blockEntity) : null;
	}

	BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state);

	default <T extends BlockEntityMapper> void tick(Level world, BlockPos pos, T blockEntity) {
	}

	default BlockEntityType<? extends BlockEntityMapper> getType() {
		return null;
	}
}
