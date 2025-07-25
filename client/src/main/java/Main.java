import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(0);
        var serverUrl = "http://localhost:" + port;
        new Repl(serverUrl).run();
    }
}