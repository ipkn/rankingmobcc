package com.stackmob.example;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.json.simple.JSONValue;
import org.junit.Test;

import com.stackmob.sdkapi.SMInt;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMValue;

public class RankingMobTest {

	@Test
	public void test() {
		{
			Object o = JSONValue.parse("[");
			assertEquals(null, o);
			o = JSONValue.parse("[1]");
			assertNotSame(null, o);
		}
		
		final SMInt i = new SMInt(3l);
		@SuppressWarnings({ "serial", "rawtypes" })
		SMObject o = new SMObject(new HashMap<String, SMValue>(){{
			put("v", i);
			
		}}) ;
		@SuppressWarnings("rawtypes")
		SMValue v = o.getValue().get("v");
		System.out.println(v.getValue().getClass());

	}

}
