/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package inventory;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * An implementation of the {@link BookList} interface that uses two maps and a list to enable
 * fast and effective searching for books.
 *
 * TODO: thread safe, Index checks.
 */
public class IndexedBookList implements BookList {
    private static final String PUNCTUATION_REGEXP = "\\p{P}";

    private List<Book> books;
    private Map<String, List<Integer>> titleIndex;
    private Map<String, List<Integer>> authorIndex;

    IndexedBookList() {
        this.books = new ArrayList<>();
        this.titleIndex = new HashMap<>();
        this.authorIndex = new HashMap<>();
    }

    @Override
    public Book[] list(String searchString) {
        Book[] bookArray;

        if (searchString == null) {
            bookArray = books.toArray(new Book[books.size()]);
        } else {
            String[] searchWords = cleanInput(searchString);
            bookArray = searchForBooks(searchWords);
        }

        return bookArray;
    }

    @Override
    public synchronized void add(Book book, int quantity) {
        this.books.add(book);
        addToIndex(book.getTitle(), titleIndex);
        addToIndex(book.getAuthor(), authorIndex);
    }

    @Override
    // TODO: Synchronize.
    public int[] buy(Book... books) {
        return new int[0];
    }

    private String[] cleanInput(String input) {
        String cleanInput = StringUtils.replaceAll(input, PUNCTUATION_REGEXP, "");
        return StringUtils.split(cleanInput);
    }

    private Book[] searchForBooks(String[] searchWords) {
        Set<Book> temp = new HashSet<>();

        for (String searchWord : searchWords) {
            List<Integer> titleIndexes = titleIndex.get(searchWord);
            addBooksToResult(temp, titleIndexes);

            List<Integer> authorIndexes = authorIndex.get(searchWord);
            addBooksToResult(temp, authorIndexes);
        }

        return temp.toArray(new Book[temp.size()]);
    }

    private void addBooksToResult(Set<Book> temp, List<Integer> titleIndexes) {
        if (titleIndexes != null) {
            for (Integer index : titleIndexes) {
                Book matchingBook = books.get(index);
                temp.add(matchingBook);
            }
        }
    }

    private void addToIndex(String indexString, Map<String, List<Integer>> index) {
        for (String word : cleanInput(indexString)) {
            List<Integer> bookIndex = index.get(word);

            if (bookIndex == null) {
                bookIndex = new ArrayList<>();
            }

            bookIndex.add(this.books.size() - 1);
            index.put(word, bookIndex);
        }
    }
}
