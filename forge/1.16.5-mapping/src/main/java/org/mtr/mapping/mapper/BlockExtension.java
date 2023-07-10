package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.state.StateContainer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockAbstractMapping;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;

public abstract class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockHelper.Properties properties) {
		super(properties.blockSettings);
	}

	@Deprecated
	@Override
	public final boolean hasTileEntity2(BlockState state) {
		return this instanceof BlockWithEntity;
	}

	@Deprecated
	@Override
	public final org.mtr.mapping.holder.BlockEntity createTileEntity2(BlockState arg0, BlockView arg1) {
		if (this instanceof BlockWithEntity) {
			return new BlockEntity(((BlockWithEntity) this).createBlockEntity().create(((BlockWithEntity) this).getBlockEntityType(), null, null));
		} else {
			return super.createTileEntity2(arg0, arg1);
		}
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}
}
