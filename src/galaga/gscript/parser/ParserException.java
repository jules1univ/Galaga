package galaga.gscript.parser;

public class ParserException extends Exception {
    
    public ParserException(ParserContext context, String message, Object... args) {
        super(context.getErrorContext(String.format(message, args)));
    }
    
}
