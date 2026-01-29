package galaga.gscript.interpreter.subinterpreter;

import java.util.Optional;

import galaga.gscript.ast.declaration.DeclarationVisitor;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.interpreter.InterpreterContext;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.types.values.FunctionValue;
import galaga.gscript.types.values.Value;

public class DeclarationInterpreter implements DeclarationVisitor<Value> {
    private final InterpreterContext context;

    public DeclarationInterpreter(InterpreterContext context) {
        super();
        this.context = context;
    }

    @Override
    public Value visitFunctionDeclaration(FunctionDeclaration node) {
        if (this.context.getScope().hasVariable(node.name().getValue())) {
            throw new RuntimeException("Function '" + node.name().getValue() + "' is already defined in this scope.");
        }

        FunctionValue functionValue = new FunctionValue(Optional.of(node.name().getValue()),
                node.parameters().stream().map(Token::getValue).toList(), node.body(), this.context.getScope(), false);
        this.context.getScope().setVariable(node.name().getValue(), functionValue);
        return null;
    }

    @Override
    public Value visitVariableDeclaration(VariableDeclaration node) {
        if (this.context.getScope().hasVariable(node.name().getValue())
                || this.context.getScope().hasConstant(node.name().getValue())) {
            throw new RuntimeException("Object '" + node.name().getValue() + "' is already defined in this scope.");
        }
        if (node.isConstant()) {
            this.context.getScope().defineConstant(node.name().getValue(),
                    node.value().accept(this.context.getInterpreter()));
        } else {
            this.context.getScope().setVariable(node.name().getValue(),
                    node.value().accept(this.context.getInterpreter()));
        }
        return null;
    }

    @Override
    public Value visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        if (this.context.getScope().hasVariable(node.name().getValue())) {
            throw new RuntimeException("Function '" + node.name() + "' is already defined in this scope.");
        }

        if (!this.context.isNativeDefined(node.name().getValue())) {
            throw new RuntimeException("Native function '" + node.name().getValue() + "' is not defined.");
        }

        FunctionValue functionValue = new FunctionValue(Optional.of(node.name().getValue()),
                node.parameters().stream().map(Token::getValue).toList(), null, null, true);
        this.context.getScope().setVariable(node.name().getValue(), functionValue);
        return null;
    }

}
