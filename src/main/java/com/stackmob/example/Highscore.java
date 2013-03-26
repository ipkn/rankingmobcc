package com.stackmob.example;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.MethodVerb;
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
		response.getLoggerService(Highscore.class).debug("highscore running");
		if (request.getVerb() == MethodVerb.POST)
		{
			if (request.getBody().isEmpty())
			{
				response.getLoggerService(Highscore.class).error("body empty");
				return internalError();
			}
			JSONObject o = (JSONObject)JSONValue.parse(request.getBody());
			if (o == null)
			{
				response.getLoggerService(Highscore.class).error("json parse error");
				return internalError();
			}
			
			String gameId = (String)o.get("game_id");
			String clientId = (String)o.get("client_id");
			double score = Double.parseDouble(o.get("score").toString());
			String password = (String)o.get("password");
			
			if (gameId == null || gameId.isEmpty() || gameId.contains("\n"))
			{
				response.getLoggerService(Highscore.class).error("json game id error");
				return internalError();
			}
			if (clientId == null || clientId.isEmpty() || clientId.contains("\n"))
			{
				response.getLoggerService(Highscore.class).error("json client id error");
				return internalError();
			}
			
			if (password == null)
				password = "";
			
			SMObject gameObj = readByPrimaryKey("game", "game_id", gameId);
			if (gameObj == null)
			{
				response.getLoggerService(Highscore.class).error("cannot find game obj for " + gameId);
				return internalError();
			}
			SMGame game = new SMGame(gameObj);
			
			long lastResetTime = Util.computeLastResetTime(game);
			
			SMObject clientObj = readByPrimaryKey("client", "client_id", clientId+"\n"+gameId);
			if (clientObj == null)
			{
				response.getLoggerService(Highscore.class).error("cannot find client obj for " + clientId);
				return internalError();
			}
			response.getLoggerService(Highscore.class).debug("build smclient" + clientObj.toString());
			
			SMClient client;
			try {
				client = new SMClient(clientObj);
			}catch(Exception e)
			{
				response.getLoggerService(Highscore.class).error("build smclient error:"+e.getClass().getName(), e);
				return internalError();
			}
			
			// TODO: reject invalid password
			response.getLoggerService(Highscore.class).debug("get current score");
			double currentScore = Util.getCurrentScore(client, lastResetTime);
			response.getLoggerService(Highscore.class).debug("currentscore, score compare");
			if (currentScore < score)
			{
				response.getLoggerService(Highscore.class).debug("build updates");
				currentScore = score;
				List<SMUpdate> updates = new ArrayList<SMUpdate>();
				updates.add(new SMSet("score", new SMDouble(currentScore)));
				updates.add(new SMSet("scoredate", new SMInt(System.currentTimeMillis())));
				try {
					response.getLoggerService(Highscore.class).debug("query updates");
					response.getDataService().updateObject("client", clientId + "\n" + gameId, updates);
					response.getLoggerService(Highscore.class).debug("query done");
				} catch (InvalidSchemaException e) {
					e.printStackTrace();
					response.getLoggerService("Highscore").error("InvalidSchemaException", e);
				} catch (DatastoreException e) {
					e.printStackTrace();
					response.getLoggerService("Highscore").error("DatastoreException", e);
				}
			}
			response.getLoggerService(Highscore.class).debug("returning");
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
