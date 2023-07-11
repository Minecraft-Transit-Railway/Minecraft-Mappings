package org.mtr.mapping.mapper;


import net.minecraft.scoreboard.ScoreCriteria;
import org.mtr.mapping.holder.ScoreboardCriterion;
import org.mtr.mapping.tool.HolderBase;

public interface ScoreboardCriteria {

	ScoreboardCriterion DUMMY = new ScoreboardCriterion(ScoreCriteria.DUMMY);
	ScoreboardCriterion TRIGGER = new ScoreboardCriterion(ScoreCriteria.TRIGGER);
	ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion(ScoreCriteria.DEATH_COUNT);
	ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion(ScoreCriteria.KILL_COUNT_PLAYERS);
	ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion(ScoreCriteria.KILL_COUNT_ALL);
	ScoreboardCriterion HEALTH = new ScoreboardCriterion(ScoreCriteria.HEALTH);
	ScoreboardCriterion FOOD = new ScoreboardCriterion(ScoreCriteria.FOOD);
	ScoreboardCriterion AIR = new ScoreboardCriterion(ScoreCriteria.AIR);
	ScoreboardCriterion ARMOR = new ScoreboardCriterion(ScoreCriteria.ARMOR);
	ScoreboardCriterion XP = new ScoreboardCriterion(ScoreCriteria.EXPERIENCE);
	ScoreboardCriterion LEVEL = new ScoreboardCriterion(ScoreCriteria.LEVEL);
	ScoreboardCriterion[] TEAM_KILLS = HolderBase.convertArray(ScoreCriteria.TEAM_KILL, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
	ScoreboardCriterion[] KILLED_BY_TEAMS = HolderBase.convertArray(ScoreCriteria.KILLED_BY_TEAM, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
}
