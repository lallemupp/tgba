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

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the {@link BookListParser} that parses book lists in CVS representation.
 *
 * The format of a line in the CVS is title;author;price;available copies
 */
public class CvsBookListParser implements BookListParser {

    private static final int NUMBER_OF_FIELDS = 4;
    private static final String THOUSAND_SEPARATOR = ",";
    private static final String EMPTY_STRING = "";
    private static final String LIST_SEPARATOR = ";";

    @Override
    public Map<Book, Integer> parse(InputStreamReader bookListStreamReader) throws IOException, ParseException {
        Map<Book, Integer> result = new HashMap<>();
        BufferedReader reader = new BufferedReader(bookListStreamReader);

        int lineNumber = 1;
        String line = reader.readLine();
        while (line != null) {
            String[] words = StringUtils.split(line, LIST_SEPARATOR);

            if (words.length != NUMBER_OF_FIELDS) {
                throw new ParseException("The line " + line + " does not contain " + NUMBER_OF_FIELDS + " fields", lineNumber);
            }

            String title = words[0];
            String author = words[1];
            String priceAsString = words[2].replace(THOUSAND_SEPARATOR, EMPTY_STRING);
            String quantityAsString = words[3];
            Book book = new Book(title, author, new BigDecimal(priceAsString));
            result.put(book, Integer.parseInt(quantityAsString));

            line = reader.readLine();
            lineNumber++;
        }

        return result;
    }
}
