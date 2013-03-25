package com.stackmob.example;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.MethodVerb;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SMDouble;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMSet;
import com.stackmob.sdkapi.SMUpdate;

import org.json.simple.*;

public class Highscore extends BaseCustomCodeMethod {

	
	@Override
	public ResponseToProcess _execute() {
		if (request.getVerb() == MethodVerb.POST)
		{
			if (request.getBody().isEmpty())
				return internalError();
			JSONObject o = (JSONObject)JSONValue.parse(request.getBody());
			if (o == null)
				return internalError();
			
			String gameId = (String)o.get("game_id");
			String clientId = (String)o.get("client_id");
			double score = ((Number)o.get("score")).doubleValue();
			String password = (String)o.get("password");
			
			if (gameId == null || gameId.isEmpty())
				return internalError();
			if (clientId == null || clientId.isEmpty())
				return internalError();
			if (password == null)
				password = "";
			
			SMObject gameObj = readByPrimaryKey("game", "game_id", gameId);
			if (gameObj == null)
				return internalError();
			SMGame game = new SMGame(gameObj);
			
			long lastResetTime = Util.computeLastResetTime(game);
			
			SMObject clientObj = readByPrimaryKey("client", "client_id", clientId);
			if (clientObj == null)
				return internalError();
			SMClient client = new SMClient(clientObj);
			
			double currentScore = Util.getCurrentScore(client, lastResetTime);
			if (currentScore < score)
			{
				currentScore = score;
				List<SMUpdate> updates = new ArrayList<SMUpdate>();
				updates.add(new SMSet("score", new SMDouble(currentScore)));
				updates.add(new SMSet("scoredate", new SMInt(System.currentTimeMillis())));
				try {
					response.getDataService().updateObject("client", clientId, updates);
				} catch (InvalidSchemaException e) {
					e.printStackTrace();
					response.getLoggerService("Highscore").error("InvalidSchemaException", e);
				} catch (DatastoreException e) {
					e.printStackTrace();
					response.getLoggerService("Highscore").error("DatastoreException", e);
				}
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("score", new Double(currentScore));
			return new ResponseToProcess(HttpURLConnection.HTTP_OK, m);
			
		}
		return internalError();
	}

	@Override
	public String getMethodName() {
		return "highscore";
	}

	@Override
	public List<String> getParams() {
		List<String> l = new ArrayList<String>();
		l.add("game_id");
		l.add("client_id");
		l.add("score");
		l.add("password");
		return l;
	}

}