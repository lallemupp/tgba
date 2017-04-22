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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Test classes for {@link IndexedBookList}
 */
public class IndexedBookListTest {

    private IndexedBookList uut;

    @Before
    public void setup() {
        Book book = new Book("Test Title", "Test Author", new BigDecimal(100.4));
        uut = new IndexedBookList();
        uut.add(book, 10);

    }

    @Test
    public void addBook() {
        Book[] searchResult = uut.list(null);
        Assert.assertEquals("The book was not added as expected", 1, searchResult.length);
    }

    @Test
    public void searchForBook() {
        Book[] searchResult = uut.list("Title");
        Assert.assertEquals("The search result was not as expected", 1, searchResult.length);
    }

    @Test
    public void searchWithMultipleHits() {
        Book book = new Book("Another Title", "Another Author", new BigDecimal(10.5));
        uut.add(book, 3);

        Book[] searchResult = uut.list("Title");
        Assert.assertEquals("Search with multiple hits did not work as expected", 2, searchResult.length);
    }

    @Test
    public void searchWithMultipleWords() {
        Book book = new Book("Another Title", "Another Author", new BigDecimal(10.5));
        uut.add(book, 3);

        Book[] searchResult = uut.list("Another Title");
        Assert.assertEquals("Search with multiple words did not work as expected", 2, searchResult.length);
    }

    @Test
    public void searchWithLowerCase() {
        Book[] searchResult = uut.list("test");
        Assert.assertEquals("Search with lower case did not work", 1, searchResult.length);
    }

    @Test
    public void searchForBookWithPunctuation() {
        Book book = new Book("With. Punctuation:Part II, The Best Part!", "Test Author", new BigDecimal(100.4));
        uut.add(book, 10);

        Book[] searchResult = uut.list("With");
        Assert.assertEquals(". was not cleaned.", 1, searchResult.length);

        searchResult = uut.list("Punctuation");
        Assert.assertEquals(": was not cleaned.", 1, searchResult.length);

        searchResult = uut.list("II");
        Assert.assertEquals(", was not cleaned.", 1, searchResult.length);

        searchResult = uut.list("Part!");
        Assert.assertEquals("! was not cleaned.", 1, searchResult.length);
    }

    @Test
    public void searchWithNoMatch() {
        Book[] searchResult = uut.list("No match!");
        Assert.assertEquals("No match should be an empty book array", 0, searchResult.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNegativeQuantity() {
        Book book = new Book("Another title", "Another Author", new BigDecimal(10));
        uut.add(book, -3);
    }

    @Test
    public void buy() {
        Book book = new Book("Another title", "Another Author", new BigDecimal(10));
        uut.add(book, 0);

        Book[] bookList = uut.list(null);
        int[] actuals = uut.buy(bookList);
        Assert.assertArrayEquals("Buy array was not as expected.", new int[] {0, 1}, actuals);
    }

    @Test
    public void buyNotExistingBook() {
        int[] actuals = uut.buy(new Book("not", "in stock", new BigDecimal(100)));
        Assert.assertArrayEquals(new int[] {2}, actuals);
    }

    @Test
    public void buyAllCopiesOfOneBook() {
        Book book = new Book("Test Title", "Test Author", new BigDecimal(1));

        for (int i = 0; i < 10; i++) {
            uut.buy(book);
            Assert.assertEquals("Buy did not decrease the number of books in stock",
                    9 - i, uut.getCopiesOfBookInStock(book));
        }

        uut.buy(book);
        Assert.assertTrue("Negative number of books in the inventory", uut.getCopiesOfBookInStock(book) == 0);
    }
}
