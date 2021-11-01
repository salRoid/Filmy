package tech.salroid.filmy.utility

object NullChecker {

    fun <T> isSettable(stuff: T?): Boolean {
        return !(stuff == null || stuff == "null")
    }
}