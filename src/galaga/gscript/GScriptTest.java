package galaga.gscript;

import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.LexerError;
import galaga.gscript.parser.Parser;

public class GScriptTest {

    public static void main(String[] args) {
        Lexer lexer = Lexer.of("""

                import std.io.{printf};

                extern type Position;
                extern Position getMousePosition();


                type Player = struct {
                    String name;
                    Int score;
                    Position pos;
                    PLayerState state;
                }

                type PLayerState = enum {
                    ALIVE,
                    DEAD,
                    INVINCIBLE
                }

                type FunctionCallback = void created(int score);

                Player create_player(String name, FunctionCallback callback) {
                    Player p{
                        .name = name,
                        .score = 0,
                        .pos = getMousePosition(),
                        .state = PLayerState.ALIVE
                    };
                    callback(p.score);
                    return p;
                }

                void update_player() extends Player {
                    this->score += 10;
                }


                void modify_player(ref Player p) {
                    p->score += 20;
                }

                void read_player(const Player p) {
                    printf("Player %s has score %d\n", p.name, p.score);
                }

                void copy_player(const Player p, Player p2) {
                    Player p3 = p; // Copy
                    p3->score += 30; // can modify p3

                    const Player p4 = p2; // Copy
                    // p4->score += 40; // Error: cannot modify p4

                    printf("Copied player %s with score %d\n", p2.name, p2.score);
                }
                    """);
        try {
            lexer.execute();
        } catch (LexerError e) {
            System.err.println(e.getMessage());
        }
        Parser parser = Parser.of(lexer);
        try{
            parser.execute();
            for (var stmt : parser){
                System.out.println(stmt.format());
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}
