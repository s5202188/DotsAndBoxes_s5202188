package uk.ac.bournemouth.ap.dotsandboxeslib.matrix

import java.lang.IndexOutOfBoundsException

inline fun MutableBooleanMatrix(width: Int, height: Int, init: (Int, Int)->Boolean): MutableBooleanMatrix {
    return MutableBooleanMatrix(width, height).also { m ->
        for (x in 0 until width) {
            for (y in 0 until height) {
                m[x, y] = init(x, y)
            }
        }
    }
}

interface BooleanMatrix {
    val width: Int
    val height: Int

    operator fun get(x: Int, y: Int): Boolean
}

class MutableBooleanMatrix private constructor(override val width: Int, private val data: BooleanArray):
    BooleanMatrix {

    override val height: Int get() = data.size/width

    constructor(width: Int, height: Int): this (width, BooleanArray(width*height))
    constructor(source: MutableBooleanMatrix): this(source.width, source.data.copyOf())

    fun copyOf(): MutableBooleanMatrix =
        MutableBooleanMatrix(width, data.copyOf())

    operator fun set(x:Int, y:Int, value: Boolean) {
        if (x !in 0 until width ||
            y !in 0 until height) throw IndexOutOfBoundsException("($x,$y) out of range: ($width, $height)")

        data[x+y*width] = value
    }

    override operator fun get(x:Int, y:Int): Boolean {
        if (x !in 0 until width ||
            y !in 0 until height) throw IndexOutOfBoundsException("($x,$y) out of range: ($width, $height)")

        return data[x+y*width]
    }

    override fun toString(): String = buildString {
        for(y in 0 until height) {
            (0 until width).joinTo(this, separator = " ") {x -> if(get(x, y)) "T" else "F"}
            appendln()
        }
    }
}