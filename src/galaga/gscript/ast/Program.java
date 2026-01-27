package galaga.gscript.ast;

import java.util.List;

import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.lexer.token.TokenRange;

public record Program(List<Declaration> declarations, TokenRange range) implements ASTNode {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }

    @Override
    public TokenRange getRange() {
        return range;
    }

}