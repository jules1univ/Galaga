package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import galaga.gscript.lexer.Keyword;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.expression.Expression;
import galaga.gscript.parser.statement.BreakStatement;
import galaga.gscript.parser.statement.ContinueStatement;
import galaga.gscript.parser.statement.EnumDeclaration;
import galaga.gscript.parser.statement.ExternStatement;
import galaga.gscript.parser.statement.FunctionCallStatement;
import galaga.gscript.parser.statement.FunctionDeclaration;
import galaga.gscript.parser.statement.IfStatement;
import galaga.gscript.parser.statement.ImportStatement;
import galaga.gscript.parser.statement.ReturnStatement;
import galaga.gscript.parser.statement.Statement;
import galaga.gscript.parser.statement.StructDeclaration;
import galaga.gscript.parser.statement.WhileStatement;

public final class Parser implements Iterable<Statement> {
    private final List<Token> tokens;
    private final List<Statement> statements = new ArrayList<>();

    private int index = 0;
    private Token current;

    public static Parser of(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        lexer.iterator().forEachRemaining(tokens::add);
        return new Parser(tokens);
    }

    public static Parser of(List<Token> tokens) {
        return new Parser(tokens);
    }

    private Parser(List<Token> tokens) {
        this.tokens = tokens.stream()
                .filter(token -> token.getType() != TokenType.COMMENT)
                .collect(Collectors.toList());
    }

    /*
     * import -> import <import_patern>
     * <import_patern> -> ID . <import_patern> | ID | { <import_list> }
     * <import_list> -> <import_item> , <import_list> | <import_item>
     * <import_item> -> ID | ID . <import_item>
     * 
     * extern -> extern type ID | extern <function_declaration>
     * 
     * <function_declaration> -> ID ID ( <parameter_list> ) { <function_body> }
     * <parameter_list> -> <parameter> , <parameter_list> | <parameter>
     * <parameter> -> [const] [ref] ID ID
     * <function_body> -> <statements>
     * 
     * <statements> -> <statement>; <statements> | <statement>
     * <statement> -> <return_statement> | <if_statement> | <while_statement> |
     * <for_statement> | <break_statement> | <continue_statement> |
     * <expression_statement>
     * 
     * <return_statement> -> return <expression>
     * <if_statement> -> if ( <expression> ) { <statements> } [<else_statement>]
     * <else_statement> -> else { <statements> } | else if ( <expression> ) {
     * <statements> } [<else_statement>]
     * 
     * <while_statement> -> while ( <expression> ) { <statements> } | do {
     * <statements> } while ( <expression> );
     * <for_statement> -> for ( <expression_statement>; <expression_statement>;
     * <expression> ) { <statements> }
     * <break_statement> -> break
     * <continue_statement> -> continue
     * <expression_statement> -> <expression>
     * 
     * <expression> -> <variables> | <function_call> | <literals> |
     * <binary_expression> | <unary_expression> | ( <expression> )
     * <variables> -> <variable_declaration> | <variable_access>
     * <variable_declaration> -> [const] [ref] ID ID [= <expression>]
     * <variable_access> -> ID [-> ID] | ID [. ID]
     * 
     * <function_call> -> ID ( <argument_list> )
     * <argument_list> -> <expression> , <argument_list> | <expression>
     * 
     * <literals> -> NUMBER | STRING | TRUE | FALSE
     * <binary_expression> -> <expression> OPERATOR <expression>
     * <unary_expression> -> OPERATOR <expression>
     * 
     */
    private boolean isEnd() {
        return this.index >= this.tokens.size();
    }

    private void advance() {
        this.index++;
        if (!this.isEnd()) {
            this.current = this.tokens.get(this.index);
        }
    }

    private void expect(TokenType type) throws ParserError {
        if (this.current.getType() != type) {
            throw new ParserError(this.current, type, "Unexpected token");
        }
        this.advance();
    }

    private void expect(TokenType type, String value) throws ParserError {
        if (this.current.getType() != type || !this.current.getValue().equals(value)) {
            throw new ParserError(this.current, type, value, "Unexpected token");
        }
        this.advance();
    }

