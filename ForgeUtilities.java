package @package@;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;

public interface ForgeUtilities {

	static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}
}
