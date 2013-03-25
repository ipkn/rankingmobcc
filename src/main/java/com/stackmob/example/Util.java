package com.stackmob.example;

public class Util {
	static long computeLastResetTime(long resetBegin, long resetInterval)
	{
		long currentTimeStamp = System.currentTimeMillis();
		return currentTimeStamp - (currentTimeStamp - resetBegin) % resetInterval;
	}
	static long computeLastResetTime(SMGame game)
	{
		return computeLastResetTime(game.resetBegin, game.resetInterval);
	}
	static double getCurrentScore(long scoreDate, double score, long resetTime)
	{
		if (scoreDate - resetTime < 0)
			return 0;
		return score;
	}
	static double getCurrentScore(SMClient client, long resetTime)
	{
		return getCurrentScore(client.scoreDate, client.score, resetTime);
	}
}
