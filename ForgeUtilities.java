package @package@;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Function;

public interface ForgeUtilities {

	static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}

	static Packet<?> createAddEntityPacket(Entity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}

	static <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, Function<Object, EntityRendererMapper<T>> entityRendererProvider) {
	}

	class RegisterEntityRenderer {
	}
}
