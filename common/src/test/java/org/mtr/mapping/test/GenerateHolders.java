package org.mtr.mapping.test;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

public final class GenerateHolders {

	private final Map<Class<?>, String> classMap = new HashMap<>();
	private static final Path PATH = Paths.get("@path@");

	public void put(Class<?> classObject, String newClassName) {
		classMap.put(classObject, newClassName);
	}

	public void generate() throws IOException {
		FileUtils.deleteDirectory(PATH.toFile());

		for (Map.Entry<Class<?>, String> classEntry : classMap.entrySet()) {
			final Class<?> classObject = classEntry.getKey();
			final String newClassName = classEntry.getValue();
			final StringBuilder mainStringBuilder = new StringBuilder("package org.mtr.mapping.holder;public final class ");
			mainStringBuilder.append(newClassName);

			appendIfNotEmpty(mainStringBuilder, classObject.getTypeParameters(), "<", ">", typeVariable -> {
				final StringBuilder extendsStringBuilder = new StringBuilder();
				appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", Type::getTypeName);
				return String.format("%s%s", typeVariable.getName(), extendsStringBuilder);
			});

			final String staticClassName = formatClassName(classObject.getName());
			final StringBuilder classNameStringBuilder = new StringBuilder(staticClassName);
			appendIfNotEmpty(classNameStringBuilder, classObject.getTypeParameters(), "<", ">", TypeVariable::getName);
			final String className = classNameStringBuilder.toString();
			mainStringBuilder.append("{public final ").append(className).append(" data;public ").append(newClassName).append("(").append(className).append(" data){this.data=data;}");

			if (!Modifier.isAbstract(classObject.getModifiers())) {
				processMethods(classObject.getDeclaredConstructors(), mainStringBuilder, className, staticClassName, newClassName);
			}

			processMethods(classObject.getDeclaredMethods(), mainStringBuilder, className, staticClassName, newClassName);
			mainStringBuilder.append("}");
			Files.createDirectories(PATH);
			Files.write(PATH.resolve(String.format("%s.java", newClassName)), mainStringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}

	private void processMethods(Executable[] executables, StringBuilder mainStringBuilder, String className, String staticClassName, String newClassName) {
		final Set<String> visited = new HashSet<>();

		for (final Executable executable : executables) {
			final StringBuilder signatureStringBuilder = new StringBuilder();
			final List<String> parameterList = new ArrayList<>();
			final List<String> superList = new ArrayList<>();
			boolean allPublicParameterTypes = true;

			for (final Parameter parameter : executable.getParameters()) {
				signatureStringBuilder.append(parameter.getParameterizedType()).append(" ");
				parameterList.add(String.format("%s %s", resolveType(parameter.getParameterizedType()), parameter.getName()));
				superList.add(String.format("%s%s", parameter.getName(), isResolvable(parameter.getParameterizedType()) ? ".data" : ""));
				if (!Modifier.isPublic(parameter.getType().getModifiers())) {
					allPublicParameterTypes = false;
				}
			}

			final String signature = signatureStringBuilder.toString();
			final int modifiers = executable.getModifiers();

			if (allPublicParameterTypes && !executable.isSynthetic() && !visited.contains(signature) && Modifier.isPublic(modifiers) && executable.getDeclaredAnnotations().length == 0) {
				visited.add(signature);
				mainStringBuilder.append("public ");
				final boolean isStatic = Modifier.isStatic(modifiers);
				final boolean isMethod = executable instanceof Method;
				final boolean isVoid;
				final Type returnTypeClass;

				if (isStatic) {
					mainStringBuilder.append("static ");
				}

				if (Modifier.isFinal(modifiers)) {
					mainStringBuilder.append("final ");
				}

				if (isMethod) {
					appendIfNotEmpty(mainStringBuilder, ((Method) executable).getTypeParameters(), "<", ">", typeVariable -> {
						final StringBuilder typeParametersStringBuilder = new StringBuilder();
						appendIfNotEmpty(typeParametersStringBuilder, typeVariable.getBounds(), " extends ", "", Type::getTypeName);
						return String.format("%s%s", typeVariable.getName(), typeParametersStringBuilder);
					});
					returnTypeClass = ((Method) executable).getGenericReturnType();
					isVoid = returnTypeClass == Void.TYPE;
					mainStringBuilder.append(resolveType(returnTypeClass)).append(" ").append(executable.getName());
				} else {
					returnTypeClass = null;
					isVoid = true;
					mainStringBuilder.append(newClassName);
				}

				mainStringBuilder.append("(");
				mainStringBuilder.append(String.join(",", parameterList)).append(")");
				appendIfNotEmpty(mainStringBuilder, executable.getGenericExceptionTypes(), "throws ", "", Type::getTypeName);
				mainStringBuilder.append("{");
				final boolean addClosing;

				if (isMethod) {
					if (isVoid) {
						addClosing = false;
					} else {
						mainStringBuilder.append("return ");
						if (isResolvable(returnTypeClass)) {
							mainStringBuilder.append("new ").append(resolveType(returnTypeClass)).append("(");
							addClosing = true;
						} else {
							addClosing = false;
						}
					}
					mainStringBuilder.append(isStatic ? staticClassName : "data").append(".").append(executable.getName());
				} else {
					mainStringBuilder.append("data=new ").append(className);
					addClosing = false;
				}

				mainStringBuilder.append("(").append(String.join(",", superList));

				if (addClosing) {
					mainStringBuilder.append(")");
				}

				mainStringBuilder.append(");}");
			}
		}
	}

	private static <T> void appendIfNotEmpty(StringBuilder stringBuilder, T[] array, String prefix, String suffix, Function<T, String> callback) {
		if (array.length > 0) {
			stringBuilder.append(prefix);
			final List<String> dataList = new ArrayList<>();
			for (T data : array) {
				dataList.add(callback.apply(data));
			}
			stringBuilder.append(String.join(",", dataList)).append(suffix);
		}
	}

	private String resolveType(Type type) {
		return formatClassName(isResolvable(type) && type instanceof Class ? classMap.get(type) : type.getTypeName());
	}

	private boolean isResolvable(Type type) {
		return type instanceof Class && classMap.containsKey(type);
	}

	private static String formatClassName(String className) {
		return className.replace("$", ".");
	}
}
