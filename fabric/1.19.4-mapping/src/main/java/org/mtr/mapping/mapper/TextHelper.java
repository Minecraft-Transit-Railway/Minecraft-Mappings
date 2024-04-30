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
		return new MutableText(mutableText.data.setStyle(style.data));
	}

	@MappedMethod
	public static OrderedText mutableTextToOrderedText(MutableText mutableText) {
		return new OrderedText(mutableText.data.asOrderedText());
	}

	@MappedMethod
	public static MutableText append(MutableText baseText, MutableText... moreText) {
		net.minecraft.text.MutableText result = baseText.data;
		for (final MutableText mutableText : moreText) {
			result = result.append(mutableText.data);
		}
		return new MutableText(result);
	}
}
