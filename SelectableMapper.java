package minecraftmappings;

import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public interface SelectableMapper extends NarratableEntry {

	@Override
	default NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	default void updateNarration(NarrationElementOutput narrationElementOutput) {
	}
}
