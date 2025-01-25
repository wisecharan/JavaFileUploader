import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploader {
    public static void main(String[] args) {
        // Replace with the file path you want to upload
        String filePath = "C:/path/to/your/file.txt"; 
        // Replace with your server URL
        String serverURL = "https://your-server.com/upload";

        try {
            uploadFile(filePath, serverURL);
        } catch (IOException e) {
            System.err.println("Error during file upload: " + e.getMessage());
        }
    }

    public static void uploadFile(String filePath, String serverURL) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        System.out.println("Uploading file: " + file.getName());

        // Connect to the server
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the connection for POST
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----BoundaryMagic");

        // Write the file to the server
        try (OutputStream outputStream = connection.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(file)) {

            // Write multipart form data header
            outputStream.write(("------BoundaryMagic\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: application/octet-stream\r\n\r\n").getBytes());

            // Write file content
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Write closing boundary
            outputStream.write("\r\n------BoundaryMagic--\r\n".getBytes());
            outputStream.flush();
        }

        // Check the server response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("File uploaded successfully!");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server Response: " + line);
                }
            }
        } else {
            System.err.println("File upload failed. Server responded with code: " + responseCode);
        }

        connection.disconnect();
    }
}
