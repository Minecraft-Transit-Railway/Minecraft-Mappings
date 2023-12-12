package org.mtr.mapping.mapper;

import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		return scoreboard.getObjective(name);
	}

	@MappedMethod
	public static ScoreboardObjective addObjective(Scoreboard scoreboard, String name, ScoreboardCriterion scoreboardCriterion, Text displayName, ScoreboardCriterionRenderType scoreboardCriterionRenderType) {
		return scoreboard.addObjective(name, scoreboardCriterion, displayName, scoreboardCriterionRenderType, true, BlankFormat.INSTANCE);
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return getOrCreateScore(scoreboard, playerName, scoreboardObjective).get();
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).set(amount);
	}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).add(amount);
	}

	private static ScoreAccess getOrCreateScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), scoreboardObjective);
	}
}
