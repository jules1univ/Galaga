package galaga.gscript.ast;

import java.util.List;

import galaga.gscript.ast.declaration.Declaration;

public record Program(List<Declaration> declarations) implements ASTNode {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}