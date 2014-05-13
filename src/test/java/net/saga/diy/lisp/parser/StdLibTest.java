package net.saga.diy.lisp.parser;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Interpreter.interpretFile;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/*Consider these tests as suggestions for what a standard library for
 your language could contain. Each test function tests the implementation
 of one stdlib function.

 Put the implementation in the file `stdlib.diy` at the `src/main/resources/std` directory
 of the repository. The first function, `not` is already defined for you.
 It's your job to create the rest, or perhaps somthing completely different?

 */
public class StdLibTest {

    Environment env = new Environment();

    @Before
    public void loadStdLib() throws URISyntaxException {
        File stdLib = Paths.get(getClass().getClassLoader().getResource("std/std.diy").toURI()).toFile();
        env = new Environment();
        interpretFile(stdLib, env);

    }

    @Test
    public void testOpenFile() throws URISyntaxException {
        File stdLib = Paths.get(getClass().getClassLoader().getResource("std/std.diy").toURI()).toFile();
        Assert.assertNotNull(stdLib);
        Assert.assertTrue(stdLib.exists());

    }

    @Test
    public void testEcho() {
        assertEquals("foo", evaluate(parse("(echo ('foo))"), env));
    }

    @Test
    public void testDoubleEcho() {
        assertArrayEquals(new Object[]{"foo", "foo"}, (Object[]) evaluate(parse("(double_echo ('foo))"), env));
    }

    @Test
    public void testNot() {
        assertTrue((boolean) evaluate(parse("(not #f)"), env));
        assertFalse((boolean) evaluate(parse("(not #t)"), env));
    }

    @Test
    public void testOr() {
        assertFalse((boolean) evaluate(parse("(or #f #f)"), env));
        assertTrue((boolean) evaluate(parse("(or #f #t)"), env));
        assertTrue((boolean) evaluate(parse("(or #t #f)"), env));
        assertTrue((boolean) evaluate(parse("(or #t #t)"), env));
    }
}
