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
		readCompoundTag(tag);
	}

	@Override
	public CompoundTag saveClientData(CompoundTag tag) {
		writeCompoundTag(tag);
		return tag;
	}

	public final void fromClientTag(CompoundTag tag) {
		readCompoundTag(tag);
	}

	public final CompoundTag toClientTag(CompoundTag tag) {
		writeCompoundTag(tag);
		return tag;
	}
}
