package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.statement.logic.BreakStatement;
import galaga.gscript.ast.statement.logic.ForStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.ReturnStatement;
import galaga.gscript.ast.statement.logic.SwitchStatement;
import galaga.gscript.ast.statement.logic.WhileStatement;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.parser.ParserContext;

public final class StatementParser {
    public static boolean isBlock(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static Optional<Block> parseBlock(ParserContext context) {
        if (!context.expect(Operator.LEFT_BRACE)) {
            return Optional.empty();
        }

        List<Statement> statements = new ArrayList<>();
        while (context.isEnd() == false && !context.is(Operator.RIGHT_BRACE)) {
            if (isIfStatement(context)) {
                Optional<IfStatement> ifStmt = parseIfStatement(context);
                if (ifStmt.isPresent()) {
                    statements.add(ifStmt.get());
                    continue;
                }
            } else if (isWhileStatement(context)) {
                Optional<WhileStatement> whileStmt = parseWhileStatement(context);
                if (whileStmt.isPresent()) {
                    statements.add(whileStmt.get());
                    continue;
                }
            } else if (isForStatement(context)) {
                Optional<ForStatement> forStmt = parseForStatement(context);
                if (forStmt.isPresent()) {
                    statements.add(forStmt.get());
                    continue;
                }
            } else if (isReturnStatement(context)) {
                Optional<ReturnStatement> returnStmt = parseReturnStatement(context);
                if (returnStmt.isPresent()) {
                    statements.add(returnStmt.get());
                    continue;
                }
            } else if (isBreakStatement(context)) {
                Optional<BreakStatement> breakStmt = parseBreakStatement(context);
                if (breakStmt.isPresent()) {
                    statements.add(breakStmt.get());
                    continue;
                }
            } else {
                Optional<ExpressionStatement> expr = parseExpressionStatement(context);
                if (expr.isPresent()) {
                    statements.add(expr.get());
                    context.advanceIfSemicolon();
                    continue;
                }
            }
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            return Optional.empty();
        }
        return Optional.of(new Block(statements));
    }

    public static boolean isIfStatement(ParserContext context) {
        return context.is(Keyword.IF);
    }

    public static Optional<IfStatement> parseIfStatement(ParserContext context) {
        if (!context.expect(Keyword.IF)) {
            return Optional.empty();
        }

        if (!context.expect(Operator.LEFT_PAREN)) {
            return Optional.empty();
        }

        Optional<Expression> condition = Optional.empty();// ExpressionParser.parseExpression(context);
        if (condition.isEmpty()) {
            return Optional.empty();
        }

        if (!context.expect(Operator.RIGHT_PAREN)) {
            return Optional.empty();
        }

        Optional<Block> block = parseBlock(context);
        if (block.isEmpty()) {
            return Optional.empty();
        }

        Map<Expression, Block> conditions = Map.of(condition.get(), block.get());

        while (context.isAndAdvance(Keyword.ELSE)) {
            if (context.is(Keyword.IF)) {
                context.advance();

                if (!context.expect(Operator.LEFT_PAREN)) {
                    return Optional.empty();
                }

                Optional<Expression> elseIfCondition = Optional.empty();// ExpressionParser.parseExpression(context);
                if (elseIfCondition.isEmpty()) {
                    return Optional.empty();
                }

                if (!context.expect(Operator.RIGHT_PAREN)) {
                    return Optional.empty();
                }

                Optional<Block> elseIfBlock = parseBlock(context);
                if (elseIfBlock.isEmpty()) {
                    return Optional.empty();
                }

                conditions.put(elseIfCondition.get(), elseIfBlock.get());
            } else {
                Optional<Block> elseBlock = parseBlock(context);
                if (elseBlock.isEmpty()) {
                    return Optional.empty();
                }

                return Optional.of(new IfStatement(conditions, elseBlock));
            }
        }

        return Optional.of(new IfStatement(conditions, Optional.empty()));
    }

    public static boolean isWhileStatement(ParserContext context) {
        return context.is(Keyword.WHILE) || context.is(Keyword.DO);
    }

    public static Optional<WhileStatement> parseWhileStatement(ParserContext context) {
        boolean isDoWhile = false;
        if (context.is(Keyword.DO)) {
            isDoWhile = true;
            context.advance();
        } else if (context.is(Keyword.WHILE)) {
            context.advance();
        } else {
            return Optional.empty();
        }

        if (isDoWhile == false) {
            if (!context.expect(Operator.LEFT_PAREN)) {
                return Optional.empty();
            }
        }
        Optional<Expression> condition = Optional.empty();// ExpressionParser.parseExpression(context);
        if (condition.isEmpty()) {
            return Optional.empty();
        }
        if (isDoWhile == false) {
            if (!context.expect(Operator.RIGHT_PAREN)) {
                return Optional.empty();
            }
        }
        Optional<Block> body = parseBlock(context);
        if (body.isEmpty()) {
            return Optional.empty();
        }
        if (isDoWhile) {
            if (!context.expect(Keyword.WHILE)) {
                return Optional.empty();
            }
            if (!context.expect(Operator.LEFT_PAREN)) {
                return Optional.empty();
            }
            condition = Optional.empty();// ExpressionParser.parseExpression(context);
            if (condition.isEmpty()) {
                return Optional.empty();
            }
            if (!context.expect(Operator.RIGHT_PAREN)) {
                return Optional.empty();
            }
            context.advanceIfSemicolon();
        }

        return Optional.of(new WhileStatement(condition.get(), body.get(), isDoWhile));
    }

    public static boolean isSwitchStatement(ParserContext context) {
        return context.is(Keyword.SWITCH);
    }

    public static Optional<SwitchStatement> parseSwitchStatement(ParserContext context) {
        if (!context.expect(Keyword.SWITCH)) {
            return Optional.empty();
        }
        if (!context.expect(Operator.LEFT_PAREN)) {
            return Optional.empty();
        }
        Map<Expression, Block> cases = Map.of();
        while (context.isAndAdvance(Keyword.CASE)) {
            Optional<Expression> caseExpr = Optional.empty();// ExpressionParser.parseExpression(context);
            if (caseExpr.isEmpty()) {
                return Optional.empty();
            }
            if (!context.expect(Operator.ASSIGN) || !context.expect(Operator.GREATER_THAN)) {
                return Optional.empty();
            }
            Optional<Block> caseBlock = parseBlock(context);
            if (caseBlock.isEmpty()) {
                return Optional.empty();
            }
            context.advanceIfSemicolon();
            cases.put(caseExpr.get(), caseBlock.get());
        }

        return Optional.of(new SwitchStatement(cases));
    }

    public static boolean isForStatement(ParserContext context) {
        return context.is(Keyword.FOR);
    }

    public static Optional<ForStatement> parseForStatement(ParserContext context) {
        if (!context.expect(Keyword.FOR) || !context.expect(Operator.LEFT_PAREN)) {
            return Optional.empty();
        }

        List<Expression> conditions = new ArrayList<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            Optional<Expression> expr = Optional.empty();// ExpressionParser.parseExpression(context);
            if (expr.isEmpty()) {
                return Optional.empty();
            }
            conditions.add(expr.get());
            if (!context.isAndAdvance(Operator.SEMICOLON)) {
                break;
            }
        }

        if (!context.expect(Operator.RIGHT_PAREN)) {
            return Optional.empty();
        }
        Optional<Block> body = parseBlock(context);
        if (body.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ForStatement(conditions, body.get()));
    }

