package org.mtr.mapping.test;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(BlockEntityRendererProvider.Context.class, "BlockEntityRendererArgument");
		generateHolders.put(BlockEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(CompoundTag.class, "CompoundTag");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(MutableComponent.class, "MutableText");
		generateHolders.put(FormattedCharSequence.class, "OrderedText");
		generateHolders.put(ResourceLocation.class, "ResourceLocation");
		generateHolders.put(ChatFormatting.class, "TextFormatting");
		generateHolders.put(Vector3d.class, "Vector3d");
		generateHolders.put(Vector3f.class, "Vector3f");
		generateHolders.put(Vec3i.class, "Vector3i");
		generateHolders.generate();
	}
}
