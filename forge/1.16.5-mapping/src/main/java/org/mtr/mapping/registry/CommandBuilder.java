package org.mtr.mapping.registry;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public final class CommandBuilder<T extends ArgumentBuilder<CommandSource, T>> {

	T argumentBuilder;

	CommandBuilder(T argumentBuilder) {
		this.argumentBuilder = argumentBuilder;
	}

	@MappedMethod
	public void permissionLevel(int permissionLevel) {
		argumentBuilder = argumentBuilder.requires(serverCommandSource -> serverCommandSource.hasPermission(permissionLevel));
	}

	@MappedMethod
	public <U> void then(String argumentName, ArgumentType<U> argumentType, Consumer<CommandBuilder<?>> consumer) {
		final CommandBuilder<RequiredArgumentBuilder<CommandSource, U>> commandBuilder = new CommandBuilder<>(Commands.argument(argumentName, argumentType));
		consumer.accept(commandBuilder);
		argumentBuilder = argumentBuilder.then(commandBuilder.argumentBuilder);
	}

	@MappedMethod
	public void executes(ToIntFunction<ContextHandler> execute) {
		argumentBuilder = argumentBuilder.executes(context -> execute.applyAsInt(new ContextHandler(context)));
	}

	public static class ContextHandler {

		private final CommandContext<CommandSource> context;

		private ContextHandler(CommandContext<CommandSource> context) {
			this.context = context;
		}

		@MappedMethod
		public boolean getBoolean(String argumentName) {
			return BoolArgumentType.getBool(context, argumentName);
		}

		@MappedMethod
		public double getDouble(String argumentName) {
			return DoubleArgumentType.getDouble(context, argumentName);
		}

		@MappedMethod
		public float getFloat(String argumentName) {
			return FloatArgumentType.getFloat(context, argumentName);
		}

		@MappedMethod
		public int getInteger(String argumentName) {
			return IntegerArgumentType.getInteger(context, argumentName);
		}

		@MappedMethod
		public long getLong(String argumentName) {
			return LongArgumentType.getLong(context, argumentName);
		}

		@MappedMethod
		public String getString(String argumentName) {
			return StringArgumentType.getString(context, argumentName);
		}

		@MappedMethod
		public void sendSuccess(String message, boolean broadcastToOps, Object... translatableArguments) {
			context.getSource().sendSuccess(new TranslationTextComponent(message), broadcastToOps);
		}

		@MappedMethod
		public void sendFailure(String message, Object... translatableArguments) {
			context.getSource().sendFailure(new TranslationTextComponent(message));
		}

		@MappedMethod
		public MinecraftServer getServer() {
			return new MinecraftServer(context.getSource().getServer());
		}

		@MappedMethod
		public World getWorld() {
			return new World(context.getSource().getLevel());
		}

		@MappedMethod
		@Nullable
		public ServerPlayerEntity getServerPlayer() {
			net.minecraft.entity.player.ServerPlayerEntity serverPlayerEntity = null;
			try {
				serverPlayerEntity = context.getSource().getPlayerOrException();
			} catch (Exception e) {
				DummyClass.logException(e);
			}
			return serverPlayerEntity == null ? null : new ServerPlayerEntity(serverPlayerEntity);
		}
	}
}
