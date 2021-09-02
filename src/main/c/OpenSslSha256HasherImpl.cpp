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

#include "com_valaphee_tesseract_util_OpenSslSha256HasherImpl.h"
#include "Common.hpp"
#include <openssl/evp.h>

jlong JNICALL Java_com_valaphee_tesseract_util_OpenSslSha256HasherImpl_init(JNIEnv * environment, jclass clazz) {
	EVP_MD_CTX *evpMdContext = EVP_MD_CTX_create();
	if (evpMdContext == NULL) {
		throwException(environment, "EVP_MD_CTX_create returned null", 0);
		return 0;
	}

	int result;
	if (!(result = EVP_DigestInit_ex(evpMdContext, EVP_sha256(), NULL))) {
		throwException(environment, "EVP_DigestInit_ex returned non-zero", result);
		return 0;
	}
	return (jlong) evpMdContext;
}

void JNICALL Java_com_valaphee_tesseract_util_OpenSslSha256HasherImpl_update(JNIEnv *environment, jclass clazz, jlong context, jlong buffer, jint length) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return;
	}

	int result;
	if (!(result = EVP_DigestUpdate((EVP_MD_CTX *) context, (uint8_t *) buffer, length))) {
		throwException(environment, "EVP_DigestUpdate returned non-zero", result);
		return;
	}
}

jbyteArray JNICALL Java_com_valaphee_tesseract_util_OpenSslSha256HasherImpl_digest(JNIEnv *environment, jclass clazz, jlong context) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return NULL;
	}

	uint8_t buffer[32];
	EVP_MD_CTX *evpMdContext = (EVP_MD_CTX *) context;
	int result;
	if (!(result = EVP_DigestFinal_ex(evpMdContext, (uint8_t *) buffer, NULL))) {
		throwException(environment, "EVP_DigestFinal_ex returned non-zero", result);
		return NULL;
	}
	if (!(result = EVP_DigestInit_ex(evpMdContext, EVP_sha256(), NULL))) {
		throwException(environment, "EVP_DigestInit_ex returned non-zero", result);
		return NULL;
	}

	const jbyteArray array = environment->NewByteArray(32);
	environment->SetByteArrayRegion(array, 0, 32, (jbyte *) buffer);
	return array;
}

void JNICALL Java_com_valaphee_tesseract_util_OpenSslSha256HasherImpl_free(JNIEnv *environment, jclass clazz, jlong context) {
	if (context != 0) EVP_MD_CTX_destroy((EVP_MD_CTX *) context);
}