    private void expect(Keyword keyword) throws ParserError {
        this.expect(TokenType.KEYWORD, keyword.getText());
    }

    private void expect(Operator operator) throws ParserError {
        this.expect(TokenType.OPERATOR, operator.getText());
    }

    private boolean is(TokenType type, String value) {
        return this.current.getType() == type && this.current.getValue().equals(value);
    }

    private boolean is(TokenType type) {
        return this.current.getType() == type;
    }

    private boolean is(Keyword keyword) {
        return this.is(TokenType.KEYWORD, keyword.getText());
    }

    private boolean is(Operator operator) {
        return this.is(TokenType.OPERATOR, operator.getText());
    }

    private boolean nextIs(Operator operator) {
        if (this.index + 1 >= this.tokens.size()) {
            return false;
        }
        Token next = this.tokens.get(this.index + 1);
        return next.getType() == TokenType.OPERATOR && next.getValue().equals(operator.getText());
    }

    private void removeSemicolon() {
        if (this.is(Operator.SEMICOLON)) {
            this.advance();
        }
    }

    private void importStatement() throws ParserError {
        this.expect(Keyword.IMPORT);
        List<String> paths = new ArrayList<>();
        List<String> functions = new ArrayList<>();
        boolean isWildcard = false;

        boolean functionMode = false;

        while (!this.isEnd() && this.is(TokenType.IDENTIFIER)) {
            paths.add(this.current.getValue());
            this.advance();
            if (this.is(Operator.DOT)) {
                this.advance();
                continue;
            } else if (this.is(Operator.LEFT_BRACE)) {
                this.advance();
                functionMode = true;
                break;
            } else if (this.is(Operator.MULTIPLY)) {
                isWildcard = true;
                this.advance();
                break;
            }
        }

        while (functionMode && !this.isEnd() && this.is(TokenType.IDENTIFIER)) {
            functions.add(this.current.getValue());
            this.advance();
            if (this.is(Operator.COMMA)) {
                this.advance();
                continue;
            } else if (this.is(Operator.RIGHT_BRACE)) {
                this.advance();
                this.removeSemicolon();
                break;
            }
        }

        this.statements.add(new ImportStatement(paths, functions, isWildcard));
    }

    private void externStatement() throws ParserError {
        this.expect(Keyword.EXTERN);
        if (this.is(Keyword.TYPE)) {
            this.advance();
            TypeSignature typeName = this.typeSignature();
            this.removeSemicolon();
            this.statements.add(new ExternStatement(typeName));
        } else {
            this.statements.add(new ExternStatement(this.functionDeclaration(null, true)));
        }
    }

    private FunctionSignature functionDeclaration(List<Statement> parent, boolean signature) throws ParserError {
        String returnType = this.current.getValue();
        this.expect(TokenType.IDENTIFIER);

        String functionName = this.current.getValue();
        this.expect(TokenType.IDENTIFIER);
        this.expect(Operator.LEFT_PAREN);

        Map<TypeSignature, String> parameters = Map.of();
        boolean hasNext = false;
        while (!this.isEnd() && !this.is(Operator.RIGHT_PAREN)) {
            hasNext = false;

            TypeSignature paramType = this.typeSignature();
            String paramName = this.current.getValue();
            this.expect(TokenType.IDENTIFIER);
            parameters.put(paramType, paramName);
            if (this.is(Operator.COMMA)) {
                this.advance();
                hasNext = true;
            }
        }

        if (hasNext) {
            throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, parameter expected");
        }

        this.expect(Operator.RIGHT_PAREN);
        this.removeSemicolon();
        if (signature) {
            return new FunctionSignature(returnType, functionName, parameters);
        }

        List<Statement> body = new ArrayList<>();
        this.expect(Operator.LEFT_BRACE);
        while (!this.isEnd() && !this.is(Operator.RIGHT_BRACE)) {
            this.blockStatement(body);
        }
        this.expect(Operator.RIGHT_BRACE);
        parent.add(new FunctionDeclaration(new FunctionSignature(returnType, functionName, parameters), body));
        return null;
    }

