package galaga.gscript.ast.expression;

import galaga.gscript.ast.expression.collection.IndexExpression;
import galaga.gscript.ast.expression.collection.ListExpression;
import galaga.gscript.ast.expression.collection.MapExpression;
import galaga.gscript.ast.expression.collection.RangeExpression;
import galaga.gscript.ast.expression.function.CallExpression;
import galaga.gscript.ast.expression.function.FunctionExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.expression.operator.UnaryExpression;

public interface ExpressionVisitor<T> {

    T visitBinaryExpression(BinaryExpression node);

    T visitUnaryExpression(UnaryExpression node);

    T visitCallExpression(CallExpression node);

    T visitIndexExpression(IndexExpression node);

    T visitIdentifierExpression(IdentifierExpression node);

    T visitLiteralExpression(LiteralExpression node);

    T visitListExpression(ListExpression node);

    T visitMapExpression(MapExpression node);

    T visitFunctionExpression(FunctionExpression node);

    T visitRangeExpression(RangeExpression node);

}
