package uk.ac.bournemouth.ap.dotsandboxeslib.matrix

import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext.Coordinate


/**
 * A matrix type (based upon [SparseMatrix]) but it has values at all coordinates.
 */
interface Matrix<out T>: MatrixCommon, SparseMatrix<T> {

    override val indices: Iterable<Coordinate<T>> get() = MatrixIndices<T>(this)
    override fun copyOf(): Matrix<T>
}

/**
 * A 2-dimensional storage type/matrix that does not require values in all cells. This is a
 * read-only type. The writable version is [MutableSparseMatrix]. The minimum coordinate is always
 * 0. This implements [Iterable] to allow you to get all values (this relies on [isValid]).
 */
interface SparseMatrix<out T>: SparseMatrixCommon, Iterable<T> {
    /**
     * Operator to get the values out of the matrix.
     */
    operator fun get(x:Int, y: Int): T

    override val indices: Iterable<Coordinate<T>> get() = SparseMatrixIndices<T>(this)
    override fun copyOf(): SparseMatrix<T>
}

/**
 * A mutable version of [SparseMatrix] that adds a setter ([set]) to allow for changing the values
 * in the matrix.
 */
interface MutableSparseMatrix<T>: SparseMatrix<T> {
    /**
     * Operator/function to set the value at the given coordinate.
     */
    operator fun set(x: Int, y: Int, value: T)
    override fun copyOf(): MutableSparseMatrix<T>
}

/**
 * An extension to Matrix that is mutable. This is effectively a 2D array.
 */
interface MutableMatrix<T> : MutableSparseMatrix<T>,
                             Matrix<T> {
    override fun copyOf(): MutableMatrix<T>
}


inline fun <T,R> Matrix<T>.map(transform: (T) -> R): Matrix<R> {
    return MutableMatrix(width, height) { x, y -> transform(get(x, y)) }
}
