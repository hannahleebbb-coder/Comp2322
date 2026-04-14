import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HttpResponse {
    private OutputStream output;
    private String version = "HTTP/1.1";

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    public void sendResponse(int statusCode, String statusMessage,
                             String contentType, File file,
                             Date lastModified, boolean isHead, boolean keepAlive)
            throws IOException {
        StringBuilder response = new StringBuilder();

        response.append(version).append(" ")
                .append(statusCode).append(" ")
                .append(statusMessage).append("\r\n");

        response.append("Server: MultiThreadWebServer/1.0\r\n");
        response.append("Date: ").append(formatDate(new Date())).append("\r\n");

        if (contentType != null) {
            response.append("Content-Type: ").append(contentType).append("\r\n");
        }

        if (lastModified != null) {
            response.append("Last-Modified: ").append(formatDate(lastModified))
                    .append("\r\n");
        }

        if (file != null && file.exists() && !isHead) {
            response.append("Content-Length: ").append(file.length()).append("\r\n");}

       
        if (keepAlive) {
            response.append("Connection: keep-alive\r\n");
        } else {
            response.append("Connection: close\r\n");}

        response.append("\r\n");

        output.write(response.toString().getBytes());

        if (file != null && file.exists() && !isHead) {
            sendFile(file);
        }
        output.flush();}

    public void send404(String requestedFile, boolean keepAlive) throws IOException {
        String body = "<html><body><h1>404 Not Found</h1><p>File not found: " + requestedFile + "</p></body></html>";
        StringBuilder response = new StringBuilder();
        response.append(version).append(" 404 Not Found\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("Content-Length: ").append(body.length()).append("\r\n");
        if (keepAlive) {
            response.append("Connection: keep-alive\r\n");
        } else {

            response.append("Connection: close\r\n");
        }
        response.append("\r\n");
        response.append(body);
        output.write(response.toString().getBytes());
        output.flush();
    }

    public void send400(boolean keepAlive) throws IOException {
        String body = "<html><body><h1>400 Bad Request</h1></body></html>";
        StringBuilder response = new StringBuilder();
        response.append(version).append(" 400 Bad Request\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("Content-Length: ").append(body.length()).append("\r\n");
        if (keepAlive) {
            response.append("Connection: keep-alive\r\n");
        } else {
            response.append("Connection: close\r\n");
        }
        response.append("\r\n");
        response.append(body);
        output.write(response.toString().getBytes());
        output.flush();
    }



    public void send403(boolean keepAlive) throws IOException {
        String body = "<html><body><h1>403 Forbidden</h1></body></html>";
        StringBuilder response = new StringBuilder();
        response.append(version).append(" 403 Forbidden\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("Content-Length: ").append(body.length()).append("\r\n");
        if (keepAlive) {
            response.append("Connection: keep-alive\r\n");
        } else {
            response.append("Connection: close\r\n");
        }
        response.append("\r\n");
        response.append(body);
        output.write(response.toString().getBytes());
        output.flush();

    }

    public void send304(boolean keepAlive) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append(version).append(" 304 Not Modified\r\n");
        if (keepAlive) {
            response.append("Connection: keep-alive\r\n");
        } else {
            response.append("Connection: close\r\n");
        }
        response.append("\r\n");
        output.write(response.toString().getBytes());
        output.flush();}

    private void sendFile(File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }fis.close();}

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        return sdf.format(date);}}
