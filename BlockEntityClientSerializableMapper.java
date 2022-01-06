package @package@;

import dev.architectury.extensions.BlockEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityClientSerializableMapper extends BlockEntityMapper implements BlockEntityExtension {

	public BlockEntityClientSerializableMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void loadClientData(BlockState pos, CompoundTag tag) {
		load(tag);
	}

	@Override
	public CompoundTag saveClientData(CompoundTag tag) {
		save(tag);
		return tag;
	}

	public final void fromClientTag(CompoundTag tag) {
		load(tag);
	}

	public final CompoundTag toClientTag(CompoundTag tag) {
		save(tag);
		return tag;
	}
}
