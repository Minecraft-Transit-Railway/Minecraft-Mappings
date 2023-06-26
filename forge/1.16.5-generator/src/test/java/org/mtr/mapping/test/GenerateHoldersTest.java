package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(TileEntityRendererDispatcher.class, "BlockEntityRendererArgument");
		generateHolders.put(TileEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(CompoundNBT.class, "CompoundTag");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(ResourceLocation.class, "ResourceLocation");
		generateHolders.put(Vector3d.class, "Vector3d");
		generateHolders.put(Vector3f.class, "Vector3f");
		generateHolders.put(Vector3i.class, "Vector3i");
		generateHolders.generate();
	}
}
