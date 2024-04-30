package org.mtr.mapping.mapper;

import net.minecraft.world.level.GameRules;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;

import javax.annotation.Nullable;

public enum GameRule {

	ANNOUNCE_ADVANCEMENTS(GameRules.RULE_ANNOUNCE_ADVANCEMENTS, null),
	BLOCK_EXPLOSION_DROP_DECAY(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY, null),
	COMMAND_BLOCK_OUTPUT(GameRules.RULE_COMMANDBLOCKOUTPUT, null),
	DISABLE_ELYTRA_MOVEMENT_CHECK(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK, null),
	DISABLE_RAIDS(GameRules.RULE_DISABLE_RAIDS, null),
	DO_DAYLIGHT_CYCLE(GameRules.RULE_DAYLIGHT, null),
	DO_ENTITY_DROPS(GameRules.RULE_DOENTITYDROPS, null),
	DO_FIRE_TICK(GameRules.RULE_DOFIRETICK, null),
	DO_IMMEDIATE_RESPAWN(GameRules.RULE_DO_IMMEDIATE_RESPAWN, null),
	DO_INSOMNIA(GameRules.RULE_DOINSOMNIA, null),
	DO_LIMITED_CRAFTING(GameRules.RULE_LIMITED_CRAFTING, null),
	DO_MOB_GRIEFING(GameRules.RULE_MOBGRIEFING, null),
	DO_MOB_LOOT(GameRules.RULE_DOMOBLOOT, null),
	DO_MOB_SPAWNING(GameRules.RULE_DOMOBSPAWNING, null),
	DO_PATROL_SPAWNING(GameRules.RULE_DO_PATROL_SPAWNING, null),
	DO_TILE_DROPS(GameRules.RULE_DOBLOCKDROPS, null),
	DO_TRADER_SPAWNING(GameRules.RULE_DO_TRADER_SPAWNING, null),
	DO_VINES_SPREAD(GameRules.RULE_DO_VINES_SPREAD, null),
	DO_WARDEN_SPAWNING(GameRules.RULE_DO_WARDEN_SPAWNING, null),
	DO_WEATHER_CYCLE(GameRules.RULE_WEATHER_CYCLE, null),
	DROWNING_DAMAGE(GameRules.RULE_DROWNING_DAMAGE, null),
	ENDER_PEARLS_VANISH_ON_DEATH(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH, null),
	FALL_DAMAGE(GameRules.RULE_FALL_DAMAGE, null),
	FIRE_DAMAGE(GameRules.RULE_FIRE_DAMAGE, null),
	FORGIVE_DEAD_PLAYERS(GameRules.RULE_FORGIVE_DEAD_PLAYERS, null),
	FREEZE_DAMAGE(GameRules.RULE_FREEZE_DAMAGE, null),
	GLOBAL_SOUND_EVENTS(GameRules.RULE_GLOBAL_SOUND_EVENTS, null),
	KEEP_INVENTORY(GameRules.RULE_KEEPINVENTORY, null),
	LAVA_SOURCE_CONVERSION(GameRules.RULE_LAVA_SOURCE_CONVERSION, null),
	LOG_ADMIN_COMMANDS(GameRules.RULE_LOGADMINCOMMANDS, null),
	MOB_EXPLOSION_DROP_DECAY(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY, null),
	NATURAL_REGENERATION(GameRules.RULE_NATURAL_REGENERATION, null),
	PROJECTILES_CAN_BREAK_BLOCKS(GameRules.RULE_PROJECTILESCANBREAKBLOCKS, null),
	REDUCED_DEBUG_INFO(GameRules.RULE_REDUCEDDEBUGINFO, null),
	SEND_COMMAND_FEEDBACK(GameRules.RULE_SENDCOMMANDFEEDBACK, null),
	SHOW_DEATH_MESSAGES(GameRules.RULE_SHOWDEATHMESSAGES, null),
	SPECTATORS_GENERATE_CHUNKS(GameRules.RULE_SPECTATORSGENERATECHUNKS, null),
	TNT_EXPLOSION_DROP_DECAY(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY, null),
	UNIVERSAL_ANGER(GameRules.RULE_UNIVERSAL_ANGER, null),
	WATER_SOURCE_CONVERSION(GameRules.RULE_WATER_SOURCE_CONVERSION, null),
	COMMAND_MODIFICATION_BLOCK_LIMIT(null, GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT),
	MAX_COMMAND_CHAIN_LENGTH(null, GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH),
	MAX_COMMAND_FORK_COUNT(null, GameRules.RULE_MAX_COMMAND_FORK_COUNT),
	MAX_ENTITY_CRAMMING(null, GameRules.RULE_MAX_ENTITY_CRAMMING),
	PLAYERS_NETHER_PORTAL_CREATIVE_DELAY(null, GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY),
	PLAYERS_NETHER_PORTAL_DEFAULT_DELAY(null, GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY),
	PLAYERS_SLEEPING_PERCENTAGE(null, GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE),
	RANDOM_TICK_SPEED(null, GameRules.RULE_RANDOMTICKING),
	SNOW_ACCUMULATION_HEIGHT(null, GameRules.RULE_SNOW_ACCUMULATION_HEIGHT),
	SPAWN_RADIUS(null, GameRules.RULE_SPAWN_RADIUS);


	@Nullable
	private final GameRules.Key<GameRules.BooleanValue> gameRuleBoolean;
	@Nullable
	private final GameRules.Key<GameRules.IntegerValue> gameRuleInteger;

	@Deprecated
	GameRule(@Nullable GameRules.Key<GameRules.BooleanValue> gameRuleBoolean, @Nullable GameRules.Key<GameRules.IntegerValue> gameRuleInteger) {
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
