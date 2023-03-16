package @package@;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface UtilitiesClient {

	static void beginDrawingRectangle(BufferBuilder buffer) {
		RenderSystem.disableTexture();
		buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);
	}

	static void finishDrawingRectangle() {
		RenderSystem.enableTexture();
	}

	static void beginDrawingTexture(ResourceLocation textureId) {
		Minecraft.getInstance().getTextureManager().bind(textureId);
	}

	static int drawInBatch(Font textRenderer, FormattedCharSequence formattedCharSequence, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource immediate, int overlay, int light) {
		return textRenderer.drawInBatch(formattedCharSequence, x, y, color, shadow, matrix4f, immediate, false, overlay, light);
	}

	static void setScreen(Minecraft client, ScreenMapper screen) {
		client.setScreen(screen);
	}

	static EntityModel<Minecart> getMinecartModel() {
		return new MinecartModel<>();
	}

	static EntityModel<Boat> getBoatModel() {
		return new BoatModel();
	}

	static void setPacketCoordinates(Entity entity, double x, double y, double z) {
		entity.setPacketCoordinates(x, y, z);
	}

	static float getPacketYaw(ClientboundAddEntityPacket packet) {
		return packet.getyRot() * 360F / 256;
	}

	static int getRenderDistance() {
		return Minecraft.getInstance().options.renderDistance;
	}

	static List<Resource> getResources(ResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
		return resourceManager.getResources(resourceLocation);
	}

	static boolean hasResource(ResourceLocation resourceLocation) {
		return Minecraft.getInstance().getResourceManager().hasResource(resourceLocation);
	}

	static boolean isHovered(AbstractWidget widget) {
		return widget.isHovered();
	}

	static File getResourcePackDirectory(Minecraft minecraft) {
		return minecraft.getResourcePackDirectory();
	}

	static Button newButton(Button.OnPress onPress) {
		return newButton(Text.literal(""), onPress);
	}

	static Button newButton(Component component, Button.OnPress onPress) {
		return new Button(0, 0, 0, 20, component, onPress);
	}

	static Button newButton(int height, Component component, Button.OnPress onPress) {
		return new Button(0, 0, 0, height, component, onPress);
	}

	static int getWidgetX(AbstractWidget widget) {
		return widget.x;
	}

	static void setWidgetX(AbstractWidget widget, int x) {
		widget.x = x;
	}

	static void setWidgetY(AbstractWidget widget, int y) {
		widget.y = y;
	}

	static int getWidgetY(AbstractWidget widget) {
		return widget.y;
	}

	static void rotateX(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.XP.rotation(angle));
	}

	static void rotateXDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.XP.rotationDegrees(angle));
	}

	static void rotateY(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.YP.rotation(angle));
	}

	static void rotateYDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.YP.rotationDegrees(angle));
	}

	static void rotateZ(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.ZP.rotation(angle));
	}

	static void rotateZDegrees(PoseStack matrices, float angle) {
		matrices.mulPose(Vector3f.ZP.rotationDegrees(angle));
	}
}
