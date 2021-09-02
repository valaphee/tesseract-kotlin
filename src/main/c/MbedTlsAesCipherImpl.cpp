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

#include "com_valaphee_tesseract_util_MbedTlsAesCipherImpl.h"
#include "Common.hpp"
#include <string.h>
#include <mbedtls/aes.h>

struct CipherContext {
	mbedtls_aes_context context;
	int mode;
    uint8_t *iv;
};

jlong JNICALL Java_com_valaphee_tesseract_util_MbedTlsAesCipherImpl_init(JNIEnv* environment, jclass clazz, jboolean encrypt, jbyteArray key, jbyteArray iv) {
	CipherContext *cipherContext = new CipherContext;

	jbyte *keyBytes = environment->GetByteArrayElements(key, NULL);
	mbedtls_aes_init(&cipherContext->context);
	int result;
	if (result = mbedtls_aes_setkey_enc(&cipherContext->context, (uint8_t *) keyBytes, environment->GetArrayLength(key) * 8) != 0) {
		throwException(environment, "mbedtls_aes_setkey_enc returned non-zero", result);
    	return 0;
	}

	cipherContext->mode = encrypt ? MBEDTLS_AES_ENCRYPT : MBEDTLS_AES_DECRYPT;

	jbyte *ivBytes = environment->GetByteArrayElements(iv, NULL);
	jsize ivLength = environment->GetArrayLength(iv);
	memcpy(cipherContext->iv = new uint8_t[ivLength], ivBytes, ivLength);

	environment->ReleaseByteArrayElements(key, keyBytes, JNI_ABORT);
	environment->ReleaseByteArrayElements(iv, ivBytes, JNI_ABORT);

	return (jlong) cipherContext;
}

void Java_com_valaphee_tesseract_util_MbedTlsAesCipherImpl_cipher(JNIEnv* environment, jclass clazz, jlong context, jlong in, jlong out, jint length) {
	if (context == 0) {
		throwException(environment, "Already freed", 0);
		return;
	}

	CipherContext *cipherContext = (CipherContext *) context;
	int result;
	if (result = mbedtls_aes_crypt_cfb8(&cipherContext->context, cipherContext->mode, length, cipherContext->iv, (uint8_t *) in, (uint8_t *) out) != 0) throwException(environment, "mbedtls_aes_crypt_cfb8 returned non-zero.", result);
}

void Java_com_valaphee_tesseract_util_MbedTlsAesCipherImpl_free(JNIEnv* environment, jclass clazz, jlong context) {
	if (context != 0) {
		CipherContext *cipherContext = (CipherContext *) context;
		delete cipherContext->iv;
		mbedtls_aes_free(&cipherContext->context);
		delete cipherContext;
	}
}
