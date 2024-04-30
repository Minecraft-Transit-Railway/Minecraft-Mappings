package org.mtr.mapping.mapper;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.mtr.mapping.holder.ScoreboardCriterion;
import org.mtr.mapping.tool.HolderBase;

public interface ScoreboardCriteria {

	ScoreboardCriterion DUMMY = new ScoreboardCriterion(ObjectiveCriteria.DUMMY);
	ScoreboardCriterion TRIGGER = new ScoreboardCriterion(ObjectiveCriteria.TRIGGER);
	ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion(ObjectiveCriteria.DEATH_COUNT);
	ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion(ObjectiveCriteria.KILL_COUNT_PLAYERS);
	ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion(ObjectiveCriteria.KILL_COUNT_ALL);
	ScoreboardCriterion HEALTH = new ScoreboardCriterion(ObjectiveCriteria.HEALTH);
	ScoreboardCriterion FOOD = new ScoreboardCriterion(ObjectiveCriteria.FOOD);
	ScoreboardCriterion AIR = new ScoreboardCriterion(ObjectiveCriteria.AIR);
	ScoreboardCriterion ARMOR = new ScoreboardCriterion(ObjectiveCriteria.ARMOR);
	ScoreboardCriterion XP = new ScoreboardCriterion(ObjectiveCriteria.EXPERIENCE);
	ScoreboardCriterion LEVEL = new ScoreboardCriterion(ObjectiveCriteria.LEVEL);
	ScoreboardCriterion[] TEAM_KILLS = HolderBase.convertArray(ObjectiveCriteria.TEAM_KILL, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
	ScoreboardCriterion[] KILLED_BY_TEAMS = HolderBase.convertArray(ObjectiveCriteria.KILLED_BY_TEAM, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
}