    private void functionCallStatement(List<Statement> parent) throws ParserError {
        String functionName = this.current.getValue();
        this.expect(TokenType.IDENTIFIER);
        this.expect(Operator.LEFT_PAREN);

        List<Expression> arguments = new ArrayList<>();
        boolean hasNext = false;
        while (!this.isEnd() && !this.is(Operator.RIGHT_PAREN)) {
            hasNext = false;

            Expression arg = this.expression();
            arguments.add(arg);
            if (this.is(Operator.COMMA)) {
                this.advance();
                hasNext = true;
            }
        }

        if (hasNext) {
            throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, argument expected");
        }

        this.expect(Operator.RIGHT_PAREN);
        this.removeSemicolon();

        parent.add(new FunctionCallStatement(functionName, arguments));
    }

    private void variableStatement(List<Statement> parent) throws ParserError {
    
        
    }

    private TypeSignature typeSignature() throws ParserError {
        boolean isConst = false;
        boolean isRef = false;
        boolean isArray = false;

        if (this.is(Keyword.CONST)) {
            isConst = true;
            this.advance();
        }

        if (this.is(Keyword.REF)) {
            isRef = true;
            this.advance();
        }

        String typeName = this.current.getValue();
        this.expect(TokenType.IDENTIFIER);

        if (this.is(Operator.LEFT_BRACKET)) {
            this.advance();
            this.expect(Operator.RIGHT_BRACKET);
            isArray = true;
        }

        return new TypeSignature(typeName, isConst, isRef, isArray);
    }

    private void typeDeclaration(List<Statement> parent) throws ParserError {
        this.expect(Keyword.TYPE);
        String typeName = this.current.getValue();
        this.expect(TokenType.IDENTIFIER);
        this.expect(Operator.ASSIGN);

        if (this.is(Keyword.STRUCT)) {
            this.structDeclaration(typeName, parent);
        } else if (this.is(Keyword.ENUM)) {
            this.enumDeclaration(typeName, parent);
        } else {
            throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, struct or enum expected");
        }
    }

    private void structDeclaration(String name, List<Statement> parent) throws ParserError {
        this.expect(Keyword.STRUCT);
        this.expect(Operator.LEFT_BRACE);
        Map<TypeSignature, String> fields = Map.of();

        while (!this.isEnd() && !this.is(Operator.RIGHT_BRACE)) {
            TypeSignature fieldType = this.typeSignature();

            String fieldName = this.current.getValue();
            this.expect(TokenType.IDENTIFIER);
            fields.put(fieldType, fieldName);
            this.removeSemicolon();
        }

        this.expect(Operator.RIGHT_BRACE);
        this.removeSemicolon();

        parent.add(new StructDeclaration(name, fields));
    }

    private void enumDeclaration(String name, List<Statement> parent) throws ParserError {
        this.expect(Keyword.ENUM);
        this.expect(Operator.LEFT_BRACE);
        Map<String, Optional<Integer>> values = Map.of();
        while (!this.isEnd() && !this.is(Operator.RIGHT_BRACE)) {
            String valueName = this.current.getValue();
            this.expect(TokenType.IDENTIFIER);
            Optional<Integer> valueNumber = Optional.empty();
            if (this.is(Operator.ASSIGN)) {
                this.advance();
                String numberStr = this.current.getValue();
                this.expect(TokenType.NUMBER);
                try {
                    valueNumber = Optional.of(Integer.parseInt(numberStr));
                } catch (NumberFormatException e) {
                    throw new ParserError(this.current, TokenType.NUMBER, "Invalid number format");
                }
            }
            values.put(valueName, valueNumber);
            if (this.is(Operator.COMMA)) {
                this.advance();
            }
        }
        this.expect(Operator.RIGHT_BRACE);
        this.removeSemicolon();

        parent.add(new EnumDeclaration(name, values));
    }

    private void returnStatement(List<Statement> parent) throws ParserError {
        this.expect(Keyword.RETURN);
        Expression expr = this.expression();
        this.removeSemicolon();
        parent.add(new ReturnStatement(expr));
    }

