import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final int PORT = 8080;
    private static final String WWW_ROOT = "www/";
    private static final int THREAD_POOL_SIZE = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Logger logger;

    public WebServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            logger = new Logger("logs/server.log");
            System.out.println("Web server started on port " + PORT);
            System.out.println("Serving files from: " + WWW_ROOT);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);}}



    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: " +
                        clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(
                        clientSocket, WWW_ROOT, logger);
                threadPool.execute(handler);
            } catch (IOException e) {
                System.err.println("Error accepting connection: " + e.getMessage());}}}

    public void shutdown() {
        try {
            serverSocket.close();
            threadPool.shutdown();
            logger.close();
        } catch (IOException e) {
            e.printStackTrace();}}



    public static void main(String[] args) {
        WebServer server = new WebServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();
        }));
        server.start();}}