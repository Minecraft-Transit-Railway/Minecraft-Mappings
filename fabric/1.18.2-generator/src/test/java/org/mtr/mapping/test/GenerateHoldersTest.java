package org.mtr.mapping.test;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put("ActionResult", ActionResult.class);
		generateHolders.put("BlockEntityRendererArgument", BlockEntityRendererFactory.Context.class);
		generateHolders.put("BlockEntityType", BlockEntityType.class);
		generateHolders.put("BlockHitResult", BlockHitResult.class);
		generateHolders.put("BlockPos", BlockPos.class);
		generateHolders.put("BlockState", BlockState.class);
		generateHolders.put("BlockView", BlockView.class);
		generateHolders.put("BooleanProperty", BooleanProperty.class)
				.map("create", "of")
				.map("getValues", "getValues");
		generateHolders.put("ClientWorld", ClientWorld.class);
		generateHolders.put("CompoundTag", NbtCompound.class);
		generateHolders.put("Direction", Direction.class);
		generateHolders.put("DirectionProperty", DirectionProperty.class)
				.map("create", "of")
				.map("getValues", "getValues");
		generateHolders.put("EntityType", EntityType.class);
		generateHolders.put("EnumProperty", EnumProperty.class)
				.map("create", "of")
				.map("getValues", "getValues");
		generateHolders.put("Explosion", Explosion.class);
		generateHolders.put("FluidState", FluidState.class);
		generateHolders.put("Hand", Hand.class);
		generateHolders.put("IntegerProperty", IntProperty.class)
				.map("create", "of")
				.map("getValues", "getValues");
		generateHolders.put("ItemStack", ItemStack.class);
		generateHolders.put("LivingEntity", LivingEntity.class);
		generateHolders.put("MutableText", MutableText.class);
		generateHolders.put("OrderedText", OrderedText.class);
		generateHolders.put("PacketBuffer", PacketByteBuf.class);
		generateHolders.put("PlayerEntity", PlayerEntity.class);
		generateHolders.put("Property", Property.class);
		generateHolders.put("ResourceLocation", Identifier.class);
		generateHolders.put("ServerPlayerEntity", ServerPlayerEntity.class);
		generateHolders.put("ServerWorld", ServerWorld.class);
		generateHolders.put("ServerWorldAccess", ServerWorldAccess.class);
		generateHolders.put("TextFormatting", Formatting.class);
		generateHolders.put("Vector3d", Vec3d.class);
		generateHolders.put("Vector3f", Vec3f.class);
		generateHolders.put("Vector3i", Vec3i.class);
		generateHolders.put("VoxelShape", VoxelShape.class);
		generateHolders.put("World", World.class);
		generateHolders.put("WorldAccess", WorldAccess.class);
		generateHolders.putAbstract("Block", Block.class);
		generateHolders.putAbstract("Entity", Entity.class);
		generateHolders.putAbstract("Item", Item.class);
		generateHolders.generate();
	}
}
