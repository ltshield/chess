package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;

import service.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws DataAccessException {
        var path = "/db";
        System.out.println(serverUrl+path);
        makeRequest("DELETE", path, null, null);
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException{
        var path = "/user";
        return makeRequest("POST", path, req, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        var path = "/session";
        return makeRequest("POST", path, req, LoginResult.class);
    }

    public Object logout(LogoutRequest req) throws DataAccessException {
        var path = "/session";
        return makeRequest("DELETE", path, req, null);
    }

    public ListGamesResponse listGames(ListGamesRequest req) throws DataAccessException {
        var path = "/game";
        return makeRequest("GET", path, req, ListGamesResponse.class);
    }

    public CreateGameResponse createGame(CreateGameRequest req) throws DataAccessException {
        var path = "/game";
        return makeRequest("POST", path, req, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest req) throws DataAccessException {
        var path = "/game";
        makeRequest("PUT", path, req, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException("Error.");
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
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw new DataAccessException("Error");
                }
            }

            throw new DataAccessException("Error");
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
