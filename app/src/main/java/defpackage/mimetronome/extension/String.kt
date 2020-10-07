package defpackage.mimetronome.extension

fun String.toByteArrayAsHex(): ByteArray {
    val result = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        result[i / 2] =
            ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
    }
    return result
}