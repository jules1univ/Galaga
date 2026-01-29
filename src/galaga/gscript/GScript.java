package galaga.gscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import galaga.gscript.ast.Program;
import galaga.gscript.formatter.Formatter;
import galaga.gscript.interpreter.Interpreter;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.parser.ParseError;
import galaga.gscript.parser.Parser;
import galaga.gscript.runtime.values.IntegerValue;
import galaga.gscript.runtime.values.ListValue;
import galaga.gscript.runtime.values.MapValue;
import galaga.gscript.runtime.values.StringValue;
import galaga.gscript.runtime.values.Value;

public class GScript {

    public static void main(String[] args) {

        // Interpreter interpreter = new Interpreter();
        // interpreter.getContext().defineNative("print", (Map<String, Value> funcArgs)
        // -> {
        // for (Value val : funcArgs.values()) {
        // System.out.println(val.getValue().toString());
        // }
        // return null;
        // });
        // interpreter.run(program);
        // interpreter.callFunction("main");

        GScript gscript = new GScript();
        gscript.load("test", new File("./src/galaga/gscript/tests/main.gscript"));

        List<ParseError> parseErrors = gscript.parse("test");
        System.out.println("Parse Errors: " + parseErrors.size());
        for (ParseError error : parseErrors) {
            System.out.println(error.displayContext(gscript.getSource("test")));
        }

        Interpreter interpreter = new Interpreter();
        interpreter.getContext().defineNative("print", (Map<String, Value> funcArgs) -> {
            for (Value val : funcArgs.values()) {
                System.out.println(val.getValue().toString());
            }
            return null;
        });
        interpreter.getContext().defineNative("len", (Map<String, Value> funcArgs) -> {
            if (funcArgs.values().size() != 1) {
                throw new RuntimeException("len() takes exactly one argument.");
            }

            Value val = funcArgs.values().iterator().next();
            
            if (val instanceof ListValue listVal) {
                return new IntegerValue(listVal.getValue().size());
            }

            if (val instanceof MapValue mapVal) {
                return new IntegerValue(mapVal.getValue().size());
            }

            if (val instanceof StringValue strVal) {
                return new IntegerValue(strVal.getValue().length());
            }

            throw new RuntimeException("len() argument must be a list, string, or map.");
        });
        interpreter.run(gscript.getProgram("test"));
        interpreter.callFunction("main");
    }

    private final Map<String, String> files = new HashMap<>();
    private final Map<String, Program> programs = new HashMap<>();

    public GScript() {
    }

    public boolean load(String name, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            this.files.put(name, sb.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<ParseError> parse(String name) {
        if (!this.files.containsKey(name)) {
            return null;
        }

        try {
            Lexer lexer = new Lexer(this.files.get(name));
            Parser parser = new Parser(lexer);

            Program program = parser.parse();

            this.programs.put(name, program);
            return parser.getErrors();
        } catch (Exception e) {
            return List.of(new ParseError(Token.EOF, e.getMessage()));
        }
    }

    public String getSource(String name) {
        return this.files.get(name);
    }

    public Program getProgram(String name) {
        return this.programs.get(name);
    }

    public String getFormattedProgram(String name) {
        if (!this.files.containsKey(name)) {
            return null;
        }

        return Formatter.format(this.programs.get(name));
    }
}
