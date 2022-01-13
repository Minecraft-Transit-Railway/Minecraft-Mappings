package @package@;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public abstract class EntityRendererMapper<T extends Entity> extends EntityRenderer<T> {

	public EntityRendererMapper(Object parameter) {
		super((EntityRenderDispatcher) parameter);
	}
}
