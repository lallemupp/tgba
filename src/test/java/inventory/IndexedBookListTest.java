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
    public void testAddBook() {
        Book[] searchResult = uut.list(null);
        Assert.assertEquals("The book was not added as expected", 1, searchResult.length);
    }

    @Test
    public void testSearchForBook() {
        Book[] searchResult = uut.list("Title");
        Assert.assertEquals("The search result was not as expected", 1, searchResult.length);
    }

    @Test
    public void testSearchWithMultipleHits() {
        Book book = new Book("Another Title", "Another Author", new BigDecimal(10.5));
        uut.add(book, 3);

        Book[] searchResult = uut.list("Title");
        Assert.assertEquals("Search with multiple hits did not work as expected", 2, searchResult.length);
    }

    @Test
    public void testSearchWithMultipleWords() {
        Book book = new Book("Another Title", "Another Author", new BigDecimal(10.5));
        uut.add(book, 3);

        Book[] searchResult = uut.list("Another Title");
        Assert.assertEquals("Search with multiple words did not work as expected", 2, searchResult.length);
    }

    @Test
    public void testSearchForBookWithPunctuation() {
        Book book = new Book("With. Punctuation: Part II, The Best Part!", "Test Author", new BigDecimal(100.4));
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
    public void testSearchWithNoMatch() {
        Book[] searchResult = uut.list("No match!");
        Assert.assertEquals("No match should be an empty book array", 0, searchResult.length);
    }
}
