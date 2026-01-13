package galaga.gscript.parser;

public class TypeSignature extends ParserObject {

    private final String name;
    private final boolean isConst;
    private final boolean isReference;
    private final boolean isArray;

    public TypeSignature(String name, boolean isConst, boolean isReference, boolean isArray) {
        this.name = name;
        this.isConst = isConst;
        this.isReference = isReference;
        this.isArray = isArray;
    }

    public TypeSignature(String name) {
        this(name, false, false, false);
    }

    public String getName() {
        return name;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isReference() {
        return isReference;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        if(this.isConst) {
            sb.append("const ");
        }
        if(this.isReference) {
            sb.append("ref ");
        }
        sb.append(this.name);
        if(this.isArray) {
            sb.append("[]");
        }
        return sb.toString();
    }
    
}
