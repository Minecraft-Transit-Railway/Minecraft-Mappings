package @package@;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public abstract class EntityRendererMapper<T extends Entity> extends EntityRenderer<T> {

	public EntityRendererMapper(Object parameter) {
		super((EntityRendererProvider.Context) parameter);
	}
}
