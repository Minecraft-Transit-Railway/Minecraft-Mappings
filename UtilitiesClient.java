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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;

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

	static boolean isHovered(AbstractWidget widget) {
		return widget.isHovered();
	}
}
