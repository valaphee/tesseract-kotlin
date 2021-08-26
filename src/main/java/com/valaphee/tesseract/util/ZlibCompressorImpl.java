/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util;

/**
 * @author Kevin Ludwig
 */
public final class ZlibCompressorImpl {
    static {
        startup();
    }

    public int consumed;
    public boolean finished;

    public static native void startup();

    public native long init(boolean compress, int level, boolean raw);

    public native int process(long zStream, long in, int inLength, long out, int outLength, boolean compress);

    public native void reset(long zStream, boolean compress);

    public native void free(long zStream, boolean compress);
}
