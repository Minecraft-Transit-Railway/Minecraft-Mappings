package org.mtr.mapping.tool;

import org.mtr.mapping.annotation.MappedMethod;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class HolderBase<T> {

	public final T data;

	public HolderBase(T data) {
		this.data = data;
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> List<U> convertCollection(List<T> list, Function<T, U> newInstance) {
		return list.stream().map(newInstance).collect(Collectors.toList());
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> Set<U> convertCollection(Set<T> set, Function<T, U> newInstance) {
		return set.stream().map(newInstance).collect(Collectors.toSet());
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> List<T> convertCollection(List<U> list) {
		return list.stream().map(data -> data.data).collect(Collectors.toList());
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> Set<T> convertCollection(Set<U> set) {
		return set.stream().map(data -> data.data).collect(Collectors.toSet());
	}

	@MappedMethod
	public static <T, U extends Enum<U>> List<U> convertEnumCollection(List<T> list, Function<T, U> newInstance) {
		return list.stream().map(newInstance).collect(Collectors.toList());
	}

	@MappedMethod
	public static <T, U extends Enum<U>> Set<U> convertEnumCollection(Set<T> set, Function<T, U> newInstance) {
		return set.stream().map(newInstance).collect(Collectors.toSet());
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> T[] convertArray(U[] array, IntFunction<T[]> supplier) {
		final T[] newArray = supplier.apply(array.length);
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i].data;
		}
		return newArray;
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> T[] convertArray(Supplier<U>[] array, IntFunction<T[]> supplier) {
		final T[] newArray = supplier.apply(array.length);
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i].get().data;
		}
		return newArray;
	}

	@MappedMethod
	public static <T, U extends HolderBase<T>> U[] convertArray(T[] array, IntFunction<U[]> supplier, Function<T, U> newInstance) {
		final U[] newArray = supplier.apply(array.length);
		for (int i = 0; i < array.length; i++) {
			newArray[i] = newInstance.apply(array[i]);
		}
		return newArray;
	}
}
