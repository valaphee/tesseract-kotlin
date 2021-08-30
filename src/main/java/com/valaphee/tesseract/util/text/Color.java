/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.util.text;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Ludwig
 */
public enum Color implements StyleCode {
    White('f', "white"),
    Yellow('e', "yellow"),
    LightPurple('d', "light_purple"),
    Red('c', "red"),
    Aqua('b', "aqua"),
    Green('a', "green"),
    Blue('9', "blue"),
    DarkGray('8', "dark_gray"),
    Gray('7', "gray"),
    Gold('6', "gold"),
    DarkPurple('5', "dark_purple"),
    DarkRed('4', "dark_red"),
    DarkAqua('3', "dark_aqua"),
    DarkGreen('2', "dark_green"),
    DarkBlue('1', "dark_blue"),
    Black('0', "black");

    private static final Map<String, Color> byKey;

    static {
        byKey = new HashMap<>(values().length);
        for (final Color color : values()) {
            byKey.put(color.key, color);
        }
    }

    private final char code;
    private final String key;

    @SuppressWarnings("ThisEscapedInObjectConstruction")
    Color(final char code, final String key) {
        this.code = code;
        this.key = key;

        values.add(this);
    }

    public static Color byKey(final String key) {
        return byKey.get(key);
    }

    @Override
    public char getCode() {
        return code;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return new String(new char[]{Prefix, code});
    }
}
