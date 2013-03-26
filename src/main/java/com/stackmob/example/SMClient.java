package com.stackmob.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMValue;

public class SMClient {
	public String clientId;
	public double score;
	public String smOwner;
	public long scoreDate;
	public String gameId;
	public ArrayList<String> friendIds;
	public ArrayList<SMClient> friends;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SMClient(SMObject o)
	{
		if (o == null)
			return;
		Map<String, SMValue> m = o.getValue();
		
		gameId = m.get("game_id").toString();
		clientId = m.get("client_id").toString();
		if (m.containsKey("sm_owner"))
			smOwner= m.get("sm_owner").toString();
		score = ((Number)m.get("score").getValue()).doubleValue();
		scoreDate = (Long)m.get("scoredate").getValue();
		/*
		friendIds = new ArrayList<String>();
		friends = new ArrayList<SMClient>();
		if (m.containsKey("friends"))
		{
			for(SMValue friend : (List<SMValue>)m.get("friends").getValue())
			{
				//if (friend.isA(SMString.class))
					friendIds.add(friend.toString());
				//else 
					//friends.add(new SMClient((SMObject)friend));
			}
		}*/
	}
}
