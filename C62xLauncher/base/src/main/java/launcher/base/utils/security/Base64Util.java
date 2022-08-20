package launcher.base.utils.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
    private static final String UTF_8 = "UTF-8";

    public static String encode(String origin) {
        byte[] encode = Base64.getEncoder().encode(origin.getBytes());
        return new String(encode);
    }
    public static String encodeHex(String hex) {
        return Base64Util.encode(HexUtil.decodeHex(hex.toCharArray()));
    }

    public static String encode(byte[] bytes) {
        byte[] encode = Base64.getEncoder().encode(bytes);
        return new String(encode);
    }
}
