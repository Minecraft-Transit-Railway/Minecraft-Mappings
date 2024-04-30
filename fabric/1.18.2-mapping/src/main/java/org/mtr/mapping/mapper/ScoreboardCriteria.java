package org.mtr.mapping.mapper;

import org.mtr.mapping.holder.ScoreboardCriterion;
import org.mtr.mapping.tool.HolderBase;

public interface ScoreboardCriteria {

	ScoreboardCriterion DUMMY = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.DUMMY);
	ScoreboardCriterion TRIGGER = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.TRIGGER);
	ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.DEATH_COUNT);
	ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.PLAYER_KILL_COUNT);
	ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.TOTAL_KILL_COUNT);
	ScoreboardCriterion HEALTH = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.HEALTH);
	ScoreboardCriterion FOOD = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.FOOD);
	ScoreboardCriterion AIR = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.AIR);
	ScoreboardCriterion ARMOR = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.ARMOR);
	ScoreboardCriterion XP = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.XP);
	ScoreboardCriterion LEVEL = new ScoreboardCriterion(net.minecraft.scoreboard.ScoreboardCriterion.LEVEL);
	ScoreboardCriterion[] TEAM_KILLS = HolderBase.convertArray(net.minecraft.scoreboard.ScoreboardCriterion.TEAM_KILLS, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
	ScoreboardCriterion[] KILLED_BY_TEAMS = HolderBase.convertArray(net.minecraft.scoreboard.ScoreboardCriterion.KILLED_BY_TEAMS, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
}
