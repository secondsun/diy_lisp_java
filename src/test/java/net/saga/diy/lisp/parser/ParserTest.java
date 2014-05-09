/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser;

import java.util.Iterator;
import java.util.stream.Stream;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.Parser.parse;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.types.LispException;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

    @Test
    public void testSingleToken() {
        AST ast = Parser.parse("foo");
        Assert.assertEquals(create(String.class, "foo"), ast.iterator().next());
    }

    @Test
    public void testSingleNUmber() {
        AST ast = Parser.parse("42");
        Assert.assertEquals(create(Integer.class, 42), ast.iterator().next());

        ast = Parser.parse("1337");
        Assert.assertEquals(create(Integer.class, 1337), ast.iterator().next());

    }

    @Test
    public void testParseBoolean() {
        AST ast = Parser.parse("#t");
        Assert.assertEquals(create(Boolean.class, true), ast.iterator().next());

        ast = Parser.parse("#f");
        Assert.assertEquals(create(Boolean.class, false), ast.iterator().next());
    }

    @Test
    public void testParseListOfSymbols() {
        AST ast = Parser.parse("(foo bar baz)");

        Iterator<AST.Token> iter = ast.iterator();

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(String.class, "bar"), iter.next());
        Assert.assertEquals(create(String.class, "baz"), iter.next());

    }

    @Test
    public void testParseListOfMixedTypes() {
        AST ast = Parser.parse("(foo #t 123)");

        Iterator<AST.Token> iter = ast.iterator();

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(Boolean.class, true), iter.next());
        Assert.assertEquals(create(Integer.class, 123), iter.next());

    }

    @Test
    public void testParseOfNestedList() {
        AST ast = Parser.parse("(foo (bar ((#t)) x) (baz y))");

        Iterator<AST.Token> iter = ast.iterator();

        Assert.assertTrue(ast.tokens.get(0).tree.tokens.get(1).tree != null);
        Assert.assertTrue(ast.tokens.get(0).tree.tokens.get(1).tree.tokens.get(1).tree.tokens.get(0).tree != null);
        Assert.assertTrue(ast.tokens.get(0).tree.tokens.get(2).tree != null);

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(String.class, "bar"), iter.next());
        Assert.assertEquals(create(Boolean.class, true), iter.next());
        Assert.assertEquals(create(String.class, "x"), iter.next());
        Assert.assertEquals(create(String.class, "baz"), iter.next());
        Assert.assertEquals(create(String.class, "y"), iter.next());

    }

    @Test(expected = LispException.class)
    public void testMissingExpression() {
        AST ast = Parser.parse("(foo (bar ((#t)) x) (baz y");
    }

    @Test(expected = LispException.class)
    public void testExtraParenException() {
        parse("(foo (bar x y)))");
    }

    @Test
    public void parseExtraWhitespace() {
        AST tree = parse("                             \n"
                + "                                 \n"
                + "(program    with   much        whitespace)\n"
                + "               ");
        AST expected = new AST();
        expected.tokens.add(create(new AST()));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "program"));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "with"));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "much"));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "whitespace"));

    }

    @Test
    public void parseComments() {
        AST tree = parse(" ;; this first line is a comment\n"
                + "(define variable\n"
                + "; here is another comment\n"
                + "(if #t\n"
                + "42 ; inline comment!\n"
                + "(something else)))");
        AST expected = new AST();
        expected.tokens.add(create(new AST()));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "define"));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "variable"));
        expected.tokens.get(0).tree.tokens.add(create(new AST()));
        expected.tokens.get(0).tree.tokens.get(2).tree.tokens.add(create(String.class, "if"));
        expected.tokens.get(0).tree.tokens.get(2).tree.tokens.add(create(Boolean.class, true));
        expected.tokens.get(0).tree.tokens.get(2).tree.tokens.add(create(Integer.class, 42));
        expected.tokens.get(0).tree.tokens.add(create(String.class, "whitespace"));

    }

    @Test
    public void testLargeFile() {
        AST ast = parse(" "
        + "(define fact\n"
                + ";; Factorial function\n"
            + "(lambda (n)\n"
                + "(if (<= n 1)\n"
                    + "1 ; Factorial of 0 is 1, and we deny\n"
                    + "; the existence of negative numbers\n"
                    + "(* n (fact (- n 1))))))");
        
        AST expected = 
        new AST(create(
                new AST(create(String.class, "define"), create (String.class, "fact"), create(
                        new AST( create(String.class, "lambda"), create(
                                new AST(create(String.class, "n"))
                        ), create(
                                new AST(create(String.class, "if"), create(
                                    new AST(create(String.class, "<="), create(String.class, "n"), create(Integer.class, 1))
                                ), create(Integer.class, 1), create(
                                        new AST(create(String.class, "*"), create(String.class, "n"), create(new AST(create(String.class, "fact"), create(
                                                new AST(create(String.class, "-"), create(String.class, "n"), create(Integer.class, 1))))))
                                )))))))
        );
        
        Assert.assertEquals(expected, ast);
        
    }
    
    @Test
    public void testQuotes() {
        AST ast = parse("(foo 'nil)");
        
        AST expected = 
                new AST(create(
                        new AST(create(String.class, "foo"), create(
                                new AST(QUOTE, create(String.class, "nil"))
                        ))
                ));
        
        Assert.assertEquals(expected, ast);

        
    }
    
    @Test
    public void testNestedQuotes() {
        AST ast = parse("''''foo"); 
        AST expected = new AST(create(
                            new AST(QUOTE, create(
                                new AST(QUOTE, create(
                                        new AST(QUOTE, create(
                                            new AST(QUOTE, create(String.class, "foo"))
                                        ))
                                ))
                            )))
                            );
        Assert.assertEquals(expected, ast);
    }

}
