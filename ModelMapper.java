package @package@;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.Random;

public class ModelMapper {

	private float tempPivotX, tempPivotY, tempPivotZ;
	private float tempRotationX, tempRotationY, tempRotationZ;
	private int tempU, tempV;
	private ModelPart modelPart;
	private PartDefinition modelPartData;
	private ModelMapper parent;

	public final String name;
	private final ModelDataWrapper modelDataWrapper;

	public ModelMapper(ModelDataWrapper modelDataWrapper) {
		this.modelDataWrapper = modelDataWrapper;
		name = getRandomPartName();
	}

	public void setPos(float x, float y, float z) {
		tempPivotX = x;
		tempPivotY = y;
		tempPivotZ = z;
	}

	public ModelMapper texOffs(int u, int v) {
		tempU = u;
		tempV = v;
		return this;
	}

	public void setRotationAngle(float rotationX, float rotationY, float rotationZ) {
		tempRotationX = rotationX;
		tempRotationY = rotationY;
		tempRotationZ = rotationZ;
	}

	public void addChild(ModelMapper modelMapper) {
		modelMapper.parent = this;
	}

	public void addBox(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflation, boolean mirrored) {
		final CubeListBuilder modelPartBuilder = CubeListBuilder.create().mirror(mirrored).addBox(name, x, y, z, sizeX, sizeY, sizeZ, new CubeDeformation(inflation), tempU, tempV);
		final PartPose modelTransform = PartPose.offsetAndRotation(tempPivotX, tempPivotY, tempPivotZ, tempRotationX, tempRotationY, tempRotationZ);
		if (parent != null) {
			if (parent.modelPartData == null) {
				parent.addBox(0, 0, 0, 0, 0, 0, 0, false);
			}
			modelPartData = parent.modelPartData.addOrReplaceChild(name, modelPartBuilder, modelTransform);
			parent = null;
		} else {
			if (modelPartData == null) {
				modelPartData = modelDataWrapper.modelPartData.addOrReplaceChild(name, modelPartBuilder, modelTransform);
			} else {
				modelPartData.addOrReplaceChild(getRandomPartName(), modelPartBuilder, PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0));
			}
		}
	}

	public void setModelPart() {
		modelPart = modelDataWrapper.modelPart.getChild(name);
	}

	public void setModelPart(String child) {
		modelPart = modelDataWrapper.modelPart.getChild(child).getChild(name);
	}

	public void setOffset(float x, int y, float z) {
		modelPart.setPos(x, y, z);
	}

	public void render(PoseStack matrices, VertexConsumer vertices, float x, float z, float rotateY, int light, int overlay) {
		modelPart.setPos(x, 0, z);
		modelPart.yRot = rotateY;
		modelPart.render(matrices, vertices, light, overlay);
	}

	private static String getRandomPartName() {
		return "part" + Math.abs(new Random().nextLong());
	}
}
