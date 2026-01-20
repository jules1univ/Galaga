package galaga.gscript.util;

import java.io.File;

import galaga.gscript.parser.ParserException;

public class Message {
    private final String message;
    private final MessageLevel level;

    public static Message io(File file, Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to read file:\n");
        sb.append(file.getAbsolutePath()).append("\n");
        sb.append(e.getMessage()).append("\n");
        return new Message(sb.toString(), MessageLevel.ERROR);
    }

    public static Message parse(File file, ParserException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to parse file:\n");
        sb.append(file.getAbsolutePath()).append("\n");
        sb.append(e.getMessage()).append("\n");
        return new Message(sb.toString(), MessageLevel.ERROR);
    }

    public static Message of(String message, MessageLevel level) {
        return new Message(message, level);
    }

    private Message(String message, MessageLevel level) {
        this.message = message;
        this.level = level;
    }

    public MessageLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "[" + level.name() + "] " + message;
    }
}
