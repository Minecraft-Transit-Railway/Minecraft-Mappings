package org.mtr.mapping.mapper;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.tool.Dummy;

public final class TextHelper extends Dummy {

	@MappedMethod
	public static MutableText translatable(String key, Object... arguments) {
		return new MutableText(new TranslatableComponent(key, arguments));
	}

	@MappedMethod
	public static MutableText literal(String key) {
		return new MutableText(new TextComponent(key));
	}
}
