package net.saga.diy.lisp.parser;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Interpreter.interpretFile;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author summers
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
    
}
