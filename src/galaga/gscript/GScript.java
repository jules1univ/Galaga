package galaga.gscript;

import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.IdentifierExpression;
import galaga.gscript.ast.expression.LiteralExpression;
import galaga.gscript.ast.expression.function.CallExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.formatter.Formatter;
import galaga.gscript.interpreter.Interpreter;
import galaga.gscript.interpreter.Scope;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.types.values.FloatValue;
import galaga.gscript.types.values.IntegerValue;
import galaga.gscript.types.values.NullValue;

public class GScript {

    public static void main(String[] args) {
        Program program = new Program(List.of(
                new NativeFunctionDeclaration("print", List.of("value")),
                new VariableDeclaration("HELLO", new LiteralExpression(new IntegerValue(15)), true),
                new VariableDeclaration("globalHello", new LiteralExpression(new IntegerValue(12)), false),
                new FunctionDeclaration("main", List.of(), new BlockStatement(List.of(
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

        Formatter formatter = new Formatter();
        System.out.println(formatter.format(program));

        Interpreter interpreter = new Interpreter();
        interpreter.defineNativeFunction("print", (Scope scope) -> {
            System.out.println(scope.getVariable("value").orElse(new NullValue()));
            return null;
        });
        interpreter.interpret(program);
        interpreter.callFunction("main");
    }

}
