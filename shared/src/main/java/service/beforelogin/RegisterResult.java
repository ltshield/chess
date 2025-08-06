package service.beforelogin;

import model.AuthData;

public record RegisterResult(String username, String authToken) {
}
