//import server.Server;

public class Main {
    public static void main(String[] args) {
//        Server server = new Server();
//        var port = server.run(0);
//        var serverUrl = "http://localhost:" + port;

        var serverUrl = "http://localhost:8080";
        new Repl(serverUrl).run();
    }
}