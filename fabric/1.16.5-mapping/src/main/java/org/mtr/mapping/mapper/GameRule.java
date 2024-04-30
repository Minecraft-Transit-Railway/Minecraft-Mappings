package org.mtr.mapping.mapper;

import net.minecraft.world.GameRules;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;

import javax.annotation.Nullable;

public enum GameRule {

	ANNOUNCE_ADVANCEMENTS(GameRules.ANNOUNCE_ADVANCEMENTS, null),
	BLOCK_EXPLOSION_DROP_DECAY(null, null),
	COMMAND_BLOCK_OUTPUT(GameRules.COMMAND_BLOCK_OUTPUT, null),
	DISABLE_ELYTRA_MOVEMENT_CHECK(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK, null),
	DISABLE_RAIDS(GameRules.DISABLE_RAIDS, null),
	DO_DAYLIGHT_CYCLE(GameRules.DO_DAYLIGHT_CYCLE, null),
	DO_ENTITY_DROPS(GameRules.DO_ENTITY_DROPS, null),
	DO_FIRE_TICK(GameRules.DO_FIRE_TICK, null),
	DO_IMMEDIATE_RESPAWN(GameRules.DO_IMMEDIATE_RESPAWN, null),
	DO_INSOMNIA(GameRules.DO_INSOMNIA, null),
	DO_LIMITED_CRAFTING(GameRules.DO_LIMITED_CRAFTING, null),
	DO_MOB_GRIEFING(GameRules.DO_MOB_GRIEFING, null),
	DO_MOB_LOOT(GameRules.DO_MOB_LOOT, null),
	DO_MOB_SPAWNING(GameRules.DO_MOB_SPAWNING, null),
	DO_PATROL_SPAWNING(GameRules.DO_PATROL_SPAWNING, null),
	DO_TILE_DROPS(GameRules.DO_TILE_DROPS, null),
	DO_TRADER_SPAWNING(GameRules.DO_TRADER_SPAWNING, null),
	DO_VINES_SPREAD(null, null),
	DO_WARDEN_SPAWNING(null, null),
	DO_WEATHER_CYCLE(GameRules.DO_WEATHER_CYCLE, null),
	DROWNING_DAMAGE(GameRules.DROWNING_DAMAGE, null),
	ENDER_PEARLS_VANISH_ON_DEATH(null, null),
	FALL_DAMAGE(GameRules.FALL_DAMAGE, null),
	FIRE_DAMAGE(GameRules.FIRE_DAMAGE, null),
	FORGIVE_DEAD_PLAYERS(GameRules.FORGIVE_DEAD_PLAYERS, null),
	FREEZE_DAMAGE(null, null),
	GLOBAL_SOUND_EVENTS(null, null),
	KEEP_INVENTORY(GameRules.KEEP_INVENTORY, null),
	LAVA_SOURCE_CONVERSION(null, null),
	LOG_ADMIN_COMMANDS(GameRules.LOG_ADMIN_COMMANDS, null),
	MOB_EXPLOSION_DROP_DECAY(null, null),
	NATURAL_REGENERATION(GameRules.NATURAL_REGENERATION, null),
	PROJECTILES_CAN_BREAK_BLOCKS(null, null),
	REDUCED_DEBUG_INFO(GameRules.REDUCED_DEBUG_INFO, null),
	SEND_COMMAND_FEEDBACK(GameRules.SEND_COMMAND_FEEDBACK, null),
	SHOW_DEATH_MESSAGES(GameRules.SHOW_DEATH_MESSAGES, null),
	SPECTATORS_GENERATE_CHUNKS(GameRules.SPECTATORS_GENERATE_CHUNKS, null),
	TNT_EXPLOSION_DROP_DECAY(null, null),
	UNIVERSAL_ANGER(GameRules.UNIVERSAL_ANGER, null),
	WATER_SOURCE_CONVERSION(null, null),
	COMMAND_MODIFICATION_BLOCK_LIMIT(null, null),
	MAX_COMMAND_CHAIN_LENGTH(null, GameRules.MAX_COMMAND_CHAIN_LENGTH),
	MAX_COMMAND_FORK_COUNT(null, null),
	MAX_ENTITY_CRAMMING(null, GameRules.MAX_ENTITY_CRAMMING),
	PLAYERS_NETHER_PORTAL_CREATIVE_DELAY(null, null),
	PLAYERS_NETHER_PORTAL_DEFAULT_DELAY(null, null),
	PLAYERS_SLEEPING_PERCENTAGE(null, null),
	RANDOM_TICK_SPEED(null, GameRules.RANDOM_TICK_SPEED),
	SNOW_ACCUMULATION_HEIGHT(null, null),
	SPAWN_RADIUS(null, GameRules.SPAWN_RADIUS);


	@Nullable
	private final GameRules.Key<GameRules.BooleanRule> gameRuleBoolean;
	@Nullable
	private final GameRules.Key<GameRules.IntRule> gameRuleInteger;

	@Deprecated
	GameRule(@Nullable GameRules.Key<GameRules.BooleanRule> gameRuleBoolean, @Nullable GameRules.Key<GameRules.IntRule> gameRuleInteger) {
		this.gameRuleBoolean = gameRuleBoolean;
		this.gameRuleInteger = gameRuleInteger;
	}

	@MappedMethod
	public boolean getBooleanGameRule(MinecraftServer minecraftServer) {
		return gameRuleBoolean != null && minecraftServer.data.getGameRules().getBoolean(gameRuleBoolean);
	}

	@MappedMethod
	public int getIntegerGameRule(MinecraftServer minecraftServer) {
		return gameRuleInteger == null ? 0 : minecraftServer.data.getGameRules().getInt(gameRuleInteger);
	}

	@MappedMethod
	public boolean hasBooleanGameRule() {
		return gameRuleBoolean != null;
	}

	@MappedMethod
	public boolean hasIntegerGameRule() {
		return gameRuleInteger != null;
	}
}
