package minecraftmappings;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class ScreenMapper extends Screen {

	protected ScreenMapper(Text title) {
		super(title);
	}

	public <T extends Element & Drawable & Selectable> void addChild(T widgetMap) {
		addDrawableChild(widgetMap);
	}
}
