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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Map;

public class CvsBookListParserTest {

    private CvsBookListParser uut;
    private InputStreamReader testData;

    @Before
    public void setup() throws Exception {
        uut = new CvsBookListParser();

    }

    @After
    public void teardown() throws Exception {
        if (testData != null) {
            testData.close();
        }
    }

    @Test
    public void parseCvs() throws Exception {
        testData = getTestData("bookstoredata.txt");
        Map<Book, Integer> bookList = uut.parse(testData);
        Assert.assertEquals("The parser failed to parse all books", 7, bookList.size());
    }

    @Test(expected = ParseException.class)
    public void parseCvsCorruptData() throws Exception {
        testData = getTestData("corrupt.txt");
        uut.parse(testData);
    }

    @Test(expected = ParseException.class)
    public void parseCvsEmptyAuthor() throws Exception {
        testData = getTestData("empty_author.txt");
        uut.parse(testData);
    }

    private InputStreamReader getTestData(String fileName) throws Exception {
        InputStream fis = this.getClass().getResourceAsStream(fileName);
        return new InputStreamReader(fis, "UTF-8");
    }
}
