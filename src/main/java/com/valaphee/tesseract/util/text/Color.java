/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
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
