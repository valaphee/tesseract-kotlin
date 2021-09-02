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

#include "com_valaphee_tesseract_util_ZlibCompressorImpl.h"
#include "Common.hpp"
#include <cstdint>
#include <stdlib.h>
#include <zlib.h>

static jfieldID consumedFieldId;
static jfieldID finishedFieldId;

void JNICALL Java_com_valaphee_tesseract_util_ZlibCompressorImpl_startup(JNIEnv *environment, jclass clazz) {
	consumedFieldId = environment->GetFieldID(clazz, "consumed", "I");
	finishedFieldId = environment->GetFieldID(clazz, "finished", "Z");
}

jlong JNICALL Java_com_valaphee_tesseract_util_ZlibCompressorImpl_init(JNIEnv *environment, jobject object, jboolean compress, jint level, jboolean raw) {
	z_stream *zStream = (z_stream *) calloc(1, sizeof(z_stream));

	int result;
	if (result = compress ? raw ? deflateInit2(zStream, level, Z_DEFLATED, -15, 8, Z_DEFAULT_STRATEGY) : deflateInit(zStream, level) : inflateInit(zStream) != Z_OK) throwException(environment, "deflateInit/deflateInit2/inflateInit returned not Z_OK", result);

	return (jlong) zStream;
}

jint JNICALL Java_com_valaphee_tesseract_util_ZlibCompressorImpl_process(JNIEnv *environment, jobject object, jlong context, jlong in, jint inLength, jlong out, jint outLength, jboolean compress) {
	z_stream *zStream = (z_stream *) context;
	zStream->avail_in = inLength;
	zStream->next_in = (uint8_t *) in;
	zStream->avail_out = outLength;
	zStream->next_out = (uint8_t *) out;

	int result;
	switch (result = compress ? deflate(zStream, !inLength ? Z_FINISH : Z_NO_FLUSH) : inflate(zStream, Z_PARTIAL_FLUSH)) {
	case Z_STREAM_END:
		environment->SetBooleanField(object, finishedFieldId, true);
		break;
	case Z_OK:
		break;
	default:
	    throwException(environment, "deflate/inflate returned not Z_OK or Z_STREAM_END", result);
	}

	environment->SetIntField(object, consumedFieldId, inLength - zStream->avail_in);

	return outLength - zStream->avail_out;
}

void JNICALL Java_com_valaphee_tesseract_util_ZlibCompressorImpl_reset(JNIEnv *environment, jobject object, jlong context, jboolean compress) {
	int result;
	if (result = compress ? deflateReset((z_stream *) context) : inflateReset((z_stream *) context) != Z_OK) throwException(environment, "deflateReset/inflateReset returned not Z_OK", result);
}

void JNICALL Java_com_valaphee_tesseract_util_ZlibCompressorImpl_free(JNIEnv *environment, jobject object, jlong context, jboolean compress) {
	z_stream *zStream = (z_stream *) context;
	if (compress) deflateEnd(zStream); else inflateEnd(zStream);
	free(zStream);
}
