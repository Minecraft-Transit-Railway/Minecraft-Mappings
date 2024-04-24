package org.mtr.mapping.mapper;

import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.OrderedText;
import org.mtr.mapping.holder.Style;
import org.mtr.mapping.tool.DummyClass;

public final class TextHelper extends DummyClass {

	@MappedMethod
	public static MutableText translatable(String key, Object... arguments) {
		return new MutableText(Text.translatable(key, arguments));
	}

	@MappedMethod
	public static MutableText literal(String key) {
		return new MutableText(Text.literal(key));
	}

	@MappedMethod
	public static MutableText setStyle(MutableText mutableText, Style style) {
		return mutableText.setStyle(style);
	}

	@MappedMethod
	public static OrderedText mutableTextToOrderedText(MutableText mutableText) {
		return mutableText.asOrderedText();
	}

	@MappedMethod
	public static MutableText append(MutableText baseText, MutableText... moreText) {
		MutableText result = baseText;
		for (final MutableText mutableText : moreText) {
			result = result.append(new org.mtr.mapping.holder.Text(mutableText.data));
		}
		return result;
	}
}
