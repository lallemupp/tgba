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
 * An implementation of the {@link BookListDao} that reads a book list from
 * an URL.
 */
public class HtmlBookListDao implements BookListDao {
    private String bookListUrl;

    private BookListParser parser;

    /**
     * Constructor.
     *
     * @param parser the parser that will parse the data.
     * @param bookListUrl the url where the data can be read.
     */
    public HtmlBookListDao(BookListParser parser, String bookListUrl) {
        this.parser = parser;
        this.bookListUrl = bookListUrl;
    }

    @Override
    public Map<Book, Integer> bookList() throws IOException {
        URL url = new URL(bookListUrl);
        Map<Book, Integer> bookList;

        try (InputStream inputStream = url.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            bookList = parser.parse(reader);
        }

        return bookList;
    }
}
