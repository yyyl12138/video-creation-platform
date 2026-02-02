import java.net.URL;
import java.nio.file.Paths;
import java.io.InputStream;

public class TestUrl {
    public static void main(String[] args) throws Exception {
        String path = "/Users/yangli/Documents/自动化短视频创作平台/storage/avatar/U2026013035276_1769958516424.jpg";
        
        System.out.println("Path: " + path);

        // 1. Raw String concatenation (simulating 3 slashes)
        String rawUrl = "file://" + path; 
        System.out.println("\nTesting Raw URL: " + rawUrl);
        try {
            InputStream is = new URL(rawUrl).openStream();
            if (is != null) is.close();
            System.out.println("Raw URL: SUCCESS");
        } catch (Exception e) {
            System.out.println("Raw URL: FAILED - " + e);
            // e.printStackTrace();
        }

        // 2. Encoded URI (Paths.get().toUri())
        try {
            String encodedUrl = Paths.get(path).toUri().toString();
            System.out.println("\nTesting Encoded URL: " + encodedUrl);
            InputStream is = new URL(encodedUrl).openStream();
            if (is != null) is.close();
            System.out.println("Encoded URL: SUCCESS");
        } catch (Exception e) {
             System.out.println("Encoded URL: FAILED - " + e);
             // e.printStackTrace();
        }

         // 3. Single slash ? file:/Users...
        String singleSlashRequest = "file:" + path;
        System.out.println("\nTesting Single Slash URL: " + singleSlashRequest);
        try {
            InputStream is = new URL(singleSlashRequest).openStream();
            if (is != null) is.close();
            System.out.println("Single Slash URL: SUCCESS");
        } catch (Exception e) {
            System.out.println("Single Slash URL: FAILED - " + e);
        }
    }
}
