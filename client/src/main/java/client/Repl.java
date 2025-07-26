package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final BaseClient client;

    public Repl(String serverUrl) {
        client = new BaseClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to my Chess Server. Sign in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Exception e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        System.exit(0);
    }

    private void printPrompt() {
        System.out.print(RESET_TEXT_COLOR +"\n" + "[" + client.state  + "] >>> " + SET_TEXT_COLOR_BLUE);
    }
}
