package galaga.gscript;

import java.io.File;

import galaga.gscript.parser.ParserException;

public class GScriptError {
    private final String message;

    public static GScriptError io(File file, Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("GScript failed to read file:\n");
        sb.append(file.getAbsolutePath()).append("\n");
        sb.append(e.getMessage()).append("\n");
        return new GScriptError(sb.toString());
    }

    public static GScriptError parse(File file, ParserException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("GScript failed to parse file:\n");
        sb.append(file.getAbsolutePath()).append("\n");
        sb.append(e.getMessage()).append("\n");
        return new GScriptError(sb.toString());
    }

    private GScriptError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
