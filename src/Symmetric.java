import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

class Symmetric {
    private String key = "0123456789101112";
    private final String initVector = "encryptionIntVec";

    //TODo change the encryptionIntVec

    public void setKey(String key) {
        this.key = key;
    }

    public String encrypt(String data) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {

        //prepare encrypted cipher
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv); // or Cipher.DECRYPT_MODE
        byte[] encrypted = cipher.doFinal(data.getBytes());
        String s = Base64.getEncoder().encodeToString(encrypted);

        //prepare the MAC
        SecretKey macKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(macKey);
        hmac.update(encrypted);
        byte[] mac = hmac.doFinal(encrypted);
        String sWithMac = Base64.getEncoder().encodeToString(mac);

        //prepare the MAC + DATA to send // from 0-> 44 is the MAC &&& from  44-> 68 is the DATA
        String finalS = sWithMac + s;
        //        System.out.println(finalS.substring(0,44));
        //        System.out.println(finalS.substring(44,68));

        return finalS;
    }


    public String decrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        //separating DATA FROM MAC
        //mac
        String incomingMac = data.substring(0, 44);
        byte[] encryptedMac = Base64.getDecoder().decode(incomingMac);

        //data
        String originalData = data.substring(44);
        byte[] encryptedData = Base64.getDecoder().decode(originalData);

        //decrypt data
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        //Mac computing
        SecretKey macKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(macKey);
        hmac.update(encryptedData);
        byte[] mac = hmac.doFinal(encryptedData);

        //checking the right MAC
        if (!MessageDigest.isEqual(mac, encryptedMac)) {
            throw new SecurityException("could not authenticate");
        }

        //return the data
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

//throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

}