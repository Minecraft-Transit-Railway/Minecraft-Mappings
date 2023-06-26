package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(BlockEntityRendererFactory.Context.class, "BlockEntityRendererArgument");
		generateHolders.put(BlockEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(NbtCompound.class, "CompoundTag");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(MutableText.class, "MutableText");
		generateHolders.put(OrderedText.class, "OrderedText");
		generateHolders.put(Identifier.class, "ResourceLocation");
		generateHolders.put(Formatting.class, "TextFormatting");
		generateHolders.put(Vec3d.class, "Vector3d");
		generateHolders.put(Vector3f.class, "Vector3f");
		generateHolders.put(Vec3i.class, "Vector3i");
		generateHolders.generate();
	}
}
