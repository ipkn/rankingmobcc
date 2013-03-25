package com.stackmob.example;
import java.util.Map;

import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMValue;


public class SMGame {
	public long resetBegin;
	public long resetInterval;
	public String gameId;
	public SMGame(SMObject o)
	{
		if (o == null)
			return;
		Map<String, SMValue> m = o.getValue();
		
		gameId = m.get("game_id").toString();
		resetBegin = (Long)m.get("reset_begin").getValue();
		resetInterval = (Long)m.get("reset_interval").getValue();
	}
}
