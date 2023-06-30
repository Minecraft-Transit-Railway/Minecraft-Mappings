package org.mtr.mapping.test;

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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put("ActionResult", InteractionResult.class);
		generateHolders.put("BlockEntityRendererArgument", BlockEntityRendererProvider.Context.class);
		generateHolders.put("BlockEntityType", BlockEntityType.class);
		generateHolders.put("BlockPos", BlockPos.class);
		generateHolders.put("BlockState", BlockState.class);
		generateHolders.put("BlockView", BlockGetter.class);
		generateHolders.put("ClientWorld", ClientLevel.class);
		generateHolders.put("CompoundTag", CompoundTag.class);
		generateHolders.put("Direction", Direction.class);
		generateHolders.put("ItemStack", ItemStack.class);
		generateHolders.put("LivingEntity", LivingEntity.class);
		generateHolders.put("MutableText", MutableComponent.class);
		generateHolders.put("OrderedText", FormattedCharSequence.class);
		generateHolders.put("PacketBuffer", FriendlyByteBuf.class);
		generateHolders.put("PlayerEntity", Player.class);
		generateHolders.put("ResourceLocation", ResourceLocation.class);
		generateHolders.put("ServerPlayerEntity", ServerPlayer.class);
		generateHolders.put("ServerWorld", ServerLevel.class);
		generateHolders.put("ServerWorldAccess", ServerLevelAccessor.class);
		generateHolders.put("TextFormatting", ChatFormatting.class);
		generateHolders.put("Vector3d", Vector3d.class);
		generateHolders.put("Vector3f", Vector3f.class);
		generateHolders.put("Vector3i", Vec3i.class);
		generateHolders.put("World", Level.class);
		generateHolders.put("WorldAccess", LevelAccessor.class);
		generateHolders.generate();
	}
}
