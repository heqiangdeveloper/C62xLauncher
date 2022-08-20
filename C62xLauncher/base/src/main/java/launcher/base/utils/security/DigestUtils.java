package launcher.base.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DigestUtils {
	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
	
    protected static String encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }
    
    public static byte[] md5(byte[] data) {
        return getDigest("MD5").digest(data);
    }
    
    public static String md5Hex(String data) {
        return encodeHex(md5(utf8Bytes(data)));
    }
    
    public static String md5Hex(byte[] data) {
        return encodeHex(md5(data));
    }

	private static byte[] utf8Bytes(String data) {
		try {
			return data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String sha1Hex(final String data) {
        return new String(encodeHex(sha1(data)));
    }
	
	public static byte[] sha1(final String data) {
        return sha1(utf8Bytes(data));
    }
	public static byte[] sha1(final byte[] data) {
        return getDigest("SHA-1").digest(data);
    }


    public static String hmacSha256(String key, String value) {
        String SHA_TYPE = "HmacSHA256";
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(value.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ ) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ComputeError";
    }
}