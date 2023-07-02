package org.mtr.mapping.test;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put("ActionResult", ActionResultType.class);
		generateHolders.put("BlockEntityRendererArgument", TileEntityRendererDispatcher.class);
		generateHolders.put("BlockEntityType", TileEntityType.class);
		generateHolders.put("BlockHitResult", BlockRayTraceResult.class);
		generateHolders.put("BlockPos", BlockPos.class);
		generateHolders.put("BlockState", BlockState.class)
				.map("onUse", "onUse");
		generateHolders.put("BlockView", IBlockReader.class);
		generateHolders.put("BooleanProperty", BooleanProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("ClientWorld", ClientWorld.class);
		generateHolders.put("CompoundTag", CompoundNBT.class);
		generateHolders.put("Direction", Direction.class);
		generateHolders.put("DirectionProperty", DirectionProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("EntityType", EntityType.class);
		generateHolders.put("EnumProperty", EnumProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("Explosion", Explosion.class);
		generateHolders.put("FluidState", FluidState.class);
		generateHolders.put("Hand", Hand.class);
		generateHolders.put("IntegerProperty", IntegerProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("ItemStack", ItemStack.class);
		generateHolders.put("LivingEntity", LivingEntity.class);
		generateHolders.put("MutableText", IFormattableTextComponent.class);
		generateHolders.put("OrderedText", IReorderingProcessor.class);
		generateHolders.put("PacketBuffer", PacketBuffer.class);
		generateHolders.put("PlayerEntity", PlayerEntity.class);
		generateHolders.put("Property", Property.class);
		generateHolders.put("ResourceLocation", ResourceLocation.class);
		generateHolders.put("ServerPlayerEntity", ServerPlayerEntity.class);
		generateHolders.put("ServerWorld", ServerWorld.class);
		generateHolders.put("ServerWorldAccess", IServerWorld.class);
		generateHolders.put("TextFormatting", TextFormatting.class);
		generateHolders.put("Vector3d", Vector3d.class);
		generateHolders.put("Vector3f", Vector3f.class);
		generateHolders.put("Vector3i", Vector3i.class);
		generateHolders.put("VoxelShape", VoxelShape.class);
		generateHolders.put("World", World.class);
		generateHolders.put("WorldAccess", IWorld.class);
		generateHolders.putAbstract("Block", Block.class);
		generateHolders.putAbstract("Entity", Entity.class);
		generateHolders.putAbstract("Item", Item.class);
		generateHolders.generate();
	}
}
