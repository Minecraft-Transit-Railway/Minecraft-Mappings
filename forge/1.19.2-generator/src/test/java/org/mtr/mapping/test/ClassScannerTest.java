package org.mtr.mapping.test;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.ChatFormatting;
import net.minecraft.DetectedVersion;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.junit.jupiter.api.Test;

public final class ClassScannerTest {

	@Test
	public void scan() {
		final ClassScannerBase scanner = ClassScannerBase.getInstance();
		scanner.put("AbstractTexture", AbstractTexture.class);
		scanner.put("ActionResult", InteractionResult.class);
		scanner.put("Axis", Direction.Axis.class);
		scanner.put("Biome", Biome.class);
		scanner.put("BiomeSource", BiomeSource.class);
		scanner.put("BlockEntityType", BlockEntityType.class);
		scanner.put("BlockHitResult", BlockHitResult.class);
		scanner.put("BlockPos", BlockPos.class);
		scanner.put("BlockRenderType", RenderShape.class);
		scanner.put("BlockRenderView", BlockAndTintGetter.class);
		scanner.put("BlockRotation", Rotation.class);
		scanner.put("Blocks", Blocks.class);
		scanner.put("BlockSettings", BlockBehaviour.Properties.class);
		scanner.put("BlockState", BlockState.class);
		scanner.put("BlockView", BlockGetter.class);
		scanner.put("BooleanBiFunction", BooleanOp.class);
		scanner.put("BooleanProperty", BooleanProperty.class);
		scanner.put("BossBarManager", CustomBossEvents.class);
		scanner.put("Box", AABB.class);
		scanner.put("BufferBuilder", BufferBuilder.class);
		scanner.put("BufferBuilderStorage", RenderBuffers.class);
		scanner.put("Camera", Camera.class);
		scanner.put("ClientChunkManager", ClientChunkCache.class);
		scanner.put("Chunk", ChunkAccess.class);
		scanner.put("ChunkGenerator", ChunkGenerator.class);
		scanner.put("ChunkManager", ChunkSource.class);
		scanner.put("ChunkPos", ChunkPos.class);
		scanner.put("ChunkSection", LevelChunkSection.class);
		scanner.put("ChunkStatus", ChunkStatus.class);
		scanner.put("ClientPlayerEntity", LocalPlayer.class, "getType");
		scanner.put("ClientPlayNetworkHandler", ClientPacketListener.class);
		scanner.put("ClientWorld", ClientLevel.class);
		scanner.put("CommandFunctionManager", ServerFunctionManager.class);
		scanner.put("CompoundTag", CompoundTag.class);
		scanner.put("DataCommandStorage", CommandStorage.class);
		scanner.put("DefaultParticleType", SimpleParticleType.class);
		scanner.put("Difficulty", Difficulty.class);
		scanner.put("DimensionOptions", LevelStem.class);
		scanner.put("DimensionType", DimensionType.class);
		scanner.put("Direction", Direction.class);
		scanner.put("DirectionProperty", DirectionProperty.class);
		scanner.put("EntityPose", Pose.class);
		scanner.put("EntityRenderDispatcher", EntityRenderDispatcher.class);
		scanner.put("EntityType", EntityType.class);
		scanner.put("EnumProperty", EnumProperty.class);
		scanner.put("Explosion", Explosion.class);
		scanner.put("FluidState", FluidState.class);
		scanner.put("GameMode", GameType.class);
		scanner.put("GameOptions", Options.class);
		scanner.put("GameRenderer", GameRenderer.class);
		scanner.put("Hand", InteractionHand.class);
		scanner.put("HeightMap", Heightmap.class);
		scanner.put("HeightMapType", Heightmap.Types.class);
		scanner.put("HitResult", HitResult.class);
		scanner.put("HitResultType", HitResult.Type.class);
		scanner.put("Identifier", ResourceLocation.class);
		scanner.put("IntegerProperty", IntegerProperty.class);
		scanner.put("InternalFormat", NativeImage.InternalGlFormat.class);
		scanner.put("Inventory", Container.class);
		scanner.put("ItemConvertible", ItemLike.class);
		scanner.put("ItemPlacementContext", BlockPlaceContext.class);
		scanner.put("Items", Items.class);
		scanner.put("ItemSettings", Item.Properties.class);
		scanner.put("ItemStack", ItemStack.class);
		scanner.put("ItemUsageContext", UseOnContext.class);
		scanner.put("KeyBinding", KeyMapping.class);
		scanner.put("LightmapTextureManager", LightTexture.class);
		scanner.put("LightType", LightLayer.class);
		scanner.put("LivingEntity", LivingEntity.class, "getType");
		scanner.put("MapColor", MaterialColor.class);
		scanner.put("MathHelper", Mth.class, "MathHelper");
		scanner.put("Matrix3f", Matrix3f.class);
		scanner.put("Matrix4f", Matrix4f.class);
		scanner.put("MinecraftClient", Minecraft.class, "ask", "askEither");
		scanner.put("MinecraftServer", MinecraftServer.class, "ask", "askEither");
		scanner.put("MinecraftVersion", DetectedVersion.class);
		scanner.put("Mirror", Mirror.class);
		scanner.put("ModelPart", ModelPart.class);
		scanner.put("MutableText", MutableComponent.class);
		scanner.put("NativeImage", NativeImage.class);
		scanner.put("NativeImageBackedTexture", DynamicTexture.class);
		scanner.put("NativeImageFormat", NativeImage.Format.class);
		scanner.put("OperatingSystem", Util.OS.class);
		scanner.put("OrderedText", FormattedCharSequence.class);
		scanner.put("OverlayTexture", OverlayTexture.class);
		scanner.put("PacketBuffer", FriendlyByteBuf.class);
		scanner.put("ParticleEffect", ParticleOptions.class);
		scanner.put("ParticleTypes", ParticleTypes.class);
		scanner.put("PlayerEntity", Player.class, "getType");
		scanner.put("PlayerInventory", Inventory.class);
		scanner.put("PlayerListEntry", PlayerInfo.class);
		scanner.put("PlayerManager", PlayerList.class);
		scanner.put("Position", Position.class);
		scanner.put("Property", Property.class);
		scanner.put("Random", RandomSource.class);
		scanner.put("RenderLayer", RenderType.class);
		scanner.put("Resource", Resource.class);
		scanner.put("ResourceManager", ResourceManager.class);
		scanner.put("SaveProperties", WorldData.class);
		scanner.put("Scoreboard", Scoreboard.class);
		scanner.put("ScoreboardCriterion", ObjectiveCriteria.class);
		scanner.put("ScoreboardCriterionRenderType", ObjectiveCriteria.RenderType.class);
		scanner.put("ScoreboardObjective", Objective.class);
		scanner.put("ScoreboardScore", Score.class);
		scanner.put("ServerChunkManager", ServerChunkCache.class);
		scanner.put("ServerPlayerEntity", ServerPlayer.class, "getType");
		scanner.put("ServerWorld", ServerLevel.class);
		scanner.put("ServerWorldAccess", ServerLevelAccessor.class);
		scanner.put("ShapeContext", CollisionContext.class);
		scanner.put("SlabType", SlabType.class);
		scanner.put("SoundCategory", SoundSource.class);
		scanner.put("SoundEvent", SoundEvent.class);
		scanner.put("SoundEvents", SoundEvents.class);
		scanner.put("SoundInstance", SoundInstance.class);
		scanner.put("SoundManager", SoundManager.class);
		scanner.put("SpriteProvider", SpriteSet.class);
		scanner.put("StairShape", StairsShape.class);
		scanner.put("Style", Style.class);
		scanner.put("Team", PlayerTeam.class);
		scanner.put("Tessellator", Tesselator.class);
		scanner.put("Text", Component.class);
		scanner.put("TextColor", TextColor.class);
		scanner.put("TextFormatting", ChatFormatting.class);
		scanner.put("TextRenderer", Font.class);
		scanner.put("TextureManager", TextureManager.class);
		scanner.put("TooltipContext", TooltipFlag.class);
		scanner.put("Util", Util.class);
		scanner.put("Vector3d", Vec3.class);
		scanner.put("Vector3f", Vector3f.class);
		scanner.put("Vector3i", Vec3i.class);
		scanner.put("Vector4f", Vector4f.class);
		scanner.put("VertexFormat", VertexFormat.class);
		scanner.put("VertexFormatElement", VertexFormatElement.class);
		scanner.put("VertexFormats", DefaultVertexFormat.class, "VertexFormats");
		scanner.put("VoxelShape", VoxelShape.class);
		scanner.put("VoxelShapes", Shapes.class, "VoxelShapes");
		scanner.put("Window", Window.class);
		scanner.put("World", Level.class);
		scanner.put("WorldAccess", LevelAccessor.class);
		scanner.put("WorldChunk", LevelChunk.class);
		scanner.put("WorldRenderer", LevelRenderer.class);
		scanner.put("WorldSavePath", LevelResource.class);
		scanner.putAbstract("AbstractSoundInstance", AbstractSoundInstance.class);
		scanner.putAbstract("BillboardParticle", SingleQuadParticle.class);
		scanner.putAbstract("Block", Block.class, "getRespawnPosition", "isValidSpawn");
		scanner.putAbstract("BlockEntity", BlockEntity.class, "getType");
		scanner.putAbstract("BlockItem", BlockItem.class);
		scanner.putAbstract("ButtonWidget", Button.class);
		scanner.putAbstract("CheckboxWidget", Checkbox.class);
		scanner.putAbstract("ClickableWidget", AbstractWidget.class);
		scanner.putAbstract("DoorBlock", DoorBlock.class, "getRespawnPosition", "isValidSpawn");
		scanner.putAbstract("Entity", Entity.class, "getType");
		scanner.putAbstract("EntityModel", EntityModel.class);
		scanner.putAbstract("Item", Item.class);
		scanner.putAbstract("Model", Model.class);
		scanner.putAbstract("MovingSoundInstance", AbstractTickableSoundInstance.class);
		scanner.putAbstract("Particle", Particle.class);
		scanner.putAbstract("PersistentState", SavedData.class);
		scanner.putAbstract("PlaceableOnWaterItem", PlaceOnWaterBlockItem.class);
		scanner.putAbstract("PressableWidget", AbstractButton.class);
		scanner.putAbstract("Screen", Screen.class);
		scanner.putAbstract("SlabBlock", SlabBlock.class, "getRespawnPosition", "isValidSpawn");
		scanner.putAbstract("SliderWidget", AbstractSliderButton.class);
		scanner.putAbstract("SpriteBillboardParticle", TextureSheetParticle.class);
		scanner.putAbstract("StairsBlock", StairBlock.class, "getRespawnPosition", "isValidSpawn");
		scanner.putAbstract("TextFieldWidget", EditBox.class);
		scanner.putAbstract("TexturedButtonWidget", ImageButton.class);
		scanner.putAbstract("ToggleButtonWidget", StateSwitchingButton.class);
		scanner.putInterface("BlockColorProvider", BlockColor.class);
		scanner.putInterface("ItemColorProvider", ItemColor.class);
		scanner.putInterface("PressAction", Button.OnPress.class);
		scanner.putInterface("StringIdentifiable", StringRepresentable.class);
		scanner.putInterface("TickableSoundInstance", TickableSoundInstance.class);
		scanner.generate();
	}
}
