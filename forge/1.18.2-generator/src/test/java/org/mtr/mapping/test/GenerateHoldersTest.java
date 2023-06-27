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
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenerateHoldersTest {

	@Test
	public void generate() throws IOException {
		final GenerateHolders generateHolders = new GenerateHolders();
		generateHolders.put(InteractionResult.class, "ActionResult");
		generateHolders.put(BlockEntityRendererProvider.Context.class, "BlockEntityRendererArgument");
		generateHolders.put(BlockEntityType.class, "BlockEntityType");
		generateHolders.put(BlockPos.class, "BlockPos");
		generateHolders.put(BlockState.class, "BlockState");
		generateHolders.put(BlockGetter.class, "BlockView");
		generateHolders.put(ClientLevel.class, "ClientWorld");
		generateHolders.put(CompoundTag.class, "CompoundTag");
		generateHolders.put(Direction.class, "Direction");
		generateHolders.put(ItemStack.class, "ItemStack");
		generateHolders.put(LivingEntity.class, "LivingEntity");
		generateHolders.put(MutableComponent.class, "MutableText");
		generateHolders.put(FormattedCharSequence.class, "OrderedText");
		generateHolders.put(FriendlyByteBuf.class, "PacketBuffer");
		generateHolders.put(Player.class, "PlayerEntity");
		generateHolders.put(ResourceLocation.class, "ResourceLocation");
		generateHolders.put(ServerPlayer.class, "ServerPlayerEntity");
		generateHolders.put(ServerLevel.class, "ServerWorld");
		generateHolders.put(ServerLevelAccessor.class, "ServerWorldAccess");
		generateHolders.put(ChatFormatting.class, "TextFormatting");
		generateHolders.put(Vector3d.class, "Vector3d");
		generateHolders.put(Vector3f.class, "Vector3f");
		generateHolders.put(Vec3i.class, "Vector3i");
		generateHolders.put(Level.class, "World");
		generateHolders.put(LevelAccessor.class, "WorldAccess");
		generateHolders.generate();
	}
}
