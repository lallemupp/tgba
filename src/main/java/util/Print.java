/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package util;

import java.util.Arrays;

/**
 * A class that holds a static print method (akin to the print in python3) that can be imported to
 * simplify console logging.
 */
public class Print {

    private Print() {

    }

    /**
     * Prints the strings in the varargs on at a time and then prints a new line.
     *
     * @param toPrint the varargs containing the strings to print.
     */
    public static void print(String... toPrint) {
        Arrays.stream(toPrint).forEachOrdered(System.out::print);
        System.out.println();
    }
}
