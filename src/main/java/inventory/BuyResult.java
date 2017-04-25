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

/**
 * Enumerates the different results when buying books.
 */
public enum BuyResult {
    OK,
    NOT_IN_STOCK,
    DOES_NOT_EXIST;

    /**
     * Returns the value of the enum.
     *
     * @return the value.
     */
    public int toValue() {
        return this.ordinal();
    }

    /**
     * Creates an enum from the provided value.
     *
     * @param value the value.
     * @return the enum that matches to the value.
     */
    public static BuyResult fromValue(int value) {
        BuyResult result;
        switch (value) {
            case 0:
                result = OK;
                break;
            case 1:
                result = NOT_IN_STOCK;
                break;
            case 2:
                result = DOES_NOT_EXIST;
                break;
            default:
                throw new IllegalArgumentException(value + " is not a valid enum value");
        }

        return result;
    }

    @Override
    public String toString() {
        String str;

        switch (this.ordinal()) {
            case 0:
                str = "OK";
                break;
            case 1:
                str = "not in stock";
                break;
            case 2:
                str = "does not exist";
                break;
            default:
                str = "This should never happen";
        }

        return str;
    }
}
