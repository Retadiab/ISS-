import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;


// nc localhost 11111
public class Server implements Symmetric, Runnable {
    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";
    Socket socket;
    DataBase dataBase;
    Authentication authentication;

    Server(DataBase dataBase, Socket socket) {
        this.socket = socket;
        this.dataBase = dataBase;
    }

    @Override
    public void run() {
        System.out.println("Connected: " + socket);
        ClientInfo clientInfo = new ClientInfo();
        authentication = new Authentication(socket, dataBase);

        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner input = new Scanner(socket.getInputStream());

            boolean valid = authentication.loginOrSignUpProcess(clientInfo);
            if (!valid) {
                output.println("failed in authentication : << connection will stop >>");
                socket.close();
            }
            output.println("Please enter or *** 1 *** for addAccount or *** 2 *** for showAccount or *** 3 *** for editAccount or *** 4 *** for deleteAccount");

            int userChoice = input.nextInt();
            switch (userChoice) {
                case 1:
                    Account account = new Account();
                    output.println("Enter Account Title");
                    account.title = input.next();
                    output.println("Enter Account UserName");
                    account.userName = input.next();
                    output.println("Enter Account Email");
                    account.setEmail(input.next());
                    output.println("Enter Account Description");
                    account.description = input.next();
                    output.println("Enter Account password");
                    String value = input.next();
                    account.setPassword(value);
                    output.println("Enter attachment file to your account");
                    account.file = input.next();
                    clientInfo.setAccount(account);
                    dataBase.executeQuery(clientInfo, "addAccount");
                    break;
                case 2:
                    output.println("Enter Account Title you want to show");
                    clientInfo.getAccount().title = input.next();
                    dataBase.executeQuery(clientInfo, "showAccount");
                    break;
                case 3:
                    String result;
                    output.println("Enter what the title you want to edit");
                    clientInfo.getAccount().title = input.next();
                    output.println("enter the new password -- if not enter 0");
                    result = input.next();
                    if (!result.equals("0")) {
                        clientInfo.getAccount().setPassword(result);
                    }
                    output.println("enter the new userName -- if not enter 0");
                    result = input.next();
                    if (!result.equals("0")) {
                        clientInfo.getAccount().userName = result;
                    }
                    output.println("enter the new email -- if not enter 0");
                    result = input.next();
                    if (!result.equals("0")) {
                        clientInfo.getAccount().setEmail(result);
                    }
                    output.println("enter the new description -- if not enter 0");
                    result = input.next();
                    if (!result.equals("0")) {
                        clientInfo.getAccount().description = result;
                    }
                    dataBase.executeQuery(clientInfo, "editAccount");
                    break;
                case 4:
                    output.println("enter the account title you want to delete");
                    clientInfo.getAccount().title = input.next();
                    dataBase.executeQuery(clientInfo, "deleteAccount");
                    break;
                default:
                    output.println("Wrong Input");
                    break;
            }

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


    @java.lang.Override
    public String decrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encrypted = Base64.getDecoder().decode(data);

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @java.lang.Override
    public String encrypt(String data) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv); // or Cipher.DECRYPT_MODE

        byte[] encrypted = cipher.doFinal(data.getBytes());

        String s = Base64.getEncoder().encodeToString(encrypted);
        System.out.println(s);
        return s;
    }


}
