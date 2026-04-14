import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String uri;
    private String version;
    private Map<String, String> headers;
    public static final SimpleDateFormat HTTP_DATE_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    public HttpRequest() {
        headers = new HashMap<>();}

    public boolean parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return false;}
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            return false;}



        method = parts[0];
        uri = parts[1];
        version = parts[2];
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            // DEBUG: Print all received headers
            System.out.println("DEBUG: Received header: " + line);
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);}}
        return true;}

    public String getMethod() { return method; }
    public String getUri() { return uri; }
    public String getVersion() { return version; }
    public String getHeader(String name) { return headers.get(name); }
    public String getFilePath(String wwwRoot) {
        String path = uri.split("\\?")[0];

        if (path.equals("/")) {
            path = "index.html";
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.contains("..")) {
            return null;
        }return wwwRoot + path;}

    public Date getIfModifiedSince() {
        String header = getHeader("If-Modified-Since");
        if (header == null) return null;
        try {
            return HTTP_DATE_FORMAT.parse(header);
        } catch (Exception e) {
            return null;}}

    public boolean isKeepAlive() {
        String connection = getHeader("Connection");
        System.out.println("DEBUG: Connection header = " + connection);
        if (connection == null) {
            return false;}
        return connection.equalsIgnoreCase("keep-alive");}}