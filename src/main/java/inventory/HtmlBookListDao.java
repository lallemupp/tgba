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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * An implementation of the {@link BookInventoryDao} that reads a book list from
 * an URL.
 */
public class HtmlBookListDao implements BookInventoryDao {

    private BookInventoryParser parser;

    /**
     * Constructor.
     *
     * @param parser the parser that will parse the data.
     */
    public HtmlBookListDao(BookInventoryParser parser) {
        this.parser = parser;
    }

    @Override
    public Map<Book, Integer> bookInventory(String bookInventoryUrl) throws IOException {
        URL url = new URL(bookInventoryUrl);
        Map<Book, Integer> bookInventory;

        try (InputStream inputStream = url.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            bookInventory = parser.parse(reader);
        }

        return bookInventory;
    }
}
