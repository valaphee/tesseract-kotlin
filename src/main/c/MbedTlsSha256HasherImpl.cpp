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

#include "com_valaphee_tesseract_util_MbedTlsSha256HasherImpl.h"
#include "Common.hpp"
#include <mbedtls/sha256.h>

jlong JNICALL Java_com_valaphee_tesseract_util_MbedTlsSha256HasherImpl_init(JNIEnv * environment, jclass clazz) {
	mbedtls_sha256_context *mbedTlsSha256Context = new mbedtls_sha256_context;

	mbedtls_sha256_init(mbedTlsSha256Context);
	int result;
	if (result = mbedtls_sha256_starts_ret(mbedTlsSha256Context, 0) != 0) {
		throwException(environment, "mbedtls_sha256_starts_ret returned non-zero", result);
		return 0;
	}

	return (jlong) mbedTlsSha256Context;
}

void JNICALL Java_com_valaphee_tesseract_util_MbedTlsSha256HasherImpl_update(JNIEnv *environment, jclass clazz, jlong context, jlong buffer, jint length) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return;
	}

	int result;
	if (result = mbedtls_sha256_update_ret((mbedtls_sha256_context *) context, (uint8_t *) &buffer, length) != 0) {
		throwException(environment, "mbedtls_sha256_update_ret returned non-zero", result);
		return;
	}
}

jbyteArray JNICALL Java_com_valaphee_tesseract_util_MbedTlsSha256HasherImpl_digest(JNIEnv *environment, jclass clazz, jlong context) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return NULL;
	}

	uint8_t buffer[32];
	int result;
	if (result = mbedtls_sha256_finish_ret((mbedtls_sha256_context *) context, (uint8_t *) buffer) != 0) {
		throwException(environment, "mbedtls_sha256_finish_ret returned non-zero", result);
		return NULL;
	}
	if (result = mbedtls_sha256_starts_ret((mbedtls_sha256_context *) context, 0) != 0) {
		throwException(environment, "mbedtls_sha256_starts_ret returned non-zero", result);
		return NULL;
	}

	const jbyteArray array = environment->NewByteArray(32);
	environment->SetByteArrayRegion(array, 0, 32, (jbyte *) buffer);
	return array;
}

void JNICALL Java_com_valaphee_tesseract_util_MbedTlsSha256HasherImpl_free(JNIEnv *environment, jclass clazz, jlong context) {
	if (context != 0) {
		mbedtls_sha256_context *mbedTlsSha256Context = (mbedtls_sha256_context *) context;
		mbedtls_sha256_free(mbedTlsSha256Context);
		delete mbedTlsSha256Context;
	}
}
