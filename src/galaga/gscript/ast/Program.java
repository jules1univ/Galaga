package galaga.gscript.ast;

import java.util.List;

public class Program implements ASTNode {
    private final List<ASTNode> declarations;
    
    public Program(List<ASTNode> declarations) {
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
