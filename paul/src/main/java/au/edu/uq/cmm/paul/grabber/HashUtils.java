package au.edu.uq.cmm.paul.grabber;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import au.edu.uq.cmm.paul.PaulException;


public class HashUtils {
    
    private static String ALGORITHM = "SHA-512";
    private static int HASH_STRING_LENGTH = 128; // should the hash length in bytes * 2

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append("0123456789ABCDEF".charAt(0xF & (b >> 4)));
            sb.append("0123456789ABCDEF".charAt(0xF & b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String str) throws InvalidHashException {
        byte[] res = new byte[str.length() / 2];
        for (int i = 0; i < res.length; i++) {
            try {
                res[i] = Byte.parseByte(str.substring(i * 2, 2));
            } catch (NumberFormatException ex) {
                throw new InvalidHashException("Hash string contains invalid characters");
            }
        }
        return res;
    }
    
    public static String combineHashes(String hash1, String hash2) throws InvalidHashException {
        if (hash1 == null) {
            return hash2;
        } else if (hash2 == null) {
            return hash1;
        }
        if (hash1.length() != HASH_STRING_LENGTH || hash2.length() != HASH_STRING_LENGTH) {
            throw new InvalidHashException("Incorrect hash string length");
        }
        byte[] h1 = hexStringToBytes(hash1);
        byte[] h2 = hexStringToBytes(hash2);
        for (int i = 0; i < h1.length; i++) {
            h1[i] = (byte) (h1[i] ^ h2[i]);
        }
        return bytesToHexString(h1);
    }
    
    public static MessageDigest createDigester() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new PaulException("Can't find the required secure hash algorithm", ex);
        }
    }
}
