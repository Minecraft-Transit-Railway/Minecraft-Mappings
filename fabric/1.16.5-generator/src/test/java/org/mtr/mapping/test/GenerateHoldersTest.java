package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(BlockEntityRenderDispatcher.class, "BlockEntityRendererArgument");
		generateHolders.put(BlockEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(NbtCompound.class, "CompoundTag");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(Identifier.class, "ResourceLocation");
		generateHolders.put(Vec3d.class, "Vector3d");
		generateHolders.put(Vec3f.class, "Vector3f");
		generateHolders.put(Vec3i.class, "Vector3i");
		generateHolders.generate();
	}
}
