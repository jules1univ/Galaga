package galaga.gscript.interpreter.subinterpreter;

import java.util.Optional;

import galaga.gscript.ast.declaration.DeclarationVisitor;
import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.interpreter.InterpreterContext;
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
        if (this.context.getScope().hasVariable(node.name())) {
            throw new RuntimeException("Function '" + node.name() + "' is already defined in this scope.");
        }

        FunctionValue functionValue = new FunctionValue(Optional.of(node.name()), node.parameters(), node.body(), this.context.getScope(), false);
        this.context.getScope().setVariable(node.name(), functionValue);
        return null;
    }

    @Override
    public Value visitVariableDeclaration(VariableDeclaration node) {
        if (this.context.getScope().hasVariable(node.name()) || this.context.getScope().hasConstant(node.name())) {
            throw new RuntimeException("Object '" + node.name() + "' is already defined in this scope.");
        }
        if (node.isConstant()) {
            this.context.getScope().defineConstant(node.name(), node.value().accept(this.context.getInterpreter()));
        } else {
            this.context.getScope().setVariable(node.name(), node.value().accept(this.context.getInterpreter()));
        }
        return null;
    }

    @Override
    public Value visitNativeFunctionDeclaration(NativeFunctionDeclaration node) {
        if (this.context.getScope().hasVariable(node.name())) {
            throw new RuntimeException("Function '" + node.name() + "' is already defined in this scope.");
        }

        if (!this.context.isNativeDefined(node.name())) {
            throw new RuntimeException("Native function '" + node.name() + "' is not defined.");
        }

        FunctionValue functionValue = new FunctionValue(Optional.of(node.name()), node.parameters(), null, null, true);
        this.context.getScope().setVariable(node.name(), functionValue);
        return null;
    }

}
