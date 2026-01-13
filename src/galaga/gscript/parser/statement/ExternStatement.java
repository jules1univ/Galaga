
package galaga.gscript.parser.statement;

import galaga.gscript.parser.FunctionSignature;
import galaga.gscript.parser.TypeSignature;

public final class ExternStatement extends Statement {

    private final TypeSignature type;
    private final FunctionSignature func;

    public ExternStatement(TypeSignature type) {
        super();
        this.type = type;
        this.func = null;
    }

    public ExternStatement(FunctionSignature func) {
        super();
        this.type = null;
        this.func = func;
    }

    public boolean isType() {
        return this.type != null;
    }

    public TypeSignature getType() {
        return this.type;
    }

    public FunctionSignature getFunction() {
        return this.func;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("extern ");
        if (this.type != null) {
            sb.append("type ");
            sb.append(this.type.format());
            sb.append(";\n");
        } else if (this.func != null) {
            sb.append(this.func.format());
        }
        return sb.toString();
    }
}
