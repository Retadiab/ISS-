import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnector {
    static Symmetric symmetric;

    public static void main(String[] args) {
        connectToServer();
    }

    public static void connectToServer() {
        symmetric = new Symmetric();
//0123456789101112
        try (Socket socket = new Socket("127.0.0.1", 11111)) {
            Scanner scanner = new Scanner(System.in);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String input = symmetric.decrypt(in.nextLine());
                if (input.charAt(0) == '$') {
                    String password = symmetric.decrypt(in.nextLine());
                    symmetric.setKey(password);
                }
                System.out.println(input);
                out.println(symmetric.encrypt(scanner.nextLine()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
