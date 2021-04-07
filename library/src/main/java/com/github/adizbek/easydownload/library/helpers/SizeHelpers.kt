package com.github.adizbek.easydownload.library.helpers

const val SIZE_KB = 1024
const val SIZE_MB = 1024 * SIZE_KB
const val SIZE_GB = 1024 * SIZE_MB


val Int.toKb: Int
    get() = this / 1024

val Int.toMb: Int
    get() = this.toKb / 1024

val Int.toGb: Int
    get() = this.toMb / 1024

val Long.toKb: Long
    get() = this / 1024

val Long.toMb: Long
    get() = this.toKb / 1024

val Long.toGb: Long
    get() = this.toMb / 1024

val Long.toPrettyBytes: String
    get() {
        return when {
            this < SIZE_KB -> {
                "$this b"
            }
            this < SIZE_MB -> {
                "${this.toKb} Kb"
            }
            this < SIZE_GB -> {
                "${this.toMb} Mb"
            }
            else -> {
                "${this.toGb} Gb"
            }
        }
    }

val Int.toPrettyBytes: String
    get() {
        return this.toLong().toPrettyBytes
    }