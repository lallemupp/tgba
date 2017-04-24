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

import inventory.Book;
import inventory.BookList;
import inventory.BookListDao;
import inventory.BookListParser;
import inventory.BuyResult;
import inventory.CvsBookListParser;
import inventory.HtmlBookListDao;
import inventory.IndexedBookList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import shopping.Cart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The command line interface.
 */
public class Cli {
    private static final String BOOK_LIST_URL = "http://fridaymastermix.com/bookstoredata.txt";

    private static final String EXIT = "5";
    private static final String BUY_BOOK = "1";
    private static final String EXIT_BOOK_LIST_SUB_MENU = "2";
    private static final String REMOVE_BOOK_FROM_CART = "1";
    private static final String BUY_ALL_BOOKS = "2";
    private static final String EXIT_CHECKOUT_SUB_MENU = "3";
    private static final String FLOATING_NUMBER_REGEX = "\\+?[0-9]*\\.?[0-9]+";
    private static final String LIST_ALL_BOOKS = "1";
    private static final String SEARCH_FOR_BOOKS = "2";
    private static final String CHECKOUT = "3";
    private static final String ADMIN = "4";

    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    private Cli() {

    }

    public static void main(String[] args) throws IOException {
        print("Welcome to the greatest bookstore around");
        print("Loading data from ", BOOK_LIST_URL);

        Cart cart = new Cart();
        BookList bookList = new IndexedBookList();
        BookListParser bookListParser = new CvsBookListParser();
        BookListDao bookListDao = new HtmlBookListDao(bookListParser, BOOK_LIST_URL);
        Map<Book, Integer> bookData = null;

        try {
            bookData = bookListDao.bookList();
        } catch (IOException e) {
            print("Could not read the book data from URL ", BOOK_LIST_URL);
            throw e;
        }

        bookData.forEach(bookList::add);
        print("Data loaded. Bookstore is running at maximum efficiency!");

        String input = "default";
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (!StringUtils.equals(input, EXIT)) {
            printMenu();
            input = bufferedReader.readLine();


            switch (input) {
                case LIST_ALL_BOOKS: {
                    Book[] books = bookList.list(null);
                    printBookList(books);
                    bookListSubMenu(books, cart);
                    break;
                }
                case SEARCH_FOR_BOOKS: {
                    print("Title:");
                    String title = bufferedReader.readLine();
                    print("Author:");
                    String author = bufferedReader.readLine();
                    Book[] books = bookList.list(StringUtils.trimToEmpty(title) +
                            " " + StringUtils.trimToEmpty(author));
                    printBookList(books);
                    bookListSubMenu(books, cart);
                    break;
                }
                case CHECKOUT:
                    checkoutSubMenu(cart, bookList);
                    break;
                case ADMIN:
                    adminSubMenu(bookList);
                    break;
                case EXIT:
                    break;
                default:
                    print("Invalid menu choice. Please try again: ");
            }
        }

        print("Sad to see you go. Hope you come back soon.");
        System.exit(0);
    }

    private static void bookListSubMenu(Book[] books, Cart cart) throws IOException {
        String input;

        do {
            printBookListSubMenu();

            input = bufferedReader.readLine();
            switch (input) {
                case BUY_BOOK:
                    print("Enter the book number: ");
                    String bookInput = bufferedReader.readLine();

                    if (NumberUtils.isDigits(bookInput)) {
                        int bookNumber = Integer.parseInt(bookInput) - 1;

                        if (bookNumber >= 0 && bookNumber < books.length) {
                            cart.add(books[bookNumber]);
                            print("Book ", books[bookNumber].getTitle(), " added to cart");
                        } else {
                            print ("Not a valid book number");
                        }
                    }
                    break;
                case EXIT_BOOK_LIST_SUB_MENU:
                    break;
                default:
                    print("Invalid menu choice. Please try again: ");
            }
        } while (!StringUtils.equals(EXIT_BOOK_LIST_SUB_MENU, input));
    }

    private static void printBookListSubMenu() {
        print("***************************************");
        print("* 1. Buy book                         *");
        print("* 2. Back                             * ");
        print("***************************************");
        print("Make your choice: ");
    }

