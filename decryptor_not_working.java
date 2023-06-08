import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MyClass {
    public static void main(String args[]) {
        try {
            String password = "'L9Wf)JxF>P}J{PHjS8G";
            byte[] encrypted = Files.readAllBytes(Paths.get("/uploads/assets.dat"));
            byte[] decrypted = aes128Operation(true, encrypted, password);
            
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(decrypted));
            int mutex = in.readUnsignedShort();
            boolean preferIPv6 = in.readBoolean();
            boolean keyLoggerEnabled = in.readBoolean();
            String clientTag = in.readUTF();
            
            System.out.println("Mutex: " + mutex);
            System.out.println("Prefer IPv6: " + preferIPv6);
            System.out.println("Key Logger Enabled: " + keyLoggerEnabled);
            System.out.println("Client Tag: " + clientTag);
            
            int count = in.readInt();
            System.out.println("Addresses count: " + count);
            for (int i = 0; i < count; i++) {
                String host = in.readUTF();
                int port = in.readUnsignedShort();
                String passwordEntry = in.readUTF();
                System.out.println("Address Entry " + (i+1) + ": Host - " + host + ", Port - " + port + ", Password - " + passwordEntry);
            }
            
            // Reading WindowsConfig manually
            boolean addToStartup = in.readBoolean();
            String autostartPath = in.readUTF();
            String autostartName = in.readUTF();

            // Continue reading remaining fields as per the WindowsConfig class
            // ...

            System.out.println("Windows Config: Add to startup - " + addToStartup + ", Autostart Path - " + autostartPath + ", Autostart Name - " + autostartName);
        } catch (IOException ex) {
            throw new RuntimeException("Operation failed!", ex);
        }
    }

    private static byte[] aes128Operation(boolean decrypt, byte[] input, String password) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] pwBytes = MessageDigest.getInstance("MD5").digest(password.getBytes(StandardCharsets.UTF_8));
            cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, new SecretKeySpec(pwBytes, "AES"));
            return cipher.doFinal(input);
        } catch (Exception ex) { // Catching a more general Exception here
            throw new IOException("AES128 Operation failed!", ex);
        }
    }
}