    private void ifStatement(List<Statement> parent) throws ParserError {
        this.expect(Keyword.IF);
        this.expect(Operator.LEFT_PAREN);

        Expression condition = this.expression();
        this.expect(Operator.RIGHT_PAREN);
        this.expect(Operator.LEFT_BRACE);

        Map<Expression, List<Statement>> branches = Map.of();

        branches.put(condition, new ArrayList<>());
        while (!this.isEnd() && !this.is(Operator.RIGHT_BRACE)) {
            this.blockStatement(branches.get(condition));
        }
        this.expect(Operator.RIGHT_BRACE);

        if (this.is(Keyword.IF)) {
            // else if
        } else if (this.is(Keyword.ELSE)) {
            // else
        }

        parent.add(new IfStatement(branches));
    }

    private void whileStatement(List<Statement> parent, boolean doWhile) throws ParserError {
        Expression condition = null;
        if (doWhile) {
            this.expect(Keyword.DO);
        } else {
            this.expect(Keyword.WHILE);
            this.expect(Operator.LEFT_PAREN);
            condition = this.expression();
            this.expect(Operator.RIGHT_PAREN);
        }

        this.expect(Operator.LEFT_BRACE);
        List<Statement> body = new ArrayList<>();
        while (!this.isEnd() && !this.is(Operator.RIGHT_BRACE)) {
            this.blockStatement(body);
        }
        this.expect(Operator.RIGHT_BRACE);
        if (doWhile) {
            this.expect(Keyword.WHILE);
            this.expect(Operator.LEFT_PAREN);
            condition = this.expression();
            this.expect(Operator.RIGHT_PAREN);
            this.removeSemicolon();
        }

        parent.add(new WhileStatement(condition, body, doWhile));
    }

    private void forStatement(List<Statement> parent) throws ParserError {
        this.expect(Keyword.FOR);
        // TODO: for(cond1; cond2; cond3) { ... }
        // TODO: for(type ID in <expression>) { ... }
    }

    private void breakStatement(List<Statement> parent) throws ParserError {
        this.expect(Keyword.BREAK);
        this.removeSemicolon();
        parent.add(new BreakStatement());
    }

    private void continueStatement(List<Statement> parent) throws ParserError {
        this.expect(Keyword.CONTINUE);
        this.removeSemicolon();
        parent.add(new ContinueStatement());
    }

    private void blockStatement(List<Statement> parent) throws ParserError {
        if (this.is(Keyword.TYPE)) {
            this.typeDeclaration(parent);
        } else if (this.is(Keyword.RETURN)) {
            this.returnStatement(parent);
        } else if (this.is(Keyword.IF)) {
            this.ifStatement(parent);
        } else if (this.is(Keyword.WHILE) || this.is(Keyword.DO)) {
            this.whileStatement(parent, this.is(Keyword.DO));
        } else if (this.is(Keyword.FOR)) {
            this.forStatement(parent);
        } else if (this.is(Keyword.BREAK)) {
            this.breakStatement(parent);
        } else if (this.is(Keyword.CONTINUE)) {
            this.continueStatement(parent);
        } else if (this.is(TokenType.IDENTIFIER)) {
            if(this.nextIs(Operator.LEFT_PAREN)) {
                this.functionCallStatement(parent);
            }else{
                this.variableStatement(parent);
            }
        } else if (this.is(TokenType.EOF)) {
            return;
        } else {
            throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, statement expected");
        }
    }

    private void fileStatement() throws ParserError {
        if (this.is(Keyword.IMPORT)) {
            this.importStatement();
        } else if (this.is(Keyword.EXTERN)) {
            this.externStatement();
        } else if (this.is(Keyword.TYPE)) {
            this.typeDeclaration(this.statements);
        } else if (this.is(TokenType.IDENTIFIER)) {
            this.functionDeclaration(this.statements, false);
        } else if (this.is(TokenType.EOF)) {
            return;
        } else {
            throw new ParserError(this.current, TokenType.IDENTIFIER, "Unexpected token, statement expected");
        }
    }

    private Expression expression() throws ParserError {
        return null;
    }

    public void execute() throws ParserError {
        while (!this.isEnd()) {
            this.current = this.tokens.get(this.index);
            this.fileStatement();
            this.index++;
        }
    }

    @Override
    public Iterator<Statement> iterator() {
        return this.statements.iterator();
    }
}
