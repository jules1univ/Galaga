package galaga.gscript.ast.types;

import galaga.gscript.ast.ASTNode;

public record Type(String name, boolean isConst, boolean isRef, boolean isArray) implements ASTNode{

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        if (isConst) {
            sb.append("const ");
        }
        if (isRef) {
            sb.append("ref ");
        }

        sb.append(name);
        if (isArray) {
            sb.append("[]");
        }
        return sb.toString();
    }
    
}
