package client;

import chess.ChessGame;
import websocket.NotificationHandler;

import java.util.ArrayList;
import java.util.Collection;

public class BaseClient {
    private final ServerFacade serverFacade;
    private final String serverUrl;
    public String state = "LOGGEDOUT";
    public String authToken;
    public String playerColor;
    public String username;
    private final BeforeLoginClient beforeLoginClient;
    private final AfterLoginClient afterLoginClient;
    public final InGameClient inGameClient;

    public BaseClient(String serverurl) {
        serverUrl = serverurl;
        serverFacade = new ServerFacade(serverUrl);

        beforeLoginClient = new BeforeLoginClient(serverFacade, this);
        afterLoginClient = new AfterLoginClient(serverFacade, this);
        inGameClient = new InGameClient(serverFacade, this, null, null);
    }

    public void switchState(String newState) {
        Collection<String> possibleStates = new ArrayList<>();
        possibleStates.add("LOGGEDIN");
        possibleStates.add("LOGGEDOUT");
        possibleStates.add("INGAME");
        if (possibleStates.contains(newState)) {
            state = newState;
        }
    }

    public String help() {
        String output = "";
        if (state.equals("LOGGEDIN")) {
            output = afterLoginClient.help();
        }
        if (state.equals("LOGGEDOUT")) {
            output = beforeLoginClient.help();
        }
        if (state.equals("INGAME")) {
            output = inGameClient.help();
        }
        return output;
    }

    public String eval(String input) {
        String output = "";
        if (state.equals("LOGGEDIN")) {
            output = afterLoginClient.eval(input);
        }
        if (state.equals("LOGGEDOUT")) {
            output = beforeLoginClient.eval(input);
        }
        if (state.equals("INGAME")) {
            output = inGameClient.eval(input);
        }
        return output;
    }

}
