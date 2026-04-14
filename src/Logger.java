import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {
    private PrintWriter writer;
    private ReentrantLock lock;
    public Logger(String logFilePath) {
        lock = new ReentrantLock();
        try {
            File logFile = new File(logFilePath);
            logFile.getParentFile().mkdirs();
            writer = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException e) {
            System.err.println("Failed to create logger: " + e.getMessage());
            writer = new PrintWriter(System.out, true);}}


    public void log(String clientAddress, String accessTime,
                    String requestedFile, String responseType) {
        lock.lock();
        try {
            String logEntry = String.format("%s - %s - %s - %s",
                    clientAddress, accessTime, requestedFile, responseType);
            writer.println(logEntry);
            System.out.println("[LOG] " + logEntry);
        } finally {
            lock.unlock();}}

    public void close() {
        if (writer != null) {
            writer.close();}}}