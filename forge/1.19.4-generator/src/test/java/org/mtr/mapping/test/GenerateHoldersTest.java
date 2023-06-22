package org.mtr.mapping.test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
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
		generationUtilities.put(CompoundTag.class, "CompoundTag");
		generationUtilities.put(Vector3d.class, "Vector3d");
		generationUtilities.put(Vector3f.class, "Vector3f");
		generationUtilities.put(Vec3i.class, "Vector3i");
		generationUtilities.generate(PATH);
	}
}
