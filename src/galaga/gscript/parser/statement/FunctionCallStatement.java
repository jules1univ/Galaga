package galaga.gscript.parser.statement;

import java.util.List;

import galaga.gscript.parser.expression.Expression;

public class FunctionCallStatement extends Statement {

    private final String name;
    private final List<Expression> arguments;

    public FunctionCallStatement(String name, List<Expression> args) {
        super();
        this.name = name;
        this.arguments = args;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append("(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).format());
            if (i < arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(");\n");
        return sb.toString();
    }

}
