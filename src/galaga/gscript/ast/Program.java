package galaga.gscript.ast;

import java.util.List;

import galaga.gscript.ast.declaration.DeclarationBase;

public class Program implements ASTNode {
    private final List<DeclarationBase> declarations;

    public Program(List<DeclarationBase> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        DeclarationBase lastDecl = null;
        for (DeclarationBase decl : declarations) {
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
