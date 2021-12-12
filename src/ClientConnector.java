import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class ClientConnector implements Symmetric {
    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    public static void main(String[] args) {
        connectToServer();
    }

    public static void connectToServer() {
        try (Socket socket = new Socket("127.0.0.1", 11111)) {
            Scanner scanner = new Scanner(System.in);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                System.out.println(in.nextLine());
                out.println(scanner.nextLine());

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
