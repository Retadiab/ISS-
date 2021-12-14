import java.io.IOException;;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// nc localhost 11111
public class Server implements Runnable {
    Socket socket;
    DataBase dataBase;
    Authentication authentication;
    Symmetric symmetric;

    Server(DataBase dataBase, Socket socket) {
        this.socket = socket;
        this.dataBase = dataBase;
    }

    @Override
    public void run() {
        symmetric = new Symmetric();
        System.out.println("Connected: " + socket);
        ClientInfo clientInfo = new ClientInfo();
        authentication = new Authentication(socket, dataBase);

        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner input = new Scanner(socket.getInputStream());

            boolean valid = authentication.loginOrSignUpProcess(clientInfo);

            if (!valid) {
                output.println(symmetric.encrypt("failed in authentication : << connection will stop >>"));
                socket.close();
            }
            output.println(symmetric.encrypt("$_Please enter or *** 1 *** for addAccount or *** 2 *** for showAccount or *** 3 *** for editAccount or *** 4 *** for deleteAccount"));

            output.println(symmetric.encrypt(clientInfo.getPassword()));
            symmetric.setKey(clientInfo.getPassword());

            String userChoice = symmetric.decrypt(input.next());
            switch (userChoice) {
                case "1":
                    Account account = new Account();
                    output.println(symmetric.encrypt("Enter Account Title"));
                    account.title = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("Enter Account UserName"));
                    account.userName = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("Enter Account Email"));
                    account.setEmail(symmetric.decrypt(input.next()));
                    output.println(symmetric.encrypt("Enter Account Description"));
                    account.description = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("Enter Account password"));
                    String value = symmetric.decrypt(input.next());
                    account.setPassword(value);
                    output.println(symmetric.encrypt("Enter attachment file to your account"));
                    account.file = symmetric.decrypt(input.next());
                    clientInfo.setAccount(account);
                    dataBase.executeQuery(clientInfo, "addAccount");
                    break;
                case "2":
                    output.println(symmetric.encrypt("Enter Account Title you want to show"));
                    clientInfo.getAccount().title = symmetric.decrypt(input.next());
                    dataBase.executeQuery(clientInfo, "showAccount");
                    break;
                case "3":
                    String result;
                    output.println(symmetric.encrypt("Enter what the title you want to edit"));
                    clientInfo.getAccount().title = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("enter the new password -- if not enter 0"));
                    result = symmetric.decrypt(input.next());
                    if (!result.equals("0")) {
                        clientInfo.getAccount().setPassword(result);
                    }
                    output.println(symmetric.encrypt("enter the new userName -- if not enter 0"));
                    result = symmetric.decrypt(input.next());
                    if (!result.equals("0")) {
                        clientInfo.getAccount().userName = result;
                    }
                    output.println(symmetric.encrypt("enter the new email -- if not enter 0"));
                    result = symmetric.decrypt(input.next());
                    if (!result.equals("0")) {
                        clientInfo.getAccount().setEmail(result);
                    }
                    output.println(symmetric.encrypt("enter the new description -- if not enter 0"));
                    result = symmetric.decrypt(input.next());
                    if (!result.equals("0")) {
                        clientInfo.getAccount().description = result;
                    }
                    dataBase.executeQuery(clientInfo, "editAccount");
                    break;
                case "4":
                    output.println(symmetric.encrypt("enter the account title you want to delete"));
                    clientInfo.getAccount().title = symmetric.decrypt(input.next());
                    dataBase.executeQuery(clientInfo, "deleteAccount");
                    break;
                default:
                    output.println(symmetric.encrypt("Wrong Input"));
                    break;
            }
            output.println(symmetric.encrypt("****  ..<<  Done >>..  ****"));

        } catch (Exception e) {
            System.out.println("Error:" + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.out.println("Closed: " + socket);
        }
    }
}
