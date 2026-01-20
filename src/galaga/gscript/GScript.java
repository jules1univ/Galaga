package galaga.gscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import galaga.gscript.ast.Program;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.parser.Parser;
import galaga.gscript.parser.ParserException;

public class GScript {

    public static void main(String[] args) {
        GScript gscript = GScript.folder("C:\\Users\\jules\\Desktop\\Galaga\\src\\galaga\\gscript\\tests");
        gscript.parse();
        
        List<GScriptError> errors = gscript.getErrors();
        for (GScriptError error : errors) {
            System.out.println(error);
        }
        for (Program program : gscript.getPrograms()) {
            System.out.println(program.format());
        }
    }

    private final List<File> files;

    private final List<GScriptError> errors = new ArrayList<>();
    private final List<Program> programs = new ArrayList<>();

    public static GScript folder(String path) {
        File folder = new File(path);
        if (!folder.isDirectory()) {
            return null;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".gscript"));
        return new GScript(List.of(files));
    }

    public static GScript file(String path) {
        return new GScript(List.of(new File(path)));
    }

    private GScript(List<File> files) {
        this.files = files;
    }

    private void processFile(File file) {
        String source = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                source += line + "\n";
            }
        } catch (Exception e) {
            synchronized (this.errors) {
                this.errors.add(GScriptError.io(file, e));
            }
            return;
        }

        Lexer lexer = Lexer.of(source);
        try {
            Parser parser = Parser.of(lexer);
            Program program = parser.parse();
            synchronized (this.programs) {
                this.programs.add(program);
            }
        } catch (ParserException e) {
            synchronized (this.errors) {
                this.errors.add(GScriptError.parse(file, e));
            }
        }
    }

    public void parse() {
        this.files.parallelStream().forEach(this::processFile);
    }

    public List<GScriptError> getErrors() {
        return this.errors;
    }

    public List<Program> getPrograms() {
        return this.programs;
    }
}
    