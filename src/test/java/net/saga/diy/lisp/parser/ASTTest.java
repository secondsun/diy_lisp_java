package net.saga.diy.lisp.parser;

import java.util.Iterator;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertEquals;
import static net.saga.diy.lisp.parser.AST.Token.create;
import org.junit.Assert;
import org.junit.Test;

public class ASTTest {

    @Test
    public void testAstEquals() {
        AST ast1 = new AST();
        AST ast2 = new AST();
        AST ast3 = new AST();
        ast1.tokens.add(create(Integer.class, 1));
        ast2.tokens.add(create(Integer.class, 1));
        ast3.tokens.add(create(String.class, "Yaryar"));
        ast1.tokens.add(create(ast3));
        ast2.tokens.add(create(ast3));

        Assert.assertEquals(ast1, ast2);

    }

    @Test
    public void testAstIterator() {
        AST ast1 = new AST();
        AST ast2 = new AST();
        AST ast3 = new AST();
        ast1.tokens.add(create(Integer.class, 1));
        ast1.tokens.add(create(String.class, "Yaryar"));

        ast2.tokens.add(create(Integer.class, 15));
        ast2.tokens.add(create(String.class, "yippie"));

        ast3.tokens.add(create(Long.class, 3030l));
        ast2.tokens.add(create(ast3));
        ast1.tokens.add(create(ast2));

        Iterator<AST.Token> iter = ast1.iterator();

        AST.Token token = iter.next();
        assertEquals(create(Integer.class, 1), token);

        token = iter.next();
        assertEquals(create(String.class, "Yaryar"), token);

        token = iter.next();
        assertEquals(create(Integer.class, 15), token);

        token = iter.next();
        assertEquals(create(String.class, "yippie"), token);
        
        token = iter.next();
        assertEquals(create(Long.class, 3030l), token);
        
        assertFalse(iter.hasNext());

    }

}
