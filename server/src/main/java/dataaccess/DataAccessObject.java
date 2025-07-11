package dataaccess;

public class DataAccessObject {
    UserDataDAO userDataDao = new UserDataDAO();
    AuthDataDAO authDataDao = new AuthDataDAO();
    GameDataDAO gameData = new GameDataDAO();
    void clear() {
        userDataDao.clear();
        authDataDao.clear();
        gameData.clear();
    }
}
