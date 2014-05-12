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
package net.saga.diy.lisp.parser.types;

import java.util.Map;

/**
 *
 * @author summers
 */
public class Utils {

    public static <K, V> V getOrThrow(Map<K, V> map, K key) {

        V value = map.get(key);

        if (value == null) {
            throw new LispException("No value " + key);
        }

        return value;
    }

    public static boolean isList(Object token) {
        try {
            return token.getClass().isArray();
        } catch (Exception ignore) {
            throw new LispException("Illegal token:" + token);
        }
    }

    public static boolean isEmptyList(Object token) {
        try {
            return ((Object[]) token).length == 0;
        } catch (Exception ignore) {
            throw new LispException("Illegal token:" + token);
        }
    }

}
