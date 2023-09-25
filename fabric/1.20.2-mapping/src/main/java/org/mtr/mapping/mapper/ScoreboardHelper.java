package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Scoreboard;
import org.mtr.mapping.holder.ScoreboardObjective;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		return scoreboard.getNullableObjective(name);
	}
}
