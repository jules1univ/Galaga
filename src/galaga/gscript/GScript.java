package galaga.gscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import galaga.gscript.ast.Program;
import galaga.gscript.formatter.Formatter;
import galaga.gscript.interpreter.Interpreter;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.parser.Parser;
import galaga.gscript.types.values.Value;

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

        GScript script = GScript.of(".\\tests\\main.gscript");
        System.out.println("Formatted Source:\n" + script.format());

        // script.run();
        // script.callFunction("main");
    }

    private final Lexer lexer;
    private final Program program;
    private final Interpreter interpreter = new Interpreter();

    public static GScript of(String source) {
        return new GScript(source);
    }

    public static GScript of(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return new GScript(sb.toString());
        } catch (IOException e) {
            return null;
        }
    }

    private GScript(String source) {
        this.lexer = new Lexer(source);
        this.program =  new Parser(lexer).parse();
    }

    public void run() {
        try {
            this.interpreter.run(program);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String format() {
        return Formatter.format(this.program);
    }

    public Optional<Value> getVariable(String name) {
        return this.interpreter.getVariable(name);
    }

    public Optional<Value> callFunction(String name) {
        return this.callFunction(name, new Value[] {});
    }

    public Optional<Value> callFunction(String name, Value... args) {
        return this.interpreter.callFunction(name, args);
    }
}
