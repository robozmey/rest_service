package ru.kpnn

import kotlin.random.Random

typealias Filename = String

const val STRING_LENGTH = 5

val charPool = ('0'..'9') + ('a'..'z') + ('A'..'Z')

fun generateRandomSuffix() = (1..STRING_LENGTH)
    .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
    .joinToString("")

fun checkFilename(pictureName: Filename) : Filename {
    if (!pictureStorage.contains(pictureName))
        return pictureName

    var suffix = ""
    while (pictureStorage.contains(pictureName + "_" + suffix)) {
        suffix = generateRandomSuffix()
    }

    return pictureName + "_" + suffix;
}

val pictureStorage = mutableSetOf<Filename>()