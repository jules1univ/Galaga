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
        for (ASTNode decl : declarations) {
            sb.append(decl.format());
        
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
