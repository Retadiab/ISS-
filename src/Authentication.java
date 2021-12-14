import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Authentication {
    Socket socket;
    DataBase dataBase;
    Symmetric symmetric;
    public Authentication(Socket socket, DataBase dataBase) {
        this.socket = socket;
        this.dataBase = dataBase;
    }

    public Boolean verifyPassword(ClientInfo clientInfo, String userName, String passWord) {
        clientInfo.userName = userName;
        String password = dataBase.executeQuery(clientInfo, "login");
        clientInfo.setPassword(password);
        return passWord.equals(password);
    }

    public Boolean signUpUser(ClientInfo clientInfo, String userName, String passWord) {
        clientInfo.userName = userName;
        clientInfo.setPassword(passWord);
        clientInfo.ID = (ServerForMultiUsers.IDCounter+1);
        String result = dataBase.executeQuery(clientInfo, "addUser");
        return !result.equals("error");
    }

    public Boolean loginOrSignUpProcess(ClientInfo clientInfo) {
        symmetric = new Symmetric();

        boolean readyToGo = false;
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner input = new Scanner(socket.getInputStream());

            output.println(symmetric.encrypt("Please enter *** 1 *** if you would like to LOGIN  or *** 2 *** if you would like to SIGN UP"));
            String userChoice =  symmetric.decrypt(input.next());

            switch (userChoice) {
                case "1":
                    output.println(symmetric.encrypt("Please enter your username: "));
                    String userUsername = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("Please enter your password: "));
                    String userPassword = symmetric.decrypt(input.next());
                    readyToGo = verifyPassword(clientInfo, userUsername, userPassword);
                    break;
                case "2":
                    output.println(symmetric.encrypt("Please enter A username: "));
                    String userUsername1 = symmetric.decrypt(input.next());
                    output.println(symmetric.encrypt("Please enter your password: "));
                    String userPassword1 = symmetric.decrypt(input.next());
                    readyToGo = signUpUser(clientInfo, userUsername1, userPassword1);
                    break;
            }
            return readyToGo;

        } catch (Exception e) {
            System.out.println("Error:" + socket);
            return false;
        }
    }
}
