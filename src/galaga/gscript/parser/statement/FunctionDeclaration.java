package galaga.gscript.parser.statement;

import java.util.List;

import galaga.gscript.parser.FunctionSignature;

public class FunctionDeclaration extends Statement {

    private final FunctionSignature function;
    private final List<Statement> body;

    public FunctionDeclaration(FunctionSignature function, List<Statement> body) {
        super();
        this.function = function;
        this.body = body;
    }

    public FunctionSignature getFunction() {
        return this.function;
    }

    public List<Statement> getBody() {
        return this.body;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.function.format());
        sb.append(" {\n");
        for (Statement stmt : body) {
            sb.append(stmt.format());
        }
        sb.append("}\n");
        return sb.toString();
    }
    
}
