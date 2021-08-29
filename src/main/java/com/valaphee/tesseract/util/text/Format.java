/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
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
