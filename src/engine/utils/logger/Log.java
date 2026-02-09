package engine.utils.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import engine.Application;

public final class Log {
    private static Path reportFolder = null;

    private static void log(String message, LogStatus status) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String className = "MAIN";
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            className = caller.getClassName() + "::" + caller.getMethodName();
        }

        System.out.println("[" + currentTime + " " + status + "] (" + className + ") " + message);
    }

    public static void message(String message) {
        log(message, LogStatus.MESSAGE);
    }

    public static void message(String message, Object... args) {
        log(String.format(message, args), LogStatus.MESSAGE);
    }

    public static void warning(String message) {
        log(message, LogStatus.WARNING);
    }

    public static void warning(String message, Object... args) {
        log(String.format(message, args), LogStatus.WARNING);
    }

    public static void error(String message) {
        log(message, LogStatus.ERROR);
    }

    public static void error(String message, Object... args) {
        log(String.format(message, args), LogStatus.ERROR);
    }

    public static void report(Exception exception) {
        error("An unexpected error occurred: %s", exception.getMessage());
        if (Application.DEBUG_MODE) {
            exception.printStackTrace();
        }

        if (reportFolder != null) {
            Path reportFile = reportFolder.resolve("error_" + System.currentTimeMillis() + ".log");
            StringBuilder reportContent = new StringBuilder();
            reportContent.append("Exception: ").append(exception.toString()).append("\n");
            for (StackTraceElement element : exception.getStackTrace()) {
                reportContent.append("\tat ").append(element.toString()).append("\n");
            }

            try (BufferedWriter bf = new BufferedWriter(new FileWriter(reportFile.toFile()))) {
                bf.write(reportContent.toString());
                message("Error report saved to: %s", reportFile.toAbsolutePath());

            } catch (Exception e) {
                error("Failed to write error report: %s", e.getMessage());
            }
        }
    }

    public static void setReportFolder(String folderPath) {
        reportFolder = Path.of(folderPath);
        if (!reportFolder.toFile().exists()) {
            try {
                reportFolder.toFile().mkdirs();
                message("Created log report folder at: %s", reportFolder.toAbsolutePath());
            } catch (Exception e) {
                error("Failed to create log report folder: %s", e.getMessage());
                reportFolder = null;
            }
        } else {
            message("Log report folder set to: %s", reportFolder.toAbsolutePath());
        }
    }

    public static Optional<String> input(String prompt, Object... args) {
        System.out.print(String.format(prompt, args));
        try {
            byte[] inputBytes = new byte[256];
            int bytesRead = System.in.read(inputBytes);

            if (bytesRead == -1) {
                return Optional.empty();
            }

            return Optional.of(new String(inputBytes, 0, bytesRead).trim());
        } catch (IOException e) {
            error("Log failed to read input: %s", e.getMessage());
            return Optional.of("");
        }
    }

}
