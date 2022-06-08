package @package@;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public interface Text {

	static MutableComponent translatable(String text, Object... objects) {
		return new TranslatableComponent(text, objects);
	}

	static MutableComponent literal(String text) {
		return new TextComponent(text);
	}
}
