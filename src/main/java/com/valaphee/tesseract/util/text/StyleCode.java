/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Ludwig
 */
public interface StyleCode {
    char Prefix = '\u00A7';
    char AlternativePrefix = '&';
    List<StyleCode> values = new ArrayList<>();

    char getCode();

    String getKey();

    int ordinal();
}
