package @package@;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public abstract class ButtonMapper extends Button {

	public ButtonMapper(int x, int y, int width, int height, Component component, OnPress onPress) {
		super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
	}
}
