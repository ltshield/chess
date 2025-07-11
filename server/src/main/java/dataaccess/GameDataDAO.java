package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameDataDAO extends DataAccessObject{

    private Collection<ChessGame> currentGames = new ArrayList<>();

    //CRUD
    void createGame(GameData gameData) throws DataAccessException{}
    void getGame() throws DataAccessException{}
    void listGames() throws DataAccessException{}
    void updateGame() throws DataAccessException{}
    // same as addUserToGame?
    void addUserToGame() throws DataAccessException{}
    void deleteGame() throws DataAccessException{}

    void clear() {currentGames = new ArrayList<>();}
}
