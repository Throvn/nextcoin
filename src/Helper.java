import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Base64;

public class Helper {
    public static String toSha256 (String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); //Digest = Verarbeitungsmethode
            byte [] hashedValue = digest.digest(input.getBytes());

            //Keine ahnung
            String result = "";
            for (int i = 0; i < hashedValue.length; i++) {
                String byteToHex = Integer.toHexString(0xff & hashedValue[i]);
                if(byteToHex.length() == 1) result += '0';
                result += byteToHex;
            }
            return result;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public static String publicKeyToString (PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


}