    public static boolean isBreakStatement(ParserContext context) {
        return context.is(Keyword.BREAK);
    }

    public static Optional<BreakStatement> parseBreakStatement(ParserContext context) {
        if (!context.expect(Keyword.BREAK)) {
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new BreakStatement());
    }

    public static boolean isContinueStatement(ParserContext context) {
        return context.is(Keyword.CONTINUE);
    }

    public static Optional<BreakStatement> parseContinueStatement(ParserContext context) {
        if (!context.expect(Keyword.CONTINUE)) {
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new BreakStatement());
    }

    public static boolean isReturnStatement(ParserContext context) {
        return context.is(Keyword.RETURN);
    }

    public static Optional<ReturnStatement> parseReturnStatement(ParserContext context) {
        if (!context.expect(Keyword.RETURN)) {
            return Optional.empty();
        }
        Optional<Expression> returnValue = Optional.empty();// ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return Optional.of(new ReturnStatement(returnValue));
    }

    public static Optional<ExpressionStatement> parseExpressionStatement(ParserContext context) {
        Optional<Expression> expr = ExpressionParser.parseExpression(context);
        if (expr.isEmpty()) {
            return Optional.empty();
        }
        context.advanceIfSemicolon();
        return Optional.of(new ExpressionStatement(expr.get()));
    }
}
