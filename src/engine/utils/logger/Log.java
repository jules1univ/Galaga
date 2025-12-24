package engine.utils.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Log {

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
}
