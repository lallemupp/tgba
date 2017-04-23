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

package ui;

import inventory.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

/**
 * The command line interface.
 */
public class Cli {

    private static final String BOOK_LIST_URL = "http://fridaymastermix.com/bookstoredata.txt";

    private static final String EXIT = "4";

    public static void main(String[] args) throws IOException {
        print("Welcome to the greatest bookstore around");
        print("Loading data from", BOOK_LIST_URL);

        BookList bookList = new IndexedBookList();
        BookListParser bookListParser = new CvsBookListParser();
        BookListDao bookListDao = new HtmlBookListDao(bookListParser, BOOK_LIST_URL);
        Map<Book, Integer> bookData = null;

        try {
            bookData = bookListDao.bookList();
        } catch (IOException e) {
            print("Could not read the book data from URL", BOOK_LIST_URL);
            System.exit(1);
        } catch (ParseException e) {
            print("Could not parse the data from URL", BOOK_LIST_URL);
            System.exit(2);
        }

        bookData.forEach(bookList::add);
        print("Data loaded. Bookstore is running at maximum efficiency!");

        String input = "default";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (!StringUtils.equals(input, EXIT)) {
            print(menu());
            input = bufferedReader.readLine();


            switch (input) {
                case "1": {
                    Book[] books = bookList.list(null);
                    print(printBookList(books));
                    break;
                }
                case "2": {
                    print("Title:");
                    String title = bufferedReader.readLine();
                    print("Author:");
                    String author = bufferedReader.readLine();
                    Book[] books = bookList.list(StringUtils.trimToEmpty(title) +
                            " " + StringUtils.trimToEmpty(author));
                    print(printBookList(books));
                    break;
                }
                case "3":
                    print("TBD");
//                    print(cart());
            }
        }

        print("Sad to see you go. Hope you come back soon.");
        System.exit(0);
    }

    private static void print(String... toPrint) {
        Arrays.stream(toPrint).forEachOrdered(s -> System.out.print(s + " "));
        System.out.println();
    }

    private static String menu() {
        StringBuilder sb = new StringBuilder();
        sb.append("***************************************\n");
        sb.append("* 1. List all books                   *\n");
        sb.append("* 2. Search for books                 *\n");
        sb.append("* 3. Checkout                         *\n");
        sb.append("* 4. Exit                             *\n");
        sb.append("***************************************\n");
        sb.append("Make your choice: ");
        return sb.toString();
    }

    private static String printBookList(Book[] books) {
        StringBuilder sb = new StringBuilder();
        sb.append("***************************************\n");
        if (books.length > 0) {
            Arrays.stream(books).forEachOrdered(book -> sb.append(book).append("\n"));
        } else {
            sb.append("* No books found                      *\n");
        }
        sb.append("***************************************\n");
        return sb.toString();
    }
}
