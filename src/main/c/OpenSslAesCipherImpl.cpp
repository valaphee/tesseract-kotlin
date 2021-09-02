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

#include "com_valaphee_tesseract_util_OpenSslAesCipherImpl.h"
#include "Common.hpp"
#include <openssl/evp.h>

jlong JNICALL Java_com_valaphee_tesseract_util_OpenSslAesCipherImpl_init(JNIEnv* environment, jclass clazz, jboolean encrypt, jbyteArray key, jbyteArray iv) {
	EVP_CIPHER_CTX *evpCipherContext = EVP_CIPHER_CTX_new();
	if (evpCipherContext == NULL) {
		throwException(environment, "EVP_CIPHER_CTX_new returned null", 0);
		return 0;
	}

	jbyte *keyBytes = environment->GetByteArrayElements(key, NULL);
	jbyte *ivBytes = environment->GetByteArrayElements(iv, NULL);
	if (!EVP_CipherInit(evpCipherContext, EVP_aes_256_cfb8(), (uint8_t *) keyBytes, (uint8_t *) ivBytes, encrypt)) {
		throwException(environment, "EVP_CipherInit returned non-zero", 0);
        return 0;
	}

	environment->ReleaseByteArrayElements(key, keyBytes, JNI_ABORT);
	environment->ReleaseByteArrayElements(iv, ivBytes, JNI_ABORT);

	return (jlong) evpCipherContext;
}

void Java_com_valaphee_tesseract_util_OpenSslAesCipherImpl_cipher(JNIEnv* environment, jclass clazz, jlong context, jlong in, jlong out, jint length) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return;
	}

	if (!EVP_CipherUpdate((EVP_CIPHER_CTX*) context, (uint8_t *) out, &length, (uint8_t *) in, length)) {
		throwException(environment, "EVP_CipherUpdate returned non-zero", 0);
		return;
	}
}

void Java_com_valaphee_tesseract_util_OpenSslAesCipherImpl_free(JNIEnv* environment, jclass clazz, jlong context) {
	if (context != 0) EVP_CIPHER_CTX_free((EVP_CIPHER_CTX*) context);
}
