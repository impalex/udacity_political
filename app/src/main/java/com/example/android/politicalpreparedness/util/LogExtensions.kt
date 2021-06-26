package com.example.android.politicalpreparedness.util

fun Any.tag() =
    javaClass.simpleName.let { it.substring(0, 23.coerceAtMost(it.length)) }
