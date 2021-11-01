import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    public static String pswrdEncoding (String pswrd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(pswrd.getBytes(StandardCharsets.UTF_8),0,pswrd.length());
        return new BigInteger(1, md.digest()).toString(16);
    }

}
