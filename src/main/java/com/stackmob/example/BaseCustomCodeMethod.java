package com.stackmob.example;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.DataService;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.SMEquals;
import com.stackmob.sdkapi.SMObject;
import com.stackmob.sdkapi.SMString;
import com.stackmob.sdkapi.SMCondition;

public abstract class BaseCustomCodeMethod implements CustomCodeMethod {

	ProcessedAPIRequest request;
	SDKServiceProvider response;

	ResponseToProcess internalError()
	{
		return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
	
	ResponseToProcess internalError(String msg)
	{
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("error", msg);
		return new ResponseToProcess(HttpURLConnection.HTTP_INTERNAL_ERROR, h);
	}
	
	SMObject readByPrimaryKey(String schema, String primaryKeyName, String key)
	{
		return readByPrimaryKey(schema, primaryKeyName, key, 0);
	}
	
	SMObject readByPrimaryKey(String schema, String primaryKeyName, String key, int depth)
	{
		DataService ds = response.getDataService();
		try {
			List<SMObject> ret = ds.readObjects(schema, new ArrayList<SMCondition>(Arrays.asList(
					new SMEquals(primaryKeyName, new SMString(key))
					
					)), depth);
			if (ret.size() == 0)
				return null;
			return ret.get(0);
		} catch (InvalidSchemaException e) {
			e.printStackTrace();
			response.getLoggerService("DB " + schema).error("InvalidSchemaException", e);
		} catch (DatastoreException e) {
			e.printStackTrace();
			response.getLoggerService("DB " + schema).error("DatastoreException", e);
		}
		return null;
	}
	
	@Override
	public ResponseToProcess execute(ProcessedAPIRequest request,
			SDKServiceProvider response) {
		this.request = request;
		this.response = response;
		return _execute();
	}
	
	abstract ResponseToProcess _execute();
}
