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

import static util.Print.print;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the {@link BookInventoryParser} that parses book inventories in CSV representation.
 *
 * The format of a line in the CSV is title;author;price;available copies
 */
public class CsvBookInventoryParser implements BookInventoryParser {

    private static final int TITLE = 0;
    private static final int AUTHOR = 1;
    private static final int PRICE = 2;
    private static final int QUANTITY = 3;

    private static final int NUMBER_OF_FIELDS = 4;
    private static final String THOUSAND_SEPARATOR = ",";
    private static final String EMPTY_STRING = "";
    private static final String LIST_SEPARATOR = ";";

    @Override
    public Map<Book, Integer> parse(InputStreamReader bookInventoryStreamReader) throws IOException {
        Map<Book, Integer> bookInventory = new HashMap<>();
        BufferedReader reader = new BufferedReader(bookInventoryStreamReader);

        String line = reader.readLine();

        while (line != null) {
            parseLine(bookInventory, line);
            line = reader.readLine();
        }

        return bookInventory;
    }

    private void parseLine(Map<Book, Integer> bookInventory, String line) {
        String[] words = StringUtils.split(line, LIST_SEPARATOR);

        if (words.length != NUMBER_OF_FIELDS) {
            return;
        }

        String title = words[TITLE];
        String author = words[AUTHOR];
        String priceAsString = words[PRICE].replace(THOUSAND_SEPARATOR, EMPTY_STRING);
        String quantityAsString = words[QUANTITY];

        try {
            Book book = new Book(title, author, new BigDecimal(priceAsString));
            bookInventory.put(book, Integer.parseInt(quantityAsString));
        } catch (NumberFormatException e) {
            print("Could not parse \"", quantityAsString, "\" to a BigDecimal. Row will be skipped");
        }
    }
}
