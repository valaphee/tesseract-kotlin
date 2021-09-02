#!/usr/bin/env python3

#  MIT License
#
#  Copyright (c) 2021, Valaphee.
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy
#  of this software and associated documentation files (the "Software"), to deal
#  in the Software without restriction, including without limitation the rights
#  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#  copies of the Software, and to permit persons to whom the Software is
#  furnished to do so, subject to the following conditions:
#
#  The above copyright notice and this permission notice shall be included in all
#  copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
#  SOFTWARE.

import os
import sys

if sys.platform == 'win32':
    environment = Environment()
else:
    environment = Environment(ENV={'PATH': os.environ['PATH']})

jni_classes = ["target/classes/com/valaphee/tesseract/util/ZlibCompressorImpl.class", "target/classes/com/valaphee/tesseract/util/OpenSslAesCipherImpl.class", "target/classes/com/valaphee/tesseract/util/MbedTlsAesCipherImpl.class", "target/classes/com/valaphee/tesseract/util/OpenSslSha256HasherImpl.class", "target/classes/com/valaphee/tesseract/util/MbedTlsSha256HasherImpl.class"]
jni_headers = environment.JavaH('target/generated-includes', jni_classes, JAVACLASSDIR="target/classes")
environment.Append(CPPPATH=['/usr/lib/jvm/java-8-openjdk-amd64/include/', '/usr/lib/jvm/java-8-openjdk-amd64/include/linux/', 'target/generated-includes'])
environment.Append(LIBS=['crypto', 'mbedcrypto'])
sources = ["src/main/c/Common.cpp", "src/main/c/ZlibCompressorImpl.cpp", "src/main/c/OpenSslAesCipherImpl.cpp", "src/main/c/MbedTlsAesCipherImpl.cpp", "src/main/c/OpenSslSha256HasherImpl.cpp", "src/main/c/MbedTlsSha256HasherImpl.cpp"]
environment.SharedLibrary("src/main/resources/META-INF/native/libtesseract_native_gnulnx64", sources)
