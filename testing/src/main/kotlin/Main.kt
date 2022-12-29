fun interface CapturedTest0<R> {
    operator fun invoke(): R

    fun source(): String = error("there is no source code for this block")
}

fun interface CapturedTest1<T, R> {
    operator fun invoke(arg: T): R

    fun source(): String = error("there is no source code for this block")
}

fun interface CapturedTestC0<C, R> {
    operator fun C.invoke(): R

    fun source(): String = error("there is no source code for this block")
}

fun main() {
    val t0s = object : CapturedTest0<Int> by (CapturedTest0 { 23 }) {
        override fun source(): String = "inserted source"
    }

    val t1s = object : CapturedTest1<String, Int> by (CapturedTest1 { it.length }) {
        override fun source(): String = "inserted source"
    }

    val c0s = object : CapturedTestC0<String, Int> by (CapturedTestC0<String, Int> { length }) {
        override fun source(): String = "inserted source"
    }

    val t0o = object : CapturedTest0<Int> {
        override fun invoke(): Int { return 23 }

        override fun source(): String = "inserted source"
    }

    val t1o = object : CapturedTest1<String, Int> {
        override fun invoke(arg: String): Int {
            return arg.length
        }

        override fun source(): String = "inserted source"
    }

    val c0o = object : CapturedTestC0<String, Int> {
        override fun String.invoke(): Int {
            return length
        }

        override fun source(): String = "inserted source"
    }
}