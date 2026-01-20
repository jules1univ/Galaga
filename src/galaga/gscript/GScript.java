package galaga.gscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import galaga.gscript.ast.Program;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.parser.Parser;
import galaga.gscript.parser.ParserException;
import galaga.gscript.semantic.SemanticAnalyzer;
import galaga.gscript.util.Message;
import galaga.gscript.util.MessageLevel;

public class GScript {

    public static void main(String[] args) {
        GScript gscript = GScript.folder("C:\\Users\\jules\\Desktop\\Galaga\\src\\galaga\\gscript\\tests");
        gscript.parse();

        List<Message> errors = gscript.getMessages();
        for (Message error : errors) {
            System.out.println(error);
        }
        for (Program program : gscript.getPrograms()) {
            System.out.println(program.format());
        }
    }

    private final List<File> files;

    private final List<Message> messages = new ArrayList<>();
    private final List<Program> programs = new ArrayList<>();

    public static GScript folder(String path) {
        File folder = new File(path);
        if (!folder.isDirectory()) {
            return null;
        }
        List<File> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                GScript subGScript = GScript.folder(file.getAbsolutePath());
                if (subGScript != null) {
                    files.addAll(subGScript.files);
                }
            } else if (file.getName().endsWith(".gscript")) {
                files.add(file);
            }
        }
        return new GScript(files);
    }

    public static GScript file(String path) {
        return new GScript(List.of(new File(path)));
    }

    private GScript(List<File> files) {
        this.files = files;
    }

    private void parseFile(File file) {
        String source = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                source += line + "\n";
            }
        } catch (Exception e) {
            synchronized (this.messages) {
                this.messages.add(Message.io(file, e));
            }
            return;
        }

        Lexer lexer = Lexer.of(source);
        try {
            Parser parser = Parser.of(lexer);
            Program program = parser.parse();
            if (program.module() == null) {
                this.messages.add(Message.of("Missing module declaration in file: " + file.getAbsolutePath(),
                        MessageLevel.ERROR));
                return;
            }
            synchronized (this.programs) {
                this.programs.add(program);
            }
        } catch (ParserException e) {
            synchronized (this.messages) {
                this.messages.add(Message.parse(file, e));
            }
        }
    }

    public void parse() {
        this.files.parallelStream().forEach(this::parseFile);
    }

    public void analyze() {
        Map<String, Program> programMap = this.programs.stream()
                .collect(HashMap::new, (map, program) -> map.put(String.join(".", program.module().path()), program),
                        Map::putAll);

        SemanticAnalyzer analyzer = SemanticAnalyzer.of(programMap);
        analyzer.analyze();
        this.messages.addAll(analyzer.getMessages());
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public List<Message> getMessage(MessageLevel level) {
        return this.messages.stream()
                .filter(message -> message.getLevel() == level)
                .toList();
    }

    public List<Program> getPrograms() {
        return this.programs;
    }
}
