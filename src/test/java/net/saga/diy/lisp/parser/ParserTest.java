/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saga.diy.lisp.parser;

import java.util.Iterator;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.Parser.parse;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.types.LispException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ParserTest {

    @Test
    public void testSingleToken() {
        Token token = Parser.parse("foo");
        Assert.assertEquals(create(String.class, "foo"), token);
    }

    @Test
    public void testSingleNUmber() {
        Token token = parse("42");
        Assert.assertEquals(create(Integer.class, 42), token);

        token = Parser.parse("1337");
        Assert.assertEquals(create(Integer.class, 1337), token);

    }

    @Test
    public void testParseBoolean() {
        Token token = Parser.parse("#t");
        Assert.assertEquals(create(Boolean.class, true), token);

        token = Parser.parse("#f");
        Assert.assertEquals(create(Boolean.class, false), token);
    }

    @Test
    public void testParseListOfSymbols() {
        Token token = Parser.parse("(foo bar baz)");

        Iterator<AST.Token> iter = token.tree.iterator();

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(String.class, "bar"), iter.next());
        Assert.assertEquals(create(String.class, "baz"), iter.next());

    }

    @Test
    public void testParseListOfMixedTypes() {
        Token token = parse("(foo #t 123)");

        Iterator<AST.Token> iter = token.tree.iterator();

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(Boolean.class, true), iter.next());
        Assert.assertEquals(create(Integer.class, 123), iter.next());

    }

    @Test
    public void testParseOfNestedList() {
        Token token = Parser.parse("(foo (bar ((#t)) x) (baz y))");

        Iterator<AST.Token> iter = token.tree.iterator();

        Assert.assertTrue(token.tree.tokens.get(1).tree != null);
        Assert.assertTrue(token.tree.tokens.get(1).tree.tokens.get(1).tree.tokens.get(0).tree != null);
        Assert.assertTrue(token.tree.tokens.get(2).tree != null);

        Assert.assertEquals(create(String.class, "foo"), iter.next());
        Assert.assertEquals(create(String.class, "bar"), iter.next());
        Assert.assertEquals(create(Boolean.class, true), iter.next());
        Assert.assertEquals(create(String.class, "x"), iter.next());
        Assert.assertEquals(create(String.class, "baz"), iter.next());
        Assert.assertEquals(create(String.class, "y"), iter.next());

    }

    @Test(expected = LispException.class)
    public void testMissingExpression() {
        parse("(foo (bar ((#t)) x) (baz y");
    }

    @Test(expected = LispException.class)
    public void testExtraParenException() {
        parse("(foo (bar x y)))");
    }

    @Test
    public void parseExtraWhitespace() {
        Token tree = parse("                             \n"
                + "                                 \n"
                + "(program    with   much        whitespace)\n"
                + "               ");

        assertNotNull(tree.tree);

        assertEquals(tree.tree.tokens.get(0), create(String.class, "program"));
        assertEquals(tree.tree.tokens.get(1), create(String.class, "with"));
        assertEquals(tree.tree.tokens.get(2), create(String.class, "much"));
        assertEquals(tree.tree.tokens.get(3), create(String.class, "whitespace"));
    }

    @Test
    public void parseComments() {
        Token root = parse(" ;; this first line is a comment\n"
                + "(define variable\n"
                + "; here is another comment\n"
                + "(if #t\n"
                + "42 ; inline comment!\n"
                + "(something else)))");
        Token expected = create(new AST());

        expected.tree.tokens.add(create(String.class, "define"));
        expected.tree.tokens.add(create(String.class, "variable"));
        expected.tree.tokens.add(create(new AST()));
        expected.tree.tokens.get(2).tree.tokens.add(create(String.class, "if"));
        expected.tree.tokens.get(2).tree.tokens.add(create(Boolean.class, true));
        expected.tree.tokens.get(2).tree.tokens.add(create(Integer.class, 42));
        expected.tree.tokens.get(2).tree.tokens.add(create(new AST(create(String.class, "something"), create(String.class, "else"))));

        assertEquals(root, expected);

    }

    @Test
    public void testLargeFile() {
        Token ast = parse(" "
                + "(define fact\n"
                + ";; Factorial function\n"
                + "(lambda (n)\n"
                + "(if (<= n 1)\n"
                + "1 ; Factorial of 0 is 1, and we deny\n"
                + "; the existence of negative numbers\n"
                + "(* n (fact (- n 1))))))");

        Token expected = (create(
                new AST(create(String.class, "define"), create(String.class, "fact"), create(
                        new AST(create(String.class, "lambda"), create(
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
        Token ast = parse("(foo 'nil)");

        Token expected = (create(
                new AST(create(String.class, "foo"), create(
                        new AST(QUOTE, create(String.class, "nil"))
                        ))
                ));

        Assert.assertEquals(expected, ast);

    }

    @Test
    public void testNestedQuotes() {
        Token ast = parse("''''foo");
        Token expected = (create(
                new AST(QUOTE, create(
                        new AST(QUOTE, create(
                                new AST(QUOTE, create(
                                        new AST(QUOTE, create(String.class, "foo"))
                                        ))
                                ))
                        ))));
        Assert.assertEquals(expected, ast);
    }

}
