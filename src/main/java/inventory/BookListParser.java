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

package inventory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Map;

/**
 * An interface for parsers that parsers book list.
 */
public interface BookListParser {
    /**
     * Parses a book list from an {@link java.io.InputStreamReader} and returns a map of books and the amount of
     * available copies.
     *
     * @param bookListStreamReader the {@link java.io.InputStreamReader} that contains the book list.
     * @return a map of books and the number of available copies.
     * @throws IOException if the stream can not be read.
     * @throws ParseException if the data can not be parsed.
     */
    Map<Book, Integer> parse(InputStreamReader bookListStreamReader) throws IOException, ParseException;
}
