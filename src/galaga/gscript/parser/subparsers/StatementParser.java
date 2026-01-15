package galaga.gscript.parser.subparsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.StatementBase;
import galaga.gscript.ast.statement.StatementError;
import galaga.gscript.ast.statement.StructInitStatement;
import galaga.gscript.ast.statement.logic.BreakStatement;
import galaga.gscript.ast.statement.logic.ContinueStatement;
import galaga.gscript.ast.statement.logic.ForStatement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.ReturnStatement;
import galaga.gscript.ast.statement.logic.SwitchStatement;
import galaga.gscript.ast.statement.logic.WhileStatement;
import galaga.gscript.ast.types.TypeBase;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.ParserContext;

public final class StatementParser {
    public static boolean isBlock(ParserContext context) {
        return context.is(Operator.LEFT_BRACE);
    }

    public static StatementBase parseBlock(ParserContext context) {
        if (!context.expect(Operator.LEFT_BRACE)) {
            return (StatementBase) context.getLastError();
        }

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
                TypeBase typeName = TypeParser.parseType(context);
                if (isStructInitStatement(context)) {
                    statements.add(parseStructInitStatement(context, typeName));
                } else if (isVariableInitStatement(context)) {
                    statements.add(parseExpressionStatement(context));
                } else {
                    statements.add(new StatementError("Invalid statement after type declaration."));
                }
            } else {
                statements.add(parseExpressionStatement(context));
            }
        }

        if (!context.expect(Operator.RIGHT_BRACE)) {
            return (StatementBase) context.getLastError();
        }
        return new Block(statements);
    }

    public static boolean isIfStatement(ParserContext context) {
        return context.is(Keyword.IF);
    }

    public static StatementBase parseIfStatement(ParserContext context) {
        if (!context.expect(Keyword.IF)) {
            return (StatementBase) context.getLastError();
        }

        if (!context.expect(Operator.LEFT_PAREN)) {
            return (StatementBase) context.getLastError();
        }

        ExpressionBase condition = ExpressionParser.parseExpression(context);
        if (!context.expect(Operator.RIGHT_PAREN)) {
            return (StatementBase) context.getLastError();
        }

        StatementBase block = parseBlock(context);
        Map<ExpressionBase, StatementBase> conditions = Map.of(condition, block);

        while (context.isAndAdvance(Keyword.ELSE)) {
            if (context.is(Keyword.IF)) {
                context.advance();

                if (!context.expect(Operator.LEFT_PAREN)) {
                    return (StatementBase) context.getLastError();
                }

                ExpressionBase elseIfCondition = ExpressionParser.parseExpression(context);

                if (!context.expect(Operator.RIGHT_PAREN)) {
                    return (StatementBase) context.getLastError();
                }

                StatementBase elseIfBlock = parseBlock(context);
                conditions.put(elseIfCondition, elseIfBlock);
            } else {
                StatementBase elseBlock = parseBlock(context);
                return new IfStatement(conditions, Optional.of(elseBlock));
            }
        }

        return new IfStatement(conditions, Optional.empty());
    }

    public static boolean isWhileStatement(ParserContext context) {
        return context.is(Keyword.WHILE) || context.is(Keyword.DO);
    }

    public static StatementBase parseWhileStatement(ParserContext context) {
        boolean isDoWhile = false;
        if (context.is(Keyword.DO)) {
            isDoWhile = true;
            context.advance();
        } else if (context.is(Keyword.WHILE)) {
            context.advance();
        } else {
            return new StatementError("Expected 'while' or 'do' keyword.");
        }

        if (isDoWhile == false) {
            if (!context.expect(Operator.LEFT_PAREN)) {
                return (StatementBase) context.getLastError();
            }
        }
        ExpressionBase condition = ExpressionParser.parseExpression(context);
        if (isDoWhile == false) {
            if (!context.expect(Operator.RIGHT_PAREN)) {
                return (StatementBase) context.getLastError();
            }
        }
        StatementBase body = parseBlock(context);
        if (isDoWhile) {
            if (!context.expect(Keyword.WHILE)) {
                return (StatementBase) context.getLastError();
            }
            if (!context.expect(Operator.LEFT_PAREN)) {
                return (StatementBase) context.getLastError();
            }
            condition = ExpressionParser.parseExpression(context);
            if (!context.expect(Operator.RIGHT_PAREN)) {
                return (StatementBase) context.getLastError();
            }
            context.advanceIfSemicolon();
        }

        return new WhileStatement(condition, body, isDoWhile);
    }

    public static boolean isSwitchStatement(ParserContext context) {
        return context.is(Keyword.SWITCH);
    }

    public static StatementBase parseSwitchStatement(ParserContext context) {
        if (!context.expect(Keyword.SWITCH)) {
            return (StatementBase) context.getLastError();
        }
        if (!context.expect(Operator.LEFT_PAREN)) {
            return (StatementBase) context.getLastError();
        }

        Map<ExpressionBase, StatementBase> cases = Map.of();
        while (context.isAndAdvance(Keyword.CASE)) {
            ExpressionBase caseExpr = ExpressionParser.parseExpression(context);
            if (!context.expect(Operator.ASSIGN) || !context.expect(Operator.GREATER_THAN)) {
                return (StatementBase) context.getLastError();
            }
            StatementBase caseBlock = parseBlock(context);
            context.advanceIfSemicolon();
            cases.put(caseExpr, caseBlock);
        }

        Optional<StatementBase> defaultCase = Optional.empty();
        if (context.isAndAdvance(Keyword.DEFAULT)) {
            if (!context.expect(Operator.ASSIGN) || !context.expect(Operator.GREATER_THAN)) {
                return (StatementBase) context.getLastError();
            }
            defaultCase = Optional.of(parseBlock(context));
            context.advanceIfSemicolon();
        }

        if (!context.expect(Operator.RIGHT_PAREN)) {
            return (StatementBase) context.getLastError();
        }

        return new SwitchStatement(cases, defaultCase);
    }

    public static boolean isForStatement(ParserContext context) {
        return context.is(Keyword.FOR);
    }

    public static StatementBase parseForStatement(ParserContext context) {
        if (!context.expect(Keyword.FOR) || !context.expect(Operator.LEFT_PAREN)) {
            return (StatementBase) context.getLastError();
        }

        List<ExpressionBase> conditions = new ArrayList<>();
        while (!context.isEnd() && !context.is(Operator.RIGHT_PAREN)) {
            conditions.add(ExpressionParser.parseExpression(context));
            if (!context.isAndAdvance(Operator.SEMICOLON)) {
                break;
            }
        }

        if (!context.expect(Operator.RIGHT_PAREN)) {
            return (StatementBase) context.getLastError();
        }
        return new ForStatement(conditions, parseBlock(context));
    }

    public static boolean isBreakStatement(ParserContext context) {
        return context.is(Keyword.BREAK);
    }

    public static StatementBase parseBreakStatement(ParserContext context) {
        if (!context.expect(Keyword.BREAK)) {
            return (StatementBase) context.getLastError();
        }
        context.advanceIfSemicolon();
        return new BreakStatement();
    }

    public static boolean isContinueStatement(ParserContext context) {
        return context.is(Keyword.CONTINUE);
    }

    public static StatementBase parseContinueStatement(ParserContext context) {
        if (!context.expect(Keyword.CONTINUE)) {
            return (StatementBase) context.getLastError();
        }
        context.advanceIfSemicolon();
        return new ContinueStatement();
    }

    public static boolean isReturnStatement(ParserContext context) {
        return context.is(Keyword.RETURN);
    }

    public static StatementBase parseReturnStatement(ParserContext context) {
        if (!context.expect(Keyword.RETURN)) {
            return (StatementBase) context.getLastError();
        }

        if (context.isAndAdvance(Operator.SEMICOLON)) {
            return new ReturnStatement(Optional.empty());
        }

        ExpressionBase returnValue = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new ReturnStatement(Optional.of(returnValue));
    }

    public static boolean isStructInitStatement(ParserContext context) {
        return context.nextIs(Operator.LEFT_BRACE);
    }

    public static StatementBase parseStructInitStatement(ParserContext context, TypeBase name) {
        if (!context.expect(Operator.LEFT_BRACE)) {
            return (StatementBase) context.getLastError();
        }

        Map<String, ExpressionBase> fields = new java.util.HashMap<>();

        while (!context.isEnd() && !context.is(Operator.RIGHT_BRACE)) {
            if (!context.expect(TokenType.IDENTIFIER)) {
                return (StatementBase) context.getLastError();
            }
            String fieldName = context.getValueAndAdvance();
            if (!context.expect(Operator.ASSIGN)) {
                return (StatementBase) context.getLastError();
            }
            ExpressionBase fieldValue = ExpressionParser.parseExpression(context);
            fields.put(fieldName, fieldValue);
            if (!context.isAndAdvance(Operator.COMMA)) {
                break;
            }
        }
        if (!context.expect(Operator.RIGHT_BRACE)) {
            return (StatementBase) context.getLastError();
        }
        context.advanceIfSemicolon();
        return new StructInitStatement(name, fields);
    }

    public static boolean isVariableInitStatement(ParserContext context) {
        return context.nextIs(Operator.ASSIGN);
    }

    public static StatementBase parseVariableInitStatement(ParserContext context, TypeBase type) {
        if (!context.expect(Operator.ASSIGN)) {
            return (StatementBase) context.getLastError();
        }

        ExpressionBase value = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new ExpressionStatement(value);
    }

    public static StatementBase parseExpressionStatement(ParserContext context) {
        ExpressionBase expr = ExpressionParser.parseExpression(context);
        context.advanceIfSemicolon();
        return new ExpressionStatement(expr);
    }
}
