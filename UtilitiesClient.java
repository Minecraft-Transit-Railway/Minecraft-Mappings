package @package@;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;

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
		return widget.isHoveredOrFocused();
	}
}
