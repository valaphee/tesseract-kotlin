/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util;

/**
 * @author Kevin Ludwig
 */
public enum MbedTlsSha256HasherImpl {
    ;

    public static native long init();

    public static native void update(long mbedTlsSha256Context, long buffer, int length);

    public static native byte[] digest(long mbedTlsSha256Context);

    public static native void free(long mbedTlsSha256Context);
}
