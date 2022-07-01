package @package@;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeUtilities {

	private static Runnable renderTickAction = () -> {
	};
	private static Consumer<Object> renderGameOverlayAction = matrices -> {
	};

	public static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}

	public static void registerKeyBinding(KeyMapping keyMapping) {
		ClientRegistry.registerKeyBinding(keyMapping);
	}

	public static Packet<?> createAddEntityPacket(Entity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}

	public static void renderTickAction(Runnable runnable) {
		renderTickAction = runnable;
	}

	public static void renderGameOverlayAction(Consumer<Object> consumer) {
		renderGameOverlayAction = consumer;
	}

	public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> entityType, Function<Object, EntityRendererMapper<T>> entityRendererProvider) {
	}

	public static class Events {

		@SubscribeEvent
		public static void onRenderTickEvent(net.minecraftforge.client.event.RenderWorldLastEvent event) {
			renderTickAction.run();
		}

		@SubscribeEvent
		public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
			renderGameOverlayAction.accept(event.getMatrixStack());
		}
	}

	public static class RegisterEntityRenderer {
	}
}
