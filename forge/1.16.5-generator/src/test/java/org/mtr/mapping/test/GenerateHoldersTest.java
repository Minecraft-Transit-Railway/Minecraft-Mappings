package org.mtr.mapping.test;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(ActionResultType.class, "ActionResult");
		generateHolders.put(TileEntityRendererDispatcher.class, "BlockEntityRendererArgument");
		generateHolders.put(TileEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(IBlockReader.class, "BlockView");
		generateHolders.put(ClientWorld.class, "ClientWorld");
		generateHolders.put(CompoundNBT.class, "CompoundTag");
		generateHolders.put(Direction.class, "Direction");
		generateHolders.put(Entity.class, "Entity");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(LivingEntity.class, "LivingEntity");
		generateHolders.put(IFormattableTextComponent.class, "MutableText");
		generateHolders.put(IReorderingProcessor.class, "OrderedText");
		generateHolders.put(PlayerEntity.class, "PlayerEntity");
		generateHolders.put(ResourceLocation.class, "ResourceLocation");
		generateHolders.put(ServerPlayerEntity.class, "ServerPlayerEntity");
		generateHolders.put(ServerWorld.class, "ServerWorld");
		generateHolders.put(IServerWorld.class, "ServerWorldAccess");
		generateHolders.put(TextFormatting.class, "TextFormatting");
		generateHolders.put(Vector3d.class, "Vector3d");
		generateHolders.put(Vector3f.class, "Vector3f");
		generateHolders.put(Vector3i.class, "Vector3i");
		generateHolders.put(World.class, "World");
		generateHolders.put(IWorld.class, "WorldAccess");
		generateHolders.generate();
	}
}
