package galaga.gscript.ast;

import java.util.List;

import galaga.gscript.ast.declaration.Declaration;

public class Program implements ASTNode {
    private final List<Declaration> declarations;

    public Program(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        Declaration lastDecl = null;
        for (Declaration decl : declarations) {
            if(lastDecl != null && lastDecl.getClass() != decl.getClass()) {
                sb.append("\n");
            }
            sb.append(decl.format());
            sb.append("\n");

            lastDecl = decl;
        }
        return sb.toString();
    }

}
