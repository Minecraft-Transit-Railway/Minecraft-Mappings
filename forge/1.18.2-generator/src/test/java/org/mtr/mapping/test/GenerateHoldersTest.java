package org.mtr.mapping.test;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put("ActionResult", InteractionResult.class);
		generateHolders.put("BlockEntityRendererArgument", BlockEntityRendererProvider.Context.class);
		generateHolders.put("BlockEntityType", BlockEntityType.class);
		generateHolders.put("BlockHitResult", BlockHitResult.class);
		generateHolders.put("BlockPos", BlockPos.class);
		generateHolders.put("BlockState", BlockState.class);
		generateHolders.put("BlockView", BlockGetter.class);
		generateHolders.put("BooleanProperty", BooleanProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("ClientWorld", ClientLevel.class);
		generateHolders.put("CompoundTag", CompoundTag.class);
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
		generateHolders.put("Hand", InteractionHand.class);
		generateHolders.put("IntegerProperty", IntegerProperty.class)
				.map("create", "create")
				.map("getValues", "getPossibleValues");
		generateHolders.put("ItemStack", ItemStack.class);
		generateHolders.put("LivingEntity", LivingEntity.class);
		generateHolders.put("MutableText", MutableComponent.class);
		generateHolders.put("OrderedText", FormattedCharSequence.class);
		generateHolders.put("PacketBuffer", FriendlyByteBuf.class)
				.blacklist("readRegistryIdSafe");
		generateHolders.put("PlayerEntity", Player.class);
		generateHolders.put("Property", Property.class);
		generateHolders.put("ResourceLocation", ResourceLocation.class);
		generateHolders.put("ServerPlayerEntity", ServerPlayer.class);
		generateHolders.put("ServerWorld", ServerLevel.class);
		generateHolders.put("ServerWorldAccess", ServerLevelAccessor.class);
		generateHolders.put("TextFormatting", ChatFormatting.class);
		generateHolders.put("Vector3d", Vector3d.class);
		generateHolders.put("Vector3f", Vector3f.class);
		generateHolders.put("Vector3i", Vec3i.class);
		generateHolders.put("VoxelShape", VoxelShape.class);
		generateHolders.put("World", Level.class);
		generateHolders.put("WorldAccess", LevelAccessor.class);
		generateHolders.putAbstract("Block", Block.class);
		generateHolders.putAbstract("Entity", Entity.class);
		generateHolders.putAbstract("Item", Item.class);
		generateHolders.generate();
	}
}
