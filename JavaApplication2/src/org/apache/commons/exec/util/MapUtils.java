/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.commons.exec.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper classes to manipulate maps to pass substition map to the CommandLine. This class is not part of the public API
 * and could change without warning.
 *
 * @version $Id: MapUtils.java 1636205 2014-11-02 22:32:33Z ggregory $
 */
public class MapUtils
{
    /**
     * Clones a map.
     * 
     * @param source
     *            the Map to clone
     * @param <K>
     *            the map key type
     * @param <V>
     *            the map value type
     * @return the cloned map
     */
   public static <K, V> Map<K, V> copy(final Map<K, V> source) {

        if (source == null) {
            return null;
        }

        final Map<K, V> result = new HashMap<>();
        result.putAll(source);
        return result;
    }
}
