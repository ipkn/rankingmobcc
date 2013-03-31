package com.stackmob.example;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.MethodVerb;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriend extends BaseCustomCodeMethod {
    @SuppressWarnings("UnusedAssignment")
    @Override
    ResponseToProcess _execute() {
        if (request.getVerb() == MethodVerb.POST)
        {
            if (request.getBody().isEmpty())
            {
                response.getLoggerService(AddFriend.class).error("body empty");
                return internalError();
            }
            JSONObject o = (JSONObject) JSONValue.parse(request.getBody());
            if (o == null)
            {
                response.getLoggerService(AddFriend.class).error("json parse error");
                return internalError();
            }

            String gameId = o.get("game_id").toString();
            String clientId = o.get("client_id").toString();
            String friendId = o.get("friend_id").toString();
            String password = o.get("password").toString();

            if (gameId == null || gameId.isEmpty() || gameId.contains("\n"))
            {
                response.getLoggerService(AddFriend.class).error("json game id error");
                return internalError();
            }
            if (clientId == null || clientId.isEmpty() || clientId.contains("\n"))
            {
                response.getLoggerService(AddFriend.class).error("json client id error");
                return internalError();
            }
            if (friendId == null || friendId.isEmpty() || friendId.contains("\n"))
            {
                response.getLoggerService(AddFriend.class).error("json friend id error");
                return internalError();
            }

            if (password == null)
                password = "";

            SMObject gameObj = readByPrimaryKey("game", "game_id", gameId);
            if (gameObj == null)
            {
                response.getLoggerService(AddFriend.class).error("cannot find game obj for " + gameId);
                return internalError();
            }

            DataService db = response.getDataService();
            List<SMValue> valueToAppend = new ArrayList<SMValue>();
            valueToAppend.add(new SMString(friendId+"\n"+gameId));
            try {
                SMObject ret = db.addRelatedObjects("client", new SMString(clientId + "\n" + gameId), "friends", valueToAppend);
                if (ret == null)
                {
                    response.getLoggerService(AddFriend.class).error("cannot find add relation " + friendId+ " for " + clientId + " with game " + gameId);
                    return internalError();
                }
            } catch (InvalidSchemaException e) {
                response.getLoggerService(AddFriend.class).error("InvalidSchemaException", e);
            } catch (DatastoreException e) {
                response.getLoggerService(AddFriend.class).error("DatastoreException", e);
            }

            Map<String, Object> m = new HashMap<String, Object>();
            return new ResponseToProcess(HttpURLConnection.HTTP_OK, m);

        }
        return internalError();
    }

    @Override
    public String getMethodName() {
        return "add_friend";
    }

    @Override
    public List<String> getParams() {
        List<String> l = new ArrayList<String>();
        l.add("game_id");
        l.add("client_id");
        l.add("friend_id");
        l.add("password");
        return l;
    }
}
