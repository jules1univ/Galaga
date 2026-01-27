package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record NativeFunctionDeclaration(Token name, List<Token> parameters, TokenRange range) implements Declaration {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNativeFunctionDeclaration(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}

