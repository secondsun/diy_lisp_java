/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.diy.lisp.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FunctionsTest {

    @Test
    public void testLambdaEvaluateToLambda() {
        AST ast = parse("lambda () 42");
        Closure closure = (Closure) evaluate(ast, new Environment());
        assertTrue(closure instanceof Closure);
    }

    @Test
    public void testLambdaKeepsDefiningEnv() {

        Environment env = new Environment(map(entry("foo", 1), entry("bar", 2)));
        AST ast = parse("lambda () 42");
        Closure closure = (Closure) evaluate(ast, env);
        assertEquals(env, closure.getEnv());
    }

    @Test
    public void testLambdaClosureHoldsFunction() {
        AST ast = parse("(lambda (x y) (+ x y))");
        Closure closure = (Closure) evaluate(ast, new Environment());

        AST expected = new AST(create(String.class, "+"), create(String.class, "x"), create(String.class, "y"));

        assertEquals(Lists.newArrayList("x", "y"), closure.getParams());
        assertEquals(create(expected), closure.getBody());

    }

    @Test
    public void testLambdaArgumentsAreList() {

        Closure closure = (Closure) evaluate(parse("(lambda (x y) (+ x y))"), new Environment());

        assertTrue(closure.getParams() instanceof List);
    }

    @Test(expected = LispException.class)
    public void testLambdaFails() {
        evaluate(parse("(lambda not-a-list (body of fn))"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testLambdaNumbeOfArguments() {
        evaluate(parse("(lambda (foo) (bar) (baz))"), new Environment());
    }

    @Test
    public void testBodyIsNotEvaluated() {
        evaluate(parse("(lambda (foo) (function body ((that) would never) work))"), new Environment());
    }
    
    

    private <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<K, V>(key, value);
    }

    private <K, V> HashMap<K, V> map(Map.Entry<K, V>... entries) {
        HashMap<K, V> toReturn = Maps.newHashMapWithExpectedSize(entries.length);

        Arrays.stream(entries)
                .forEach(entry -> toReturn.put(entry.getKey(), entry.getValue()));

        return toReturn;
    }

}
