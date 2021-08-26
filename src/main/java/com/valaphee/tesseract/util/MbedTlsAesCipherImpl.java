/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util;

/**
 * @author Kevin Ludwig
 */
public enum MbedTlsAesCipherImpl {
    ;

    public static native long init(boolean encrypt, byte[] key, byte[] iv);

    public static native void cipher(long cipherContext, long in, long out, int length);

    public static native void free(long cipherContext);
}
