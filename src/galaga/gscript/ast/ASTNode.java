package galaga.gscript.ast;

import galaga.gscript.lexer.token.TokenRange;

public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);

    TokenRange getRange();
}