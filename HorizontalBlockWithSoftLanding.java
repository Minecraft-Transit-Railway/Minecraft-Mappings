package @package@;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class HorizontalBlockWithSoftLanding extends BlockDirectionalMapper {

	public HorizontalBlockWithSoftLanding(Properties settings) {
		super(settings);
	}

	@Override
	public final void fallOn(Level world, BlockPos pos, Entity entity, float distance) {
		super.fallOn(world, pos, entity, distance * (softenLanding() ? 0.5F : 1));
	}

	public boolean softenLanding() {
		return false;
	}
}
