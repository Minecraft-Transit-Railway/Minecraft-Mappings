package minecraftmappings;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public abstract class BlockEntityRendererMapper<T extends BlockEntityMapper> implements BlockEntityRenderer<T> {

	public BlockEntityRendererMapper(BlockEntityRenderDispatcher dispatcher) {
	}
}
