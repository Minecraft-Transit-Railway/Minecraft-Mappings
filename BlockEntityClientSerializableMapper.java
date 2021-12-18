package minecraftmappings;

import me.shedaniel.architectury.extensions.BlockEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityClientSerializableMapper extends BlockEntityMapper implements BlockEntityExtension {

	public BlockEntityClientSerializableMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public final void loadClientData(BlockState state, CompoundTag compoundTag) {
		load(state, compoundTag);
	}

	@Override
	public final CompoundTag saveClientData(CompoundTag compoundTag) {
		return save(compoundTag);
	}

	@Override
	public final CompoundTag getUpdateTag() {
		final CompoundTag compoundTag = super.getUpdateTag();
		readCompoundTag(compoundTag);
		return compoundTag;
	}
}
