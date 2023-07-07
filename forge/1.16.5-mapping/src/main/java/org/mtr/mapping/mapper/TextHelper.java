package org.mtr.mapping.mapper;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.tool.Dummy;

public final class TextHelper extends Dummy {

	@MappedMethod
	public static MutableText translatable(String key, Object... arguments) {
		return new MutableText(new TranslationTextComponent(key, arguments));
	}

	@MappedMethod
	public static MutableText literal(String key) {
		return new MutableText(new StringTextComponent(key));
	}
}
