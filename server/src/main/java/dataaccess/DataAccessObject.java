package dataaccess;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import server.Server;

public class DataAccessObject {
    public UserDataDAO userDataDAO;
    public AuthDataDAO authDataDAO;
    public GameDataDAO gameDataDAO;
    public Server server;

    public DataAccessObject(Server server) {
        this.userDataDAO = new UserDataDAO();
        this.authDataDAO = new AuthDataDAO();
        this.gameDataDAO = new GameDataDAO(server);
        this.server = server;
    }
    public void clear() {
        userDataDAO.clear();
        authDataDAO.clear();
        gameDataDAO.clear();
    }
}
