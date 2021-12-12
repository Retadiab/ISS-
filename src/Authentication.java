import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Authentication {
    Socket socket;
    DataBase dataBase;

    public Authentication(Socket socket, DataBase dataBase) {
        this.socket = socket;
        this.dataBase = dataBase;
    }

    public Boolean verifyPassword(ClientInfo clientInfo, String userName, String passWord) {
        clientInfo.userName = userName;
        String password = dataBase.executeQuery(clientInfo, "login");
        return passWord.equals(password);
    }

    public Boolean signUpUser(ClientInfo clientInfo, String userName, String passWord) {
        clientInfo.userName = userName;
        clientInfo.setPassword(passWord);
        clientInfo.ID = ServerForMultiUsers.IDCounter++;

        String result = dataBase.executeQuery(clientInfo, "addUser");
        System.out.println("......................................................"+result);
        return !result.equals("error");
    }

    public Boolean loginOrSignUpProcess(ClientInfo clientInfo) {

        boolean readyToGo = false;
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner input = new Scanner(socket.getInputStream());

            output.println("Please enter *** 1 *** if you would like to LOGIN  or *** 2 *** if you would like to SIGN UP");
            int userChoice = input.nextInt();

            switch (userChoice) {
                case 1:
                    output.println("Please enter your username: ");
                    String userUsername = input.next();
                    output.println("Please enter your password: ");
                    String userPassword = input.next();
                    readyToGo = verifyPassword(clientInfo, userUsername, userPassword);
                    break;
                case 2:
                    output.println("Please enter A username: ");
                    String userUsername1 = input.nextLine();
                    output.println("Please enter your password: ");
                    String userPassword1 = input.nextLine();
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
