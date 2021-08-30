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

/**
 * @author Kevin Ludwig
 */
public enum Format implements StyleCode {
    Bold('l', "bold"),
    Italic('o', "italic"),
    Strikethrough('m', "strikethrough"),
    Underlined('n', "underline"),
    Obfuscated('k', "obfuscated"),
    Reset('r', "reset");

    private final char code;
    private final String key;

    @SuppressWarnings("ThisEscapedInObjectConstruction")
    Format(final char code, final String key) {
        this.code = code;
        this.key = key;

        values.add(this);
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
