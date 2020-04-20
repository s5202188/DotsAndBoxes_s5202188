package uk.ac.bournemouth.ap.dotsandboxeslib.matrix

import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext.Coordinate

/**
 * Functionality that is shared among all Matrix implementations
 * (for primitives and object ones)
 */
interface SparseMatrixCommon {
    /** The maximum x coordinate that is valid which can be stored. */
    val maxWidth: Int
    /** The maximum x coordinate that is valid */
    val maxHeight: Int

    val indices: Iterable<Coordinate<Any?>>

    /**
     * This function can be used to determine whether the given coordinates are valid. Returns
     * true if valid. This function works on any value for the coordinates and should return `false`
     * for all values out of range (`x<0 || x>=[maxWidth]`), (`y<0 || y>=[maxHeight]`).
     */
    fun isValid(x: Int, y: Int): Boolean

    /**
     * Creates a copy of the matrix of an appropriate type with the same content.
     */
    fun copyOf(): SparseMatrixCommon
}

inline fun SparseMatrixCommon.forEachIndex(action: (Int, Int)-> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            if (isValid(x, y)) {
                action(x, y)
            }
        }
    }
}

inline fun MatrixCommon.forEachIndex(action: (Int, Int)-> Unit) {
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            action(x, y)
        }
    }
}

/**
 * Helper function for implementing matrices that throws an exception if the
 * coordinates are out of range.
 */
internal fun SparseMatrixCommon.validate(x: Int, y: Int) {
    if (!isValid(x, y)) throw IndexOutOfBoundsException("($x,$y) out of range: ($maxWidth, $maxHeight)")
}

interface MatrixCommon : SparseMatrixCommon {
    /** The width of the matrix. This is effectively the same as [maxWidth]. */
    val width: Int get() = maxWidth
    /** The height of the matrix. This is effectively the same as [maxWidth]. */
    val height: Int  get() = maxHeight

    /**
     * The indices of all columns in the matrix
     */
    val columnIndices: IntRange get() = 0 until width
    /**
     * The indices of all rows in the matrix
     */
    val rowIndices: IntRange get() = 0 until height

    /**
     * This implementation will just check that the coordinates are in range. There should be no
     * reason to no use this default implementation.
     */
    override fun isValid(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }

    override fun copyOf(): MatrixCommon
}


internal class SparseMatrixIndices<T>(private val matrix: SparseMatrixCommon) :
    Iterable<Coordinate<T>> {
    override fun iterator(): Iterator<Coordinate<T>> = IteratorImpl(matrix)

    private class IteratorImpl<T>(private val matrix: SparseMatrixCommon) :
        Iterator<Coordinate<T>> {
        private var nextPoint = -1
        private val maxPoint = matrix.maxWidth * matrix.maxHeight

        init {
            moveToNext()
        }

        private fun moveToNext() {
            do {
                nextPoint++
            } while (nextPoint < maxPoint && isValidPoint(nextPoint))
        }

        private fun isValidPoint(point: Int): Boolean {
            val divisor = matrix.maxWidth
            return matrix.isValid(point % divisor, point / divisor)
        }

        override fun hasNext(): Boolean {
            return nextPoint < maxPoint
        }

        override fun next(): Coordinate<T> {
            val point = nextPoint
            moveToNext()
            return Coordinate<T>(point % maxPoint, point / maxPoint)
        }
    }
}

internal class MatrixIndices<T>(private val matrix: MatrixCommon) : Iterable<Coordinate<T>> {
    override fun iterator(): Iterator<Coordinate<T>> = IteratorImpl(matrix)

    private class IteratorImpl<T>(private val matrix: MatrixCommon) : Iterator<Coordinate<T>> {
        private var nextPoint = 0
        private val maxPoint = matrix.width * matrix.height

        override fun hasNext(): Boolean {
            return nextPoint < maxPoint
        }

        override fun next(): Coordinate<T> {
            val point = nextPoint
            ++nextPoint
            return Coordinate<T>(point % maxPoint, point / maxPoint)
        }
    }

}