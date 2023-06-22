package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest implements HolderPath {

	@Test
	public void generate() throws IOException {
		final GenerationUtilities generationUtilities = new GenerationUtilities();
		generationUtilities.put(BlockEntityType.class, "BlockEntityType");
		generationUtilities.put(BlockPos.class, "BlockPos");
		generationUtilities.put(BlockState.class, "BlockState");
		generationUtilities.put(NbtCompound.class, "CompoundTag");
		generationUtilities.put(Identifier.class, "ResourceLocation");
		generationUtilities.put(Vec3d.class, "Vector3d");
		generationUtilities.put(Vector3f.class, "Vector3f");
		generationUtilities.put(Vec3i.class, "Vector3i");
		generationUtilities.generate(PATH);
	}
}
