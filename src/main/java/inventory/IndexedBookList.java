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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of the {@link BookList} interface that uses two maps and a list to enable
 * fast and effective searching for booksInStock.
 *
 */
public class IndexedBookList implements BookList {
    private static final String PUNCTUATION_REGEXP = "\\p{P}";

    private final List<Book> booksInStock;
    private final Map<Book, Integer> stockedCopies;
    private final Map<String, List<Integer>> titleIndex;
    private final Map<String, List<Integer>> authorIndex;

    public IndexedBookList() {
        this.booksInStock = new ArrayList<>();
        this.stockedCopies = new HashMap<>();
        this.titleIndex = new HashMap<>();
        this.authorIndex = new HashMap<>();
    }

    /**
     * Returns a {@link Book} array that contains all {@link Book}s that matches the provided search string.
     * If null is passed all books in the inventory will be returned.
     *
     * If the search string contains multiple words a separate search will be done for each word.
     * All words will be used to search for both book titles and authors.
     * All punctuations will be removed and the search will be case insensitive.
     *
     * If "Tolkien" is used as search string,
     * both books by J.R.R Tolkien and books about J.R.R Tolkien would be returned.
     *
     * If "Wedding Rings" is used as search term, all books with wedding in the title
     * and all books with rings in the title will be returned.
     * Also all books written by authors named rings and/or wedding will be returned.
     *
     * @param searchString the search string. If null, all books will be returned.
     * @return an array of {@link Book}s.
     */
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

    /**
     * Adds a book and the quantity available to the inventory
     * and indexes the author and title so that they are searchable.
     *
     * A book is identified by title (case insensitive), author (case insensitive) and price so there can be multiple
     * books with the same title and author but different prices.
     * (this is due to the possibilities of different printings of a book and since).
     *
     * If the book already exists in the inventory the quantity will be added to the current quantity.
     *
     * @param book the book to add.
     * @param quantity the amount of copies that should be added to the inventory.
     *                 Must be a natural number or an {@link IllegalArgumentException} will be thrown.
     */
    @Override
    public void add(Book book, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a natural number {0, 1, 2, 3...}");
        }

        synchronized (stockedCopies) {
            addToBookList(book, quantity);
        }

        addToIndex(book.getTitle(), titleIndex);
        addToIndex(book.getAuthor(), authorIndex);
    }

    /**
     * Checks all books in the provided array and does the following:
     *  # If the book does not exist in the inventory it is marked by a 2 in the response.
     *  # If the book exists but no copies are available it is marked by a 1 in the response.
     *  # if the book exist and there are copies available the amount of available copies is lowerd by 1
     *    and it is marked by a 0 in the response.
     *
     * @param books the books to buy.
     * @return an array with a status for each book.
     *         2 if the book does not exist,
     *         1 if the book exist but is not in stock,
     *         0 if the book could be bought.
     */
    @Override
    public int[] buy(Book... books) {
        int[] result = new int[books.length];

        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            int indexInList = booksInStock.indexOf(book);

            if (indexInList >= 0) {
                synchronized (stockedCopies) {
                    int copiesInStock = stockedCopies.get(book);

                    if (copiesInStock > 0) {
                        result[i] = BuyResult.OK.toValue();
                        stockedCopies.put(book, copiesInStock - 1);
                    } else {
                        result[i] = BuyResult.NOT_IN_STOCK.toValue();
                    }
                }
            } else {
                result[i] = BuyResult.DOES_NOT_EXIST.toValue();
            }
        }

        return result;
    }

    /**
     * A help method for making testing easier.
     *
     * @param book the book to get the amount of copies in stock for.
     * @return the number of copies in stock for the specified book.
     */
    int getCopiesOfBookInStock(Book book) {
        return stockedCopies.get(book);
    }

    private String[] cleanInput(String input) {
        String lowerCaseInput = StringUtils.lowerCase(input);
        String inputWithoutPunctuation = StringUtils.replaceAll(lowerCaseInput, PUNCTUATION_REGEXP, " ");
        String[] searchWords = StringUtils.split(inputWithoutPunctuation);

        return Arrays.stream(searchWords).map(StringUtils::trimToEmpty).toArray(String[]::new);
    }

    private Book[] searchForBooks(String[] searchWords) {
        Set<Book> temp = new HashSet<>();

        for (String searchWord : searchWords) {
            String lowerCaseSearchWord = StringUtils.lowerCase(searchWord);
            List<Integer> titleIndexes = titleIndex.get(lowerCaseSearchWord);
            addBooksToResult(temp, titleIndexes);

            List<Integer> authorIndexes = authorIndex.get(lowerCaseSearchWord);
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
