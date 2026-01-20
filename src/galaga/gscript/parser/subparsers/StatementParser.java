package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.expression.VariableExpression;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.StatementBase;
import galaga.gscript.ast.statement.StructStatement;
import galaga.gscript.ast.statement.VariableStatement;
import galaga.gscript.ast.statement.AssignStatement;
import galaga.gscript.ast.statement.logic.BreakStatement;
import galaga.gscript.ast.statement.logic.ContinueStatement;
import galaga.gscript.ast.statement.logic.ForStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.ReturnStatement;
import galaga.gscript.ast.statement.logic.SwitchStatement;
import galaga.gscript.ast.statement.logic.WhileStatement;
import galaga.gscript.ast.types.Type;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;
import galaga.gscript.parser.ParserException;

public final class StatementParser {
    public static boolean isBlock(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static Block parseBlock(ParserContext context) throws ParserException {
        context.expect(Operator.LEFT_BRACE);

        List<StatementBase> statements = new ArrayList<>();
        while (context.isEnd() == false && !context.is(Operator.RIGHT_BRACE)) {
            if (isIfStatement(context)) {
                statements.add(parseIfStatement(context));
            } else if (isWhileStatement(context)) {
                statements.add(parseWhileStatement(context));
            } else if (isForStatement(context)) {
                statements.add(parseForStatement(context));
            } else if (isSwitchStatement(context)) {
                statements.add(parseSwitchStatement(context));
            } else if (isReturnStatement(context)) {
                statements.add(parseReturnStatement(context));
            } else if (isBreakStatement(context)) {
                statements.add(parseBreakStatement(context));
            } else if (isContinueStatement(context)) {
                statements.add(parseContinueStatement(context));
            } else if (TypeParser.isType(context)) {
                Type typeName = TypeParser.parseType(context);
                if (isStructStatement(context)) {
                    statements.add(parseStructStatement(context, typeName));
                } else if (isVariableStatement(context)) {
                    statements.add(parseVariableStatement(context, typeName));
                } else {
                    throw new ParserException(context, "Unexpected token after type declaration.");
                }

            } else if (isAssignStatement(context)) {
                statements.add(parseAssignStatement(context));
            } else {
                statements.add(parseExpressionStatement(context));
            }
        }
        context.expect(Operator.RIGHT_BRACE);
        return new Block(statements);
    }

    public static boolean isIfStatement(ParserContext context) {
        return context.is(Keyword.IF);
    }

    public static IfStatement parseIfStatement(ParserContext context) throws ParserException {
        context.expect(Keyword.IF);
        context.expect(Operator.LEFT_PAREN);

        ExpressionBase condition = ExpressionParser.parseExpression(context);
        context.expect(Operator.RIGHT_PAREN);

        Block block = parseBlock(context);
        Map<ExpressionBase, Block> conditions = Map.of(condition, block);

        while (context.isAndAdvance(Keyword.ELSE)) {
            if (context.isAndAdvance(Keyword.IF)) {

                context.expect(Operator.LEFT_PAREN);
                ExpressionBase elseIfCondition = ExpressionParser.parseExpression(context);
                context.expect(Operator.RIGHT_PAREN);

                conditions.put(elseIfCondition, parseBlock(context));
            } else {
                Block elseBlock = parseBlock(context);
                return new IfStatement(conditions, Optional.of(elseBlock));
            }
        }

        return new IfStatement(conditions, Optional.empty());
    }

    public static boolean isWhileStatement(ParserContext context) {
        return context.is(Keyword.WHILE) || context.is(Keyword.DO);
    }

    public static WhileStatement parseWhileStatement(ParserContext context) throws ParserException {
        boolean isDoWhile = false;
        if (context.is(Keyword.DO)) {
            isDoWhile = true;
            context.advance();
        } else if (context.is(Keyword.WHILE)) {
            context.advance();
        } else {
            throw new ParserException(context, "Expected 'while' or 'do' keyword.");
        }

        if (isDoWhile == false) {
            context.expect(Operator.LEFT_PAREN);
        }
        ExpressionBase condition = ExpressionParser.parseExpression(context);
        if (isDoWhile == false) {
            context.expect(Operator.RIGHT_PAREN);
        }
        Block body = parseBlock(context);
        if (isDoWhile) {
            context.expect(Keyword.WHILE);
            context.expect(Operator.LEFT_PAREN);
            condition = ExpressionParser.parseExpression(context);
            context.expect(Operator.RIGHT_PAREN);
            context.advanceIfSemicolon();
        }

        return new WhileStatement(condition, body, isDoWhile);
    }

    public static boolean isSwitchStatement(ParserContext context) {
        return context.is(Keyword.SWITCH);
    }

    public static SwitchStatement parseSwitchStatement(ParserContext context) throws ParserException {

        context.expect(Keyword.SWITCH);
        context.expect(Operator.LEFT_PAREN);

        Map<ExpressionBase, StatementBase> cases = Map.of();
        while (context.isAndAdvance(Keyword.CASE)) {
            ExpressionBase caseExpr = ExpressionParser.parseExpression(context);
            context.expect(Operator.ASSIGN);
            context.expect(Operator.GREATER_THAN);

            StatementBase caseBlock = parseBlock(context);
            context.advanceIfSemicolon();
            cases.put(caseExpr, caseBlock);
        }

        Optional<StatementBase> defaultCase = Optional.empty();
        if (context.isAndAdvance(Keyword.DEFAULT)) {
            context.expect(Operator.ASSIGN);
            context.expect(Operator.GREATER_THAN);
            defaultCase = Optional.of(parseBlock(context));
            context.advanceIfSemicolon();
        }

        context.expect(Operator.RIGHT_PAREN);
        return new SwitchStatement(cases, defaultCase);
    }

    public static boolean isForStatement(ParserContext context) {
        return context.is(Keyword.FOR);
    }

    public static ForStatement parseForStatement(ParserContext context) throws ParserException {
        context.expect(Keyword.FOR);
        context.expect(Operator.LEFT_PAREN);

        VariableStatement init = parseVariableStatement(context, TypeParser.parseType(context));
        if (!context.backIs(Operator.SEMICOLON)) {
            context.expect(Operator.SEMICOLON);
        }
        ExpressionBase check = ExpressionParser.parseExpression(context);
        context.expect(Operator.SEMICOLON);
        ExpressionBase action = ExpressionParser.parseExpression(context);
        context.expect(Operator.RIGHT_PAREN);
        return new ForStatement(init, check, action, parseBlock(context));
    }

    public static boolean isBreakStatement(ParserContext context) {
        return context.is(Keyword.BREAK);
    }

    public static BreakStatement parseBreakStatement(ParserContext context) throws ParserException {
        context.expect(Keyword.BREAK);
        context.advanceIfSemicolon();
        return new BreakStatement();
    }

    public static boolean isContinueStatement(ParserContext context) {
        return context.is(Keyword.CONTINUE);
    }

    public static ContinueStatement parseContinueStatement(ParserContext context) throws ParserException {
        context.expect(Keyword.CONTINUE);
        context.advanceIfSemicolon();
        return new ContinueStatement();
    }

    public static boolean isReturnStatement(ParserContext context) {
        return context.is(Keyword.RETURN);
    }

    public static ReturnStatement parseReturnStatement(ParserContext context) throws ParserException {
        context.expect(Keyword.RETURN);

        if (context.isAndAdvance(Operator.SEMICOLON)) {
            return new ReturnStatement(Optional.empty());
        }

        ExpressionBase returnValue = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new ReturnStatement(Optional.of(returnValue));
    }

    public static boolean isStructStatement(ParserContext context) {
        return context.nextIs(Operator.LEFT_BRACE);
    }

    public static StructStatement parseStructStatement(ParserContext context, Type type) throws ParserException {
        String name = context.getValue();
        context.expect(TokenType.IDENTIFIER);
        context.expect(Operator.LEFT_BRACE);

        Map<String, ExpressionBase> fields = new java.util.HashMap<>();

        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            String fieldName = context.getValue();
            context.expect(TokenType.IDENTIFIER);
            context.expect(Operator.ASSIGN);

            ExpressionBase fieldValue = ExpressionParser.parseExpression(context);
            fields.put(fieldName, fieldValue);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        context.expect(Operator.RIGHT_BRACE);
        context.advanceIfSemicolon();
        return new StructStatement(type, name, fields);
    }

    public static boolean isVariableStatement(ParserContext context) {
       return context.is(TokenType.IDENTIFIER) && context.nextIs(Operator.ASSIGN);
    }

    public static VariableStatement parseVariableStatement(ParserContext context, Type type)
            throws ParserException {

        String name = context.getValueExpect(TokenType.IDENTIFIER);
        context.expect(Operator.ASSIGN);
        ExpressionBase value = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new VariableStatement(type, name, value);
    }

    public static boolean isAssignStatement(ParserContext context) {
        return context.is(TokenType.IDENTIFIER) && (context.nextIs(Operator.DOT) ||
                context.nextIs(Operator.ASSIGN) ||
                context.nextIs(Operator.ASSIGN_PLUS) ||
                context.nextIs(Operator.ASSIGN_MINUS) ||
                context.nextIs(Operator.ASSIGN_MULTIPLY) ||
                context.nextIs(Operator.ASSIGN_DIVIDE) ||
                context.nextIs(Operator.ASSIGN_MODULO));
    }

    public static AssignStatement parseAssignStatement(ParserContext context) throws ParserException {
        String name = context.getValueExpect(TokenType.IDENTIFIER);
        List<ExpressionBase> members = new ArrayList<>();
        while (context.isAndAdvance(Operator.DOT)) {
            if (context.is(TokenType.IDENTIFIER) && context.nextIs(Operator.LEFT_PAREN)) {
                members.add(ExpressionParser.parseFunctionCallExpression(context));
                continue;
            }
            String memberName = context.getValueExpect(TokenType.IDENTIFIER);
            members.add(new VariableExpression(memberName, new ArrayList<>()));
        }

        Operator op = context.getOperatorExpect();
        ExpressionBase value = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new AssignStatement(name, members, op, value);
    }

    public static ExpressionStatement parseExpressionStatement(ParserContext context) throws ParserException {
        ExpressionBase expr = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new ExpressionStatement(expr);
    }
}
