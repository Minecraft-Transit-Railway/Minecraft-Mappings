package @package@;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeUtilities {

	private static Runnable renderTickAction = () -> {
	};
	private static Consumer<Object> renderGameOverlayAction = matrices -> {
	};
	private static final Set<EntityRendererPair<?>> ENTITY_RENDERER_PAIRS = new HashSet<>();

	public static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}

	public static void registerKeyBinding(KeyMapping keyMapping) {
		KeyMappingRegistry.register(keyMapping);
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

	public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> entityType, EntityRendererProvider<T> entityRendererProvider) {
		ENTITY_RENDERER_PAIRS.add(new EntityRendererPair<>(entityType, entityRendererProvider));
	}

	public static class Events {

		@SubscribeEvent
		public static void onRenderTickEvent(RenderLevelStageEvent event) {
			if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
				renderTickAction.run();
			}
		}

		@SubscribeEvent
		public static void onRenderGameOverlayEvent(RenderGuiOverlayEvent.Post event) {
			renderGameOverlayAction.accept(event.getPoseStack());
		}
	}

	public static class RegisterEntityRenderer {

		@SubscribeEvent
		public static void onEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
			ENTITY_RENDERER_PAIRS.forEach(entityRendererPair -> entityRendererPair.register(event));
		}
	}

	private static class EntityRendererPair<T extends Entity> {

		private final Supplier<EntityType<? extends T>> entityTypeSupplier;
		private final EntityRendererProvider<T> entityRendererProvider;

		private EntityRendererPair(Supplier<EntityType<? extends T>> entityTypeSupplier, EntityRendererProvider<T> entityRendererProvider) {
			this.entityTypeSupplier = entityTypeSupplier;
			this.entityRendererProvider = entityRendererProvider;
		}

		private void register(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(entityTypeSupplier.get(), entityRendererProvider);
		}
	}
}
