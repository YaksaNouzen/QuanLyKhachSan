package nhom3.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Mã hóa mật khẩu bằng SHA-256 trước khi lưu vào database.
 * VD: "123456" -> "8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92"
 */
public class MaHoa {

    public static String sha256(String matKhau) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] mangByte = md.digest(matKhau.getBytes(StandardCharsets.UTF_8));

            // Đổi từng byte sang 2 ký tự hex
            StringBuilder sb = new StringBuilder();
            for (byte b : mangByte) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
