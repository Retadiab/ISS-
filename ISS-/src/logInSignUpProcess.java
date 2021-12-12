import java.util.Scanner;

public class logInSignUpProcess {
    public LogIn login;
    public signUp signUp;

    public Boolean loginOrSignUp(){
        Scanner choice = new Scanner(System.in);  // Create a Scanner object
        Scanner username = new Scanner(System.in);  // Create a Scanner object
        Scanner password = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Welcome to the Password manager :) !!");
        System.out.println("\n Please enter ** 1 ** if you would like to LOGIN  or ** 2 ** if you would like to SIGN UP");
        int userChoice  = choice.nextInt();  // Read user input
        boolean readyToGo = false;
        switch (userChoice) {
            case 1:
                System.out.println("Please enter your username: ");
                String  userUsername  = username.nextLine();  // Read user input
                System.out.println("Please enter your password: ");
                String  userPassword  = password.nextLine();  // Read user input
                readyToGo = login.verifyPassword(userUsername, userPassword);
                break;
            case 2:
                System.out.println("Please enter A username: ");
                String  userUsername1  = username.nextLine();  // Read user input
                System.out.println("Please enter your password: ");
                String  userPassword1  = password.nextLine();  // Read user input
                readyToGo = signUp.signUpUser(userUsername1, userPassword1);
                break;

        }
        return readyToGo;
    }
    public static void main(String args[]){
        logInSignUpProcess l = new logInSignUpProcess();

        l.loginOrSignUp();

    }  //static method


}
