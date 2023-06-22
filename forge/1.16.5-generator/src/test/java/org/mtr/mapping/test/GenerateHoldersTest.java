package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest implements HolderPath {

	@Test
	public void generate() throws IOException {
		final GenerationUtilities generationUtilities = new GenerationUtilities();
		generationUtilities.put(TileEntityType.class, "BlockEntityType");
		generationUtilities.put(BlockPos.class, "BlockPos");
		generationUtilities.put(BlockState.class, "BlockState");
		generationUtilities.put(CompoundNBT.class, "CompoundTag");
		generationUtilities.put(ResourceLocation.class, "ResourceLocation");
		generationUtilities.put(Vector3d.class, "Vector3d");
		generationUtilities.put(Vector3f.class, "Vector3f");
		generationUtilities.put(Vector3i.class, "Vector3i");
		generationUtilities.generate(PATH);
	}
}
