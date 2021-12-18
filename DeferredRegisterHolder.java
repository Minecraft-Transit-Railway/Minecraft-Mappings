package minecraftmappings;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class DeferredRegisterHolder<T> {

	private final DeferredRegister<T> deferredRegister;

	public DeferredRegisterHolder(String modId, ResourceKey<Registry<T>> key) {
		deferredRegister = DeferredRegister.create(modId, key);
	}

	public void register() {
		deferredRegister.register();
	}

	public void register(String id, Supplier<? extends T> supplier) {
		deferredRegister.register(id, supplier);
	}
}
