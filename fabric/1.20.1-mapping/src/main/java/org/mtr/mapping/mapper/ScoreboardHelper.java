package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		return scoreboard.getNullableObjective(name);
	}

	@MappedMethod
	public static ScoreboardObjective addObjective(Scoreboard scoreboard, String name, ScoreboardCriterion scoreboardCriterion, Text displayName, ScoreboardCriterionRenderType scoreboardCriterionRenderType) {
		return scoreboard.addObjective(name, scoreboardCriterion, displayName, scoreboardCriterionRenderType);
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return getOrCreateScore(scoreboard, playerName, scoreboardObjective).getScore();
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).setScore(amount);
	}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).incrementScore(amount);
	}

	private static ScoreboardScore getOrCreateScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return scoreboard.getPlayerScore(playerName, scoreboardObjective);
	}
}
