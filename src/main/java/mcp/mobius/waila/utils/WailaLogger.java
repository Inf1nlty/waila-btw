package mcp.mobius.waila.utils;


import mcp.mobius.waila.Waila;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WailaLogger {

    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static WailaLogger instance;
    private Level level;
    private BufferedWriter fileWriter;
    private boolean consoleOutput;
    private boolean fileOutput;
    private final SimpleDateFormat dateFormat;

    private WailaLogger() {
        this.level = Level.INFO;
        this.consoleOutput = true;
        this.fileOutput = false;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static synchronized WailaLogger getInstance() {
        if (instance == null) {
            instance = new WailaLogger();
        }
        return instance;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setConsoleOutput(boolean enabled) {
        this.consoleOutput = enabled;
    }

    public void setLogFile(String filePath) throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
        }

        File file = new File(filePath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        this.fileWriter = new BufferedWriter(new FileWriter(file, true));
        this.fileOutput = true;
    }

    public void closeFileOutput() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
            fileOutput = false;
        }
    }

    private void log(Level level, String message, Throwable throwable) {
        if (level.ordinal() < this.level.ordinal()) {
            return;
        }

        String timestamp = dateFormat.format(new Date());
        String modName = Waila.modName;
        String logMessage = String.format("%s [%s] [%s] %s", timestamp, modName, level, message);

        if (consoleOutput) {
            System.out.println(logMessage);
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }

        if (fileOutput && fileWriter != null) {
            try {
                fileWriter.write(logMessage);
                fileWriter.newLine();
                if (throwable != null) {
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        fileWriter.write("\t" + element.toString());
                        fileWriter.newLine();
                    }
                }
                fileWriter.flush();
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }

    public void log(Level level, String message) {
        log(level, message, null);
    }

    public void trace(String message) {
        log(Level.TRACE, message, null);
    }

    public void debug(String message) {
        log(Level.DEBUG, message, null);
    }

    public void info(String message) {
        log(Level.INFO, message, null);
    }

    public void warn(String message) {
        log(Level.WARN, message, null);
    }

    public void warn(String message, Throwable throwable) {
        log(Level.WARN, message, throwable);
    }

    public void error(String message) {
        log(Level.ERROR, message, null);
    }

    public void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }
}
