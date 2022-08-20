package launcher.base.utils.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class Base64UtilTest {

    String origin = "841bea74df443d73eadfd805db75b4a2ccb55a3dc2f38f1071d0c96d441f91ff";
    String result1 = "ODQxYmVhNzRkZjQ0M2Q3M2VhZGZkODA1ZGI3NWI0YTJjY2I1NWEzZGMyZjM4ZjEwNzFkMGM5NmQ0NDFmOTFmZg";
    String result2 = "hBvqdN9EPXPq39gF23W0osy1Wj3C848QcdDJbUQfkf8=";

    @Test
    public void encode() {
        String s = Base64Util.encode(HexUtil.decodeHex(origin.toCharArray()));
        System.out.println("result: "+s);
        assert s.equals(result2);
    }
}