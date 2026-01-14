package galaga.gscript;

import galaga.gscript.lexer.Lexer;
import galaga.gscript.parser.Parser;

/*
 * import -> import <import_patern>
 * <import_patern> -> ID . <import_patern> | ID | { <import_list> }
 * <import_list> -> <import_item> , <import_list> | <import_item>
 * <import_item> -> ID | ID . <import_item>
 * 
 * extern -> extern type ID | extern <function_declaration>
 * 
 * <function_declaration> -> ID ID ( <parameter_list> ) { <function_body> }
 * <parameter_list> -> <parameter> , <parameter_list> | <parameter>
 * <parameter> -> [const] [ref] ID ID
 * <function_body> -> <statements>
 * 
 * <statements> -> <statement>; <statements> | <statement>
 * <statement> -> <return_statement> | <if_statement> | <while_statement> |
 * <for_statement> | <break_statement> | <continue_statement> |
 * <expression_statement>
 * 
 * <return_statement> -> return <expression>
 * <if_statement> -> if ( <expression> ) { <statements> } [<else_statement>]
 * <else_statement> -> else { <statements> } | else if ( <expression> ) {
 * <statements> } [<else_statement>]
 * 
 * <while_statement> -> while ( <expression> ) { <statements> } | do {
 * <statements> } while ( <expression> );
 * <for_statement> -> for ( <expression_statement>; <expression_statement>;
 * <expression> ) { <statements> }
 * <break_statement> -> break
 * <continue_statement> -> continue
 * <expression_statement> -> <expression>
 * 
 * <expression> -> <variables> | <function_call> | <literals> |
 * <binary_expression> | <unary_expression> | ( <expression> )
 * <variables> -> <variable_declaration> | <variable_access>
 * <variable_declaration> -> [const] [ref] ID ID [= <expression>]
 * <variable_access> -> ID [-> ID] | ID [. ID]
 * 
 * <function_call> -> ID ( <argument_list> )
 * <argument_list> -> <expression> , <argument_list> | <expression>
 * 
 * <literals> -> NUMBER | STRING | TRUE | FALSE
 * <binary_expression> -> <expression> OPERATOR <expression>
 * <unary_expression> -> OPERATOR <expression>
 * 
 */

public class GScriptTest {

    public static void main(String[] args) {
        Lexer lexer = Lexer.of("""
                module test;
                
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

                // Player create_player(String name, FunctionCallback callback) {
                //     Player p{
                //         .name = name,
                //         .score = 0,
                //         .pos = getMousePosition(),
                //         .state = PLayerState.ALIVE
                //     };
                //     callback(p.score);
                //     return p;
                // }

                // void update_player() extends Player {
                //     this->score += 10;
                // }


                // void modify_player(ref Player p) {
                //     p->score += 20;
                // }

                // void read_player(const Player p) {
                //     printf("Player %s has score %d\n", p.name, p.score);
                // }

                // void copy_player(const Player p, Player p2) {
                //     Player p3 = p; // Copy
                //     p3->score += 30; // can modify p3

                //     const Player p4 = p2; // Copy
                //     // p4->score += 40; // Error: cannot modify p4

                //     printf("Copied player %s with score %d\n", p2.name, p2.score);
                // }
                    """);
        
                
        Parser parser = Parser.of(lexer);
        try {
            System.out.println(parser.parse().format());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
