package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyInterface;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public interface BlockHelper extends DummyInterface {

	@MappedMethod
	default void addBlockProperties(List<HolderBase<?>> properties) {
	}

	@Deprecated
	default void createBlockStateDefinitionHelper(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		final List<HolderBase<?>> properties = new ArrayList<>();
		addBlockProperties(properties);

		if (!properties.isEmpty()) {
			final Property<?>[] newProperties = new Property[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				final Object data = properties.get(i).data;
				if (data instanceof Property) {
					newProperties[i] = (Property<?>) data;
				}
			}
			builder.add(newProperties);
		}
	}

	@MappedMethod
	default void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
	}

	@Deprecated
	default void appendTooltipHelper(ItemStack stack, @Nullable BlockView world, List<Component> tooltipList, TooltipContext options) {
		final List<MutableText> newTooltipList = new ArrayList<>();
		addTooltips(stack, world, newTooltipList, options);
		newTooltipList.forEach(mutableText -> tooltipList.add(mutableText.data));
	}

	@MappedMethod
	static BlockSettings setLuminance(BlockSettings blockSettings, ToIntFunction<BlockState> luminanceFunction) {
		return new BlockSettings(blockSettings.data.lightLevel(blockState -> luminanceFunction.applyAsInt(new BlockState(blockState))));
	}

	@MappedMethod
	static BlockSettings createBlockSettings(boolean blockPiston) {
		return new BlockSettings(BlockBehaviour.Properties.of().pushReaction(blockPiston ? PushReaction.BLOCK : PushReaction.NORMAL));
	}

	@MappedMethod
	static BlockSettings createBlockSettings(ToIntFunction<BlockState> luminanceFunction) {
		return setLuminance(createBlockSettings(false), luminanceFunction);
	}

	@MappedMethod
	static BlockSettings createBlockSettings(boolean blockPiston, ToIntFunction<BlockState> luminanceFunction) {
		return setLuminance(createBlockSettings(blockPiston), luminanceFunction);
	}

	@MappedMethod
	static VoxelShape shapeUnion(VoxelShape voxelShape, VoxelShape... voxelShapes) {
		VoxelShape result = voxelShape;
		for (final VoxelShape additionalShape : voxelShapes) {
			result = VoxelShapes.union(result, additionalShape);
		}
		return result;
	}
}