    private static void checkoutSubMenu(Cart cart, BookList bookList) throws IOException {
        String input;

        do {
            final int[] i = {0};
            List<Book> books = cart.getContent();

            books.forEach(book -> print(String.valueOf(++i[0]), ": ", book.toString()));

            print("Total price of all books: ", String.valueOf(cart.getTotalPrice()), "\n");
            print("***************************************");
            print("* 1. Remove book from cart            *");
            print("* 2. Buy all books                    *");
            print("* 3. Back                             *");
            print("***************************************");
            print("Make your choice: ");
            input = bufferedReader.readLine();

            switch (input) {
                case REMOVE_BOOK_FROM_CART:
                    print("Enter the book number: ");
                    String bookInput = bufferedReader.readLine();

                    if (NumberUtils.isDigits(bookInput)) {
                        int bookNumber = Integer.parseInt(bookInput) - 1;

                        if (bookNumber >= 0 && bookNumber < books.size()) {
                            Book book = books.get(bookNumber);
                            cart.remove(book);
                            print("Book ", book.getTitle(), " removed from cart");
                        } else {
                            print("Not a valid book number");
                        }
                    }

                    break;
                case BUY_ALL_BOOKS:
                    Book[] bookArray = books.toArray(new Book[books.size()]);
                    int[] results = bookList.buy(bookArray);

                    for (int j = 0; j < results.length; j++) {
                        int result = results[j];
                        Book book = bookArray[j];
                        BuyResult buyResult = BuyResult.fromValue(result);
                        print(String.valueOf(j + 1), ": ", book.getTitle(), " ", buyResult.toString());
                        cart.remove(book);
                    }

                    break;
                case EXIT_CHECKOUT_SUB_MENU:
                    break;
                default:
                    print("Invalid menu choice. Please try again: ");

            }
        } while (!StringUtils.equals(BUY_ALL_BOOKS, input) && !StringUtils.equals(EXIT_CHECKOUT_SUB_MENU, input));

    }

    private static void adminSubMenu(BookList bookList) throws IOException {
        String input;

        do {
            print("***************************************");
            print("* 1. Add book to inventory            *");
            print("* 2. Back                             *");
            print("***************************************");

            input = bufferedReader.readLine();
            switch (input) {
                case "1":
                    print("Title: ");
                    String title = bufferedReader.readLine();

                    print("Author: ");
                    String author = bufferedReader.readLine();

                    BigDecimal price = new BigDecimal(0);
                    boolean isValidNumber;

                    do {
                        print("Price: ");

                        try {
                            price = new BigDecimal(bufferedReader.readLine());
                            isValidNumber = true;
                        } catch (NumberFormatException e) {
                            print("Not a valid number. Valid format is " + FLOATING_NUMBER_REGEX);
                            isValidNumber = false;
                        }
                    } while (!isValidNumber);

                    int quantity = 0;
                    do {
                        print("Quantity:");

                        try {
                            quantity = Integer.parseInt(bufferedReader.readLine());
                            isValidNumber = quantity >= 0;
                        } catch (NumberFormatException e) {
                            isValidNumber = false;
                        }

                        if (!isValidNumber) {
                            print("Not a positive integer");
                        }
                    } while (!isValidNumber);

                    Book newBook = new Book(title, author, price);
                    bookList.add(newBook, quantity);
                    print(String.valueOf(quantity), " copies of ", newBook.toString(), " was added to the inventory");
                    break;
                case "2":
                    break;
                default:
                    print("Not a valid menu choice. Please try again: ");

            }
        } while (!StringUtils.equals("2", input));
    }

    private static void printMenu() {
        print("***************************************");
        print("* 1. List all books                   *");
        print("* 2. Search for books                 *");
        print("* 3. Checkout                         *");
        print("* 4. Admin menu                       *");
        print("* 5. Exit                             *");
        print("***************************************");
        print("Make your choice: ");
    }

    private static void printBookList(Book[] books) {
        if (books.length > 0) {
            final int[] i = {0};
            Arrays.stream(books).forEachOrdered(book -> print(String.valueOf(++i[0]), ":", book.toString()));
        } else {
            print("No books found");
        }
    }

    private static void print(String... toPrint) {
        Arrays.stream(toPrint).forEachOrdered(System.out::print);
        System.out.println();
    }
}
