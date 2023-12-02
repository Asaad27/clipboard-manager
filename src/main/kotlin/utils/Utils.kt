package utils

import java.math.BigInteger
import java.security.MessageDigest

fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')