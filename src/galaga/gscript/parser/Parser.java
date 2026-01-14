package galaga.gscript.parser;

import java.util.List;

import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;

public final class Parser  {
    private final ParserContext context;

    public static Parser of(Lexer lexer) {
        return new Parser(ParserContext.of(lexer));
    }

    public static Parser of(List<Token> tokens) {
        return new Parser(ParserContext.of(tokens));
    }

    private Parser(ParserContext ctx) {
        this.context = ctx;
    }


    // private void blockStatement(List<Statement> parent) throws ParserError {
    //     if (this.is(Keyword.TYPE)) {
    //         this.typeDeclaration(parent);
    //     } else if (this.is(Keyword.RETURN)) {
    //         this.returnStatement(parent);
    //     } else if (this.is(Keyword.IF)) {
    //         this.ifStatement(parent);
    //     } else if (this.is(Keyword.WHILE) || this.is(Keyword.DO)) {
    //         this.whileStatement(parent, this.is(Keyword.DO));
    //     } else if (this.is(Keyword.FOR)) {
    //         this.forStatement(parent);
    //     } else if (this.is(Keyword.BREAK)) {
    //         this.breakStatement(parent);
    //     } else if (this.is(Keyword.CONTINUE)) {
    //         this.continueStatement(parent);
    //     } else if (this.is(TokenType.IDENTIFIER)) {
    //         if(this.nextIs(Operator.LEFT_PAREN)) {
    //             this.functionCallStatement(parent);
    //         }else{
    //             this.variableStatement(parent);
    //         }
    //     } else if (this.is(TokenType.EOF)) {
    //         return;
    //     } else {
    //         throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, statement expected");
    //     }
    // }

    // private void fileStatement() throws ParserError {
    //     if (this.is(Keyword.IMPORT)) {
    //         this.importStatement();
    //     } else if (this.is(Keyword.EXTERN)) {
    //         this.parseExtern();
    //     } else if (this.is(Keyword.TYPE)) {
    //         this.typeDeclaration(this.statements);
    //     } else if (this.is(TokenType.IDENTIFIER)) {
    //         this.functionDeclaration(this.statements, false);
    //     } else if (this.is(TokenType.EOF)) {
    //         return;
    //     } else {
    //         throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, statement expected");
    //     }
    // }

}
