package galaga.gscript.ast.declaration;

public interface DeclarationVisitor<T> {
    T visitFunctionDeclaration(FunctionDeclaration node);

    T visitVariableDeclaration(VariableDeclaration node);

    T visitNativeFunctionDeclaration(NativeFunctionDeclaration node);
}
