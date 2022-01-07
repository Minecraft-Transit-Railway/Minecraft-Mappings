package @package@;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;

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

	static void setScreen(Minecraft client, ScreenMapper screen) {
		client.setScreen(screen);
	}

	static EntityModel<Minecart> getMinecartModel() {
		return new MinecartModel<>();
	}

	static EntityModel<Boat> getBoatModel() {
		return new BoatModel();
	}

	static boolean isHovered(AbstractWidget widget) {
		return widget.isHovered();
	}
}
