package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(ActionResult.class, "ActionResult");
		generateHolders.put(BlockEntityRendererFactory.Context.class, "BlockEntityRendererArgument");
		generateHolders.put(BlockEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(BlockView.class, "BlockView");
		generateHolders.put(ClientWorld.class, "ClientWorld");
		generateHolders.put(NbtCompound.class, "CompoundTag");
		generateHolders.put(Direction.class, "Direction");
		generateHolders.put(Entity.class, "Entity");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(LivingEntity.class, "LivingEntity");
		generateHolders.put(MutableText.class, "MutableText");
		generateHolders.put(OrderedText.class, "OrderedText");
		generateHolders.put(PlayerEntity.class, "PlayerEntity");
		generateHolders.put(Identifier.class, "ResourceLocation");
		generateHolders.put(ServerPlayerEntity.class, "ServerPlayerEntity");
		generateHolders.put(ServerWorld.class, "ServerWorld");
		generateHolders.put(ServerWorldAccess.class, "ServerWorldAccess");
		generateHolders.put(Formatting.class, "TextFormatting");
		generateHolders.put(Vec3d.class, "Vector3d");
		generateHolders.put(Vec3f.class, "Vector3f");
		generateHolders.put(Vec3i.class, "Vector3i");
		generateHolders.put(World.class, "World");
		generateHolders.put(WorldAccess.class, "WorldAccess");
		generateHolders.generate();
	}
}
