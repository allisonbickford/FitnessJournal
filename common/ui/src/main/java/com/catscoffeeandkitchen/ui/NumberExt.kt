package com.catscoffeeandkitchen.ui

fun Float.toCleanString(): String {
    return this.toString().replace(".0", "")
}

fun Double.toCleanString(): String {
    return this.toString().replace(".0", "")
}
