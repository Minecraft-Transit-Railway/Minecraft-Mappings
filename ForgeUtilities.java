package @package@;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;

public class ForgeUtilities {

	private static Runnable renderTickAction = () -> {
	};
	private static final Set<EntityRendererPair<?>> ENTITY_RENDERER_PAIRS = new HashSet<>();

	public static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}

	public static Packet<?> createAddEntityPacket(Entity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}

	public static void renderTickAction(Runnable runnable) {
		renderTickAction = runnable;
	}

	public static <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
		ENTITY_RENDERER_PAIRS.add(new EntityRendererPair<>(entityType, entityRendererProvider));
	}

	public static class RenderTick {

		@SubscribeEvent
		public static void onRenderTickEvent(net.minecraftforge.client.event.RenderWorldLastEvent event) {
			renderTickAction.run();
		}
	}

	public static class RegisterEntityRenderer {

		@SubscribeEvent
		public static void onRegisterEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
			ENTITY_RENDERER_PAIRS.forEach(entityRendererPair -> entityRendererPair.register(event));
		}
	}

	private static class EntityRendererPair<T extends Entity> {

		private final EntityType<? extends T> entityType;
		private final EntityRendererProvider<T> entityRendererProvider;

		private EntityRendererPair(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
			this.entityType = entityType;
			this.entityRendererProvider = entityRendererProvider;
		}

		private void register(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(entityType, entityRendererProvider);
		}
	}
}
