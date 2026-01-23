package galaga.gscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.IdentifierExpression;
import galaga.gscript.ast.expression.LiteralExpression;
import galaga.gscript.ast.expression.collection.IndexExpression;
import galaga.gscript.ast.expression.collection.ListExpression;
import galaga.gscript.ast.expression.function.CallExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.statement.AssignmentStatement;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.formatter.Formatter;
import galaga.gscript.interpreter.Interpreter;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.parser.Parser;
import galaga.gscript.types.values.FloatValue;
import galaga.gscript.types.values.IntegerValue;
import galaga.gscript.types.values.StringValue;
import galaga.gscript.types.values.Value;

public class GScript {

    public static void main(String[] args) {
        Program program = new Program(List.of(
                new NativeFunctionDeclaration("print", List.of("value")),
                new VariableDeclaration("HELLO", new LiteralExpression(new IntegerValue(15)), true),
                new VariableDeclaration("globalHello", new LiteralExpression(new FloatValue(12)), false),
                new FunctionDeclaration("create_list", List.of(), new BlockStatement(List.of(
                        new AssignmentStatement(
                                "my_list",
                                Operator.ASSIGN,
                                new ListExpression(List.of(
                                        new IdentifierExpression("print"),
                                        new LiteralExpression(new IntegerValue(2)),
                                        new LiteralExpression(new IntegerValue(3)))),
                                false)))),
                new FunctionDeclaration("main", List.of(), new BlockStatement(List.of(
                        new AssignmentStatement("list", Operator.ASSIGN, new CallExpression(
                                new IdentifierExpression("create_list"), List.of()), false),
                        new ExpressionStatement(
                                new CallExpression(
                                        new IndexExpression(new IdentifierExpression("list"),
                                                new LiteralExpression(new IntegerValue(0))),
                                        List.of(
                                            new LiteralExpression(new StringValue("Hello World !"))
                                        ))),
                        new IfStatement(
                                new BinaryExpression(
                                        new IdentifierExpression("HELLO"),
                                        Operator.GREATER_THAN,
                                        new LiteralExpression(new FloatValue(3.14f))),
                                new BlockStatement(List.of(
                                        new ExpressionStatement(
                                                new CallExpression(new IdentifierExpression("print"),
                                                        List.of(new IdentifierExpression("HELLO")))))),
                                Optional.empty()))))));

        System.out.println(Formatter.format(program));

        Interpreter interpreter = new Interpreter();
        interpreter.getContext().defineNative("print", (Map<String, Value> funcArgs) -> {
            for (Value val : funcArgs.values()) {
                System.out.println(val);
            }
            return null;
        });
        interpreter.run(program);
        interpreter.callFunction("main");

        // GScript script = GScript.of(".\\tests\\main.gscript");
        // script.run();
        // script.callFunction("main");
    }

    private final Lexer lexer;
    private final Interpreter interpreter = new Interpreter();
    private Program program;

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
    }

    public void run() {
        try {
            Parser parser = new Parser(lexer);
            this.program = parser.parse();

            interpreter.run(program);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Program getProgram() {
        return this.program;
    }

    public String getFormattedSource() {
        return Formatter.format(this.program);
    }

    public Optional<Value> getVariable(String name) {
        return this.interpreter.getVariable(name);
    }

    public Optional<Value> callFunction(String name) {
        return callFunction(name, new Value[] {});
    }

    public Optional<Value> callFunction(String name, Value... args) {
        return this.interpreter.callFunction(name, args);
    }
}
