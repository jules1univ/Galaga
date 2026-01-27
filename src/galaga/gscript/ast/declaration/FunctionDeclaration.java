package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record FunctionDeclaration(Token name, List<Token> parameters, BlockStatement body, TokenRange range)
        implements Declaration {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }

    @Override
    public TokenRange getRange() {
        return range;
    }
}
