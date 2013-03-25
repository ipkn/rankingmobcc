package com.stackmob.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMValue;

public class SMClient {
	public String clientId;
	public double score;
	public String smOwner;
	public long scoreDate;
	public String gameId;
	public ArrayList<String> friendIds;
	public ArrayList<SMClient> friends;
	public SMClient(SMObject o)
	{
		if (o == null)
			return;
		Map<String, SMValue> m = o.getValue();
		
		gameId = m.get("game_id").toString();
		clientId = m.get("client_id").toString();
		smOwner= m.get("sm_owner").toString();
		score = (Double)m.get("score").getValue();
		scoreDate = (Long)m.get("scoredate").getValue();
		friendIds = new ArrayList<String>();
		friends = new ArrayList<SMClient>();
		for(SMValue friend : (List<SMValue>)m.get("friends").getValue())
		{
			if (friend.isA(String.class))
				friendIds.add((String)friend.getValue());
			else 
				friends.add(new SMClient((SMObject)friend));
		}
	}
}
