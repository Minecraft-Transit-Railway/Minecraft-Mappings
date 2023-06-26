package org.mtr.mapping.mapper;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.TextFormatting;
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

	@MappedMethod
	public static MutableText format(MutableText mutableText, TextFormatting... textFormattingList) {
		final net.minecraft.util.text.TextFormatting[] formatting = new net.minecraft.util.text.TextFormatting[textFormattingList.length];
		for (int i = 0; i < textFormattingList.length; i++) {
			formatting[i] = textFormattingList[i].data;
		}
		return mutableText.withStyle(formatting);
	}
}
