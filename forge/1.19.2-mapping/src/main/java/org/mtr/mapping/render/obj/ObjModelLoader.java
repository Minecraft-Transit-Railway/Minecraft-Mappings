package org.mtr.mapping.render.obj;

import de.javagl.obj.*;
import org.apache.commons.lang3.StringUtils;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.Face;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ObjModelLoader {

	@Nullable
	public static RawModel loadModel(Identifier objLocation, AtlasManager atlasManager) {
		final RawModel[] rawModel = {null};

		ResourceManagerHelper.readResource(objLocation, inputStream -> {
			try {
				final Obj sourceObj = ObjReader.read(inputStream);
				final Map<String, Mtl> materials = loadMaterials(sourceObj, objLocation);
				rawModel[0] = loadModel(sourceObj, objLocation, materials, atlasManager);
			} catch (Exception e) {
				DummyClass.logException(e);
			}
		});

		return rawModel[0];
	}

	public static Map<String, RawModel> loadModels(Identifier objLocation, AtlasManager atlasManager) {
		final Map<String, RawModel> result = new HashMap<>();

		ResourceManagerHelper.readResource(objLocation, inputStream -> {
			try {
				final Obj sourceObj = ObjReader.read(inputStream);
				final Map<String, Mtl> materials = loadMaterials(sourceObj, objLocation);
				ObjSplitting.splitByGroups(sourceObj).forEach((key, obj) -> result.put(key, loadModel(obj, objLocation, materials, atlasManager)));
			} catch (Exception e) {
				DummyClass.logException(e);
			}
		});

		return result;
	}

	private static RawModel loadModel(Obj sourceObj, @Nullable Identifier objLocation, @Nullable Map<String, Mtl> materials, @Nullable AtlasManager atlasManager) {
		final RawModel model = new RawModel();

		ObjSplitting.splitByMaterialGroups(sourceObj).forEach((key, obj) -> {
			if (obj.getNumFaces() > 0) {
				final Map<String, String> materialOptions = splitMaterialOptions(key);
				final String materialGroupName = materialOptions.get("");
				final OptimizedModel.ShaderType shaderType = OptimizedModel.ShaderType.valueOf(materialOptions.getOrDefault("#", "cutout").toUpperCase(Locale.ENGLISH));
				final boolean flipTextureV = materialOptions.getOrDefault("flipv", "0").equals("1");
				final Identifier texture;
				final Integer color;

				if (materials != null && !materials.isEmpty() && objLocation != null) {
					final Mtl objMaterial = materials.getOrDefault(key, null);
					if (objMaterial == null) {
						texture = new Identifier("");
						color = null;
					} else {
						if (StringUtils.isEmpty(objMaterial.getMapKd())) {
							texture = new Identifier("");
						} else {
							texture = resolveRelativePath(objLocation, objMaterial.getMapKd(), ".png");
						}
						final FloatTuple kd = objMaterial.getKd();
						color = kd == null ? mergeColor(0xFF, 0xFF, 0xFF, 0xFF) : mergeColor((int) (kd.getX() * 0xFF), (int) (kd.getY() * 0xFF), (int) (kd.getZ() * 0xFF), (int) (objMaterial.getD() * 0xFF));
					}
				} else if (objLocation != null) {
					texture = materialGroupName.equals("_") ? new Identifier("") : resolveRelativePath(objLocation, materialGroupName, ".png");
					color = mergeColor(0xFF, 0xFF, 0xFF, 0xFF);
				} else {
					texture = new Identifier("");
					color = mergeColor(0xFF, 0xFF, 0xFF, 0xFF);
				}

				final Obj renderObjMesh = ObjUtils.convertToRenderable(obj);
				final RawMesh mesh = new RawMesh(new MaterialProperties(shaderType, texture, color));

				for (int i = 0; i < renderObjMesh.getNumVertices(); i++) {
					final FloatTuple pos = renderObjMesh.getVertex(i);
					final FloatTuple normal;
					final FloatTuple uv;
					if (i < renderObjMesh.getNumNormals()) {
						normal = renderObjMesh.getNormal(i);
					} else {
						normal = ZeroFloatTuple.ZERO3;
					}
					if (i < renderObjMesh.getNumTexCoords()) {
						uv = renderObjMesh.getTexCoord(i);
					} else {
						uv = ZeroFloatTuple.ZERO2;
					}
					final Vertex seVertex = new Vertex();
					seVertex.position = new Vector3f(pos.getX(), pos.getY(), pos.getZ());
					seVertex.normal = new Vector3f(normal.getX(), normal.getY(), normal.getZ());
					seVertex.u = uv.getX();
					seVertex.v = flipTextureV ? 1 - uv.getY() : uv.getY();
					mesh.vertices.add(seVertex);
				}

				for (int i = 0; i < renderObjMesh.getNumFaces(); i++) {
					final ObjFace face = renderObjMesh.getFace(i);
					mesh.faces.add(new Face(new int[]{face.getVertexIndex(0), face.getVertexIndex(1), face.getVertexIndex(2)}));
				}

				if (atlasManager != null) {
					atlasManager.applyToMesh(mesh);
				}

				mesh.validateVertexIndex();
				model.append(mesh);
			}
		});

		model.generateNormals();
		model.distinct();
		return model;
	}

	private static Map<String, Mtl> loadMaterials(Obj sourceObj, Identifier objLocation) throws IOException {
		final Map<String, Mtl> materials = new HashMap<>();
		sourceObj.getMtlFileNames().forEach(mtlFileName -> ResourceManagerHelper.readResource(resolveRelativePath(objLocation, mtlFileName, ".mtl"), inputStream -> {
			try {
				MtlReader.read(inputStream).forEach(mtl -> materials.put(mtl.getName(), mtl));
			} catch (Exception e) {
				DummyClass.logException(e);
			}
		}));
		return materials;
	}

	private static Map<String, String> splitMaterialOptions(String source) {
		final Map<String, String> result = new HashMap<>();
		final String[] majorParts = source.split("#", 2);
		result.put("", majorParts[0]);
		if (majorParts.length > 1) {
			for (final String minorPart : majorParts[1].split(",")) {
				final String[] tokens = minorPart.split("=", 2);
				if (tokens.length > 1) {
					result.put(tokens[0], tokens[1]);
				} else if (!result.containsKey("#")) {
					result.put("#", tokens[0]);
				} else {
					result.put(tokens[0].toLowerCase(Locale.ROOT), "1");
				}
			}
		}
		return result;
	}

	private static int mergeColor(int r, int g, int b, int a) {
		return r << 24 | g << 16 | b << 8 | a;
	}

	public static Identifier resolveRelativePath(Identifier baseFile, String relative, @Nullable String expectExtension) {
		String result = relative.toLowerCase(Locale.ROOT).replace('\\', '/');

		if (result.contains(":")) {
			result = result.replaceAll("[^a-z0-9/.:_-]", "_");
			return new Identifier(result);
		}

		result = result.replaceAll("[^a-z0-9/._-]", "_");

		if (result.endsWith(".jpg") || result.endsWith(".bmp") || result.endsWith(".tga")) {
			result = result.substring(0, result.length() - 4) + ".png";
		}

		if (expectExtension != null && !result.endsWith(expectExtension)) {
			result += expectExtension;
		}

		return new Identifier(baseFile.getNamespace(), FileSystems.getDefault().getPath(baseFile.getPath()).getParent().resolve(result).normalize().toString().replace('\\', '/'));
	}

	private static class ZeroFloatTuple implements FloatTuple {

		private final int dimensions;

		public static final ZeroFloatTuple ZERO2 = new ZeroFloatTuple(2);
		public static final ZeroFloatTuple ZERO3 = new ZeroFloatTuple(3);

		public ZeroFloatTuple(int dimensions) {
			this.dimensions = dimensions;
		}

		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public float getZ() {
			return 0;
		}

		@Override
		public float getW() {
			return 0;
		}

		@Override
		public float get(int index) {
			return 0;
		}

		@Override
		public int getDimensions() {
			return dimensions;
		}
	}
}