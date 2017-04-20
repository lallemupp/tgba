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
 * fast and effective searching for booksInStock.
 *
 * TODO: thread safe.
 */
public class IndexedBookList implements BookList {
    private static final String PUNCTUATION_REGEXP = "\\p{P}";

    private final List<Book> booksInStock;
    private final Map<Book, Integer> stockedCopies;
    private final Map<String, List<Integer>> titleIndex;
    private final Map<String, List<Integer>> authorIndex;

    IndexedBookList() {
        this.booksInStock = new ArrayList<>();
        this.stockedCopies = new HashMap<>();
        this.titleIndex = new HashMap<>();
        this.authorIndex = new HashMap<>();
    }

    @Override
    public Book[] list(String searchString) {
        Book[] bookArray;

        if (searchString == null) {
            bookArray = booksInStock.toArray(new Book[booksInStock.size()]);
        } else {
            String[] searchWords = cleanInput(searchString);
            bookArray = searchForBooks(searchWords);
        }

        return bookArray;
    }

    @Override
    public void add(Book book, int quantity) {
        synchronized (stockedCopies) {
            addToBookList(book, quantity);
        }

        addToIndex(book.getTitle(), titleIndex);
        addToIndex(book.getAuthor(), authorIndex);
    }

    @Override
    public int[] buy(Book... books) {
        int[] result = new int[books.length];

        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            int indexInList = booksInStock.indexOf(book);

            if (indexInList >= 0) {
                synchronized (stockedCopies) {
                    int booksInStock = stockedCopies.get(book);

                    if (booksInStock > 0) {
                        result[i] = 0;
                        stockedCopies.put(book, booksInStock - 1);
                    } else {
                        result[i] = 1;
                    }
                }
            } else {
                result[i] = 2;
            }
        }

        return result;
    }

    protected int getCopiesOfBookInStock(Book book) {
        return stockedCopies.get(book);
    }

    private String[] cleanInput(String input) {
        String inputWithoutPunctuation = StringUtils.replaceAll(input, PUNCTUATION_REGEXP, " ");
        String[] searchWords = StringUtils.split(inputWithoutPunctuation);

        return Arrays.stream(searchWords).map(StringUtils::trimToEmpty).toArray(String[]::new);
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
                Book matchingBook = booksInStock.get(index);
                temp.add(matchingBook);
            }
        }
    }

    private void addToBookList(Book book, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a natural number {0, 1, 2, 3...}");
        }

        Integer copies = stockedCopies.get(book);

        if (copies == null) {
            booksInStock.add(book);
            copies = 0;
        }
        stockedCopies.put(book, copies + quantity);
    }

    private void addToIndex(String indexString, Map<String, List<Integer>> index) {
        for (String word : cleanInput(indexString)) {
            List<Integer> bookIndex = index.get(word);

            if (bookIndex == null) {
                bookIndex = new ArrayList<>();
            }

            bookIndex.add(booksInStock.size() - 1);
            index.put(word, bookIndex);
        }
    }
}
