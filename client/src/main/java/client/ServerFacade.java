package client;

import com.google.gson.Gson;
import dataexception.DataAccessException;

import service.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    public final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws DataAccessException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

//    public ChessGameResponse getBoard(ChessGameRequest req) throws DataAccessException {
//        var path = "/db";
//        String authToken = req.authToken();
//        return makeRequest("GET", path, req, ChessGameResponse.class, authToken);
//    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException{
        var path = "/user";
        return makeRequest("POST", path, req, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        var path = "/session";
        return makeRequest("POST", path, req, LoginResult.class, null);
    }

    public void logout(LogoutRequest req) throws DataAccessException {
        var path = "/session";
        String authToken = req.authorization();
        makeRequest("DELETE", path, req, null, authToken);
    }

    public ListGamesResponse listGames(ListGamesRequest req) throws DataAccessException {
        var path = "/game";
        String authToken = req.authToken();
        return makeRequest("GET", path, null, ListGamesResponse.class, authToken);
    }

    public CreateGameResponse createGame(CreateGameRequest req) throws DataAccessException {
        var path = "/game";
        // set the authToken in the headers of the request before passing
        String authToken = req.authToken();
        return makeRequest("POST", path, req, CreateGameResponse.class, authToken);
    }

    public UpdateGameResponse updateGame(UpdateGameRequest req) throws DataAccessException {
        var path = "/db";
        String authToken = req.authToken();
        return makeRequest("POST", path, req, UpdateGameResponse.class, authToken);
    }

    public JoinGameResponse joinGame(JoinGameRequest req) throws DataAccessException {
        var path = "/game";
        String authToken = req.authToken();
        return makeRequest("PUT", path, req, JoinGameResponse.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken == null) {
                http.addRequestProperty("Authorization", null);
            } else {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            if (status == 403) {throw new DataAccessException("Sorry, that one is already taken.");}
            if (status == 400) {throw new DataAccessException("Please respond in the correct format.");}
            if (status == 401) {throw new DataAccessException("Incorrect authorization.");}
            else {throw new DataAccessException(http.getResponseMessage());}
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
