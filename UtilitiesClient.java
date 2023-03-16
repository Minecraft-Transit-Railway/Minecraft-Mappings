package @package@;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import org.joml.Matrix4f;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface UtilitiesClient {

	static void beginDrawingRectangle(BufferBuilder buffer) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
	}

	static void finishDrawingRectangle() {
	}

	static void beginDrawingTexture(ResourceLocation textureId) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, textureId);
	}

	static int drawInBatch(Font textRenderer, FormattedCharSequence formattedCharSequence, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource immediate, int overlay, int light) {
		return textRenderer.drawInBatch(formattedCharSequence, x, y, color, shadow, matrix4f, immediate, Font.DisplayMode.NORMAL, overlay, light);
	}

	static void setScreen(Minecraft client, ScreenMapper screen) {
		client.setScreen(screen);
	}

	static EntityModel<Minecart> getMinecartModel() {
		return new MinecartModel<>(MinecartModel.createBodyLayer().bakeRoot());
	}

	static EntityModel<Boat> getBoatModel() {
		return new BoatModel(BoatModel.createBodyModel().bakeRoot());
	}

	static void setPacketCoordinates(Entity entity, double x, double y, double z) {
		entity.syncPacketPositionCodec(x, y, z);
	}

	static float getPacketYaw(ClientboundAddEntityPacket packet) {
		return packet.getYRot();
	}

	static int getRenderDistance() {
		return Minecraft.getInstance().options.renderDistance().get();
	}

	static List<Resource> getResources(ResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
		return resourceManager.getResourceStack(resourceLocation);
	}

	static boolean hasResource(ResourceLocation resourceLocation) {
		return Minecraft.getInstance().getResourceManager().getResource(resourceLocation).isPresent();
	}

	static boolean isHovered(AbstractWidget widget) {
		return widget.isHoveredOrFocused();
	}

	static File getResourcePackDirectory(Minecraft minecraft) {
		return minecraft.getResourcePackDirectory().toFile();
	}

	static Button newButton(Button.OnPress onPress) {
		return newButton(Text.literal(""), onPress);
	}

	static Button newButton(Component component, Button.OnPress onPress) {
		return Button.builder(component, onPress).build();
	}

	static Button newButton(int height, Component component, Button.OnPress onPress) {
		return Button.builder(component, onPress).size(0, height).build();
	}

	static int getWidgetX(AbstractWidget widget) {
		return widget.getX();
	}

	static void setWidgetX(AbstractWidget widget, int x) {
		widget.setX(x);
	}

	static void setWidgetY(AbstractWidget widget, int y) {
		widget.setY(y);
	}

	static int getWidgetY(AbstractWidget widget) {
		return widget.getY();
	}

	static void rotateX(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.XP.rotation(angle));
	}

	static void rotateXDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.XP.rotationDegrees(angle));
	}

	static void rotateY(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.YP.rotation(angle));
	}

	static void rotateYDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.YP.rotationDegrees(angle));
	}

	static void rotateZ(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.ZP.rotation(angle));
	}

	static void rotateZDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Axis.ZP.rotationDegrees(angle));
	}
}
