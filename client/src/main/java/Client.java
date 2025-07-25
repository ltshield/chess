import dataaccess.DataAccessException;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;

public class Client {
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private String state = "LOGGEDOUT";

    private final BeforeLoginClient beforeLoginClient;

    public Client(String serverurl) {
        serverUrl = serverurl;
        serverFacade = new ServerFacade(serverUrl);
        beforeLoginClient = new BeforeLoginClient(serverFacade, this);
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
//        if (state.equals("LOGGEDIN")) {
//            ;
//        }
        if (state.equals("LOGGEDOUT")) {
            output = beforeLoginClient.help();
        }
//        if (state.equals("INGAME")) {
//            ;
//        }
        else {
            output = "uhoh";
        }
        return output;
    }

    public String eval(String input) {
        String output = "";
//        if (state.equals("LOGGEDIN")) {
//
//        }
        if (state.equals("LOGGEDOUT")) {
            output = beforeLoginClient.eval(input);
        }
//        if (state.equals("INGAME")) {
//
//        }
        else {
            output = "uhoh";
        }
        return output;
    }

}
