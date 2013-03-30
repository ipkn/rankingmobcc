package com.stackmob.example;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stackmob.core.MethodVerb;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SMObject;

public class WideRanking extends BaseCustomCodeMethod {

	@Override
	public String getMethodName() {
		return "wide_ranking";
	}

	@Override
	public List<String> getParams() {
		return new ArrayList<String>(Arrays.asList("game_id","client_id"));
	}

	@Override
	ResponseToProcess _execute() {
		try 
		{
		if (request.getVerb() == MethodVerb.GET)
		{
			final String gameId = request.getParams().get("game_id");
			final String clientId = request.getParams().get("client_id");
			
			final SMObject gameObj = readByPrimaryKey("game", "game_id", gameId);
			final SMGame game = new SMGame(gameObj);
			
			final long resetTime = Util.computeLastResetTime(game);
			
			final SMObject clientObj = readByPrimaryKey("client", "client_id", clientId + "\n" + gameId, 2);
			
			response.getLoggerService(WideRanking.class).debug("clientObj " + clientObj.toString());
			
			final SMClient client = new SMClient(clientObj);
			final double clientScore = Util.getCurrentScore(client, resetTime);
			
			final ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
			for(SMClient friend : client.friends)
			{
				ArrayList<Object> allInOne = new ArrayList<Object>();
				allInOne.add(friend.clientId);
				allInOne.add(Util.getCurrentScore(friend, resetTime));
				int rank = 1;
				for(SMClient friendOfFriend : friend.friends)
				{
					if (Util.getCurrentScore(friendOfFriend, resetTime) < clientScore)
						rank += 1;
				}
				allInOne.add(rank);
				result.add(allInOne);
			}
			
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("scores", result);
			m.put("score", clientScore);
			
			return new ResponseToProcess(HttpURLConnection.HTTP_OK, m);
			
		}
		} catch(Exception e) {
			response.getLoggerService(WideRanking.class).error("exception " + e.getClass().getName(), e);
		}
		
		return internalError();
	}

}
