import java.net.Socket;
import java.io.*;
import java.util.Date;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String wwwRoot;
    private Logger logger;
    public ClientHandler(Socket socket, String wwwRoot, Logger logger) {
        this.clientSocket = socket;
        this.wwwRoot = wwwRoot;
        this.logger = logger;}

    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(5000);

            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            HttpRequest request = new HttpRequest();
            if (!request.parse(input)) {
                HttpResponse response = new HttpResponse(output);
                response.send400(false); 
                return;}

            System.out.println("Request: " + request.getMethod() + " " + request.getUri());
            processRequest(request, output);

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();}}}

    private void processRequest(HttpRequest request, OutputStream output) throws IOException {
        String method = request.getMethod();
        String filePath = request.getFilePath(wwwRoot);
        HttpResponse response = new HttpResponse(output);
        // ADD THIS DEBUG LINE RIGHT HERE
        boolean keepAlive = request.isKeepAlive();
        System.out.println("DEBUG: keepAlive passed to response = " + keepAlive);
        if (filePath == null) {
            response.send403(keepAlive);
            logRequest(request, 403);
            return;}

        File file = new File(filePath);

        if (method.equals("GET") || method.equals("HEAD")) {
            if (!file.exists()) {
                response.send404(filePath, keepAlive);
                logRequest(request, 404);
                return;}

            if (!file.canRead()) {
                response.send403(keepAlive);
                logRequest(request, 403);
                return;}

            Date ifModifiedSince = request.getIfModifiedSince();
            long lastModified = file.lastModified();

            if (ifModifiedSince != null && lastModified <= ifModifiedSince.getTime()) {
                response.send304(keepAlive);
                logRequest(request, 304);
                return;}

            String contentType = getContentType(filePath);
            response.sendResponse(200, "OK", contentType, file,
                    new Date(lastModified), method.equals("HEAD"), keepAlive);
            logRequest(request, 200);
        } else {
            response.send400(keepAlive);
            logRequest(request, 400);}}

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            return "text/html";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";}}

    private void logRequest(HttpRequest request, int statusCode) {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        String accessTime = new Date().toString();
        String requestedFile = request.getUri();
        String responseType = getStatusMessage(statusCode);
        logger.log(clientAddress, accessTime, requestedFile, responseType);}

    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "200 OK";
            case 304: return "304 Not Modified";
            case 400: return "400 Bad Request";
            case 403: return "403 Forbidden";
            case 404: return "404 Not Found";
            default: return String.valueOf(statusCode);}}}
