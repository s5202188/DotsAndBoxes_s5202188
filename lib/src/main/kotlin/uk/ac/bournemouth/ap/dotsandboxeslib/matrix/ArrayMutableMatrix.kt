package uk.ac.bournemouth.ap.dotsandboxeslib.matrix

/**
 * Create a new [MutableMatrix] with the given size and intialization
 * @param width The width of the matrix
 * @param height The height of the matrix
 * @param init An initialization function that sets the values for the matrix.
 */
inline fun <T> MutableMatrix(width: Int, height: Int, init: (Int, Int) -> T): MutableMatrix<T> {
    // We need to first create it nullable and then set it to the correct non-null values.
    @Suppress("UNCHECKED_CAST")
    return ArrayMutableMatrix<T?>(width, height, null).also { m ->
        for (x in 0 until width) {
            for (y in 0 until height) {
                m[x, y] = init(x, y)
            }
        }
    } as ArrayMutableMatrix<T>
}

/**
 * Create a new [MutableSparseMatrix] with the given size and intialization function. It also requires
 * a validate function
 * @param width The width of the matrix
 * @param height The height of the matrix
 * @param init An initialization function that sets the values for the matrix.
 * @param validate A function that is used to determine whether a particular coordinate is contained
 *                 in the matrix.
 */

@Suppress("FunctionName")
inline fun <T> MutableSparseMatrix(
    width: Int,
    height: Int,
    init: (Int, Int) -> T,
    noinline validate: (Int, Int) -> Boolean = { _, _ -> true }
                                  ): ArrayMutableSparseMatrix<T> {
    @Suppress("UNCHECKED_CAST")
    return ArrayMutableSparseMatrix<T?>(width, height, null, validate).also { m ->
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (validate(x, y)) {
                    m[x, y] = init(x, y)
                }
            }
        }
    } as ArrayMutableSparseMatrix<T>
}

/**
 * Mutable matrix implementation based upon an array to store the data. It is the basis for
 * implementing both [ArrayMutableMatrix] and [ArrayMutableSparseMatrix].
 *
 * @constructor This constructor is for "internal use" in that it takes the data array as parameter.
 * @property maxWidth The maximum width of the matrix
 * @property data The actual array to get the data
 */
abstract class ArrayMutableMatrixBase<T> protected constructor(
    override val maxWidth: Int,
    private val data: Array<T?>
                                                              ) :
    MutableSparseMatrix<T> {

    @Suppress("UNCHECKED_CAST")
    constructor(width: Int, height: Int, initValue: T) :
            this(width, (arrayOfNulls<Any?>(width * height) as Array<T?>).fillWith(initValue)) {
    }

    constructor(original: ArrayMutableMatrixBase<T>) : this(
        original.maxWidth,
        original.data.copyOf()
                                                           )

    abstract override fun copyOf(): ArrayMutableMatrixBase<T>

    final override val maxHeight: Int get() = data.size / maxWidth

    final override operator fun set(x: Int, y: Int, value: T) {
        validate(x, y)

        data[x + y * maxWidth] = value
    }

    final override operator fun get(x: Int, y: Int): T {
        validate(x, y)

        @Suppress("UNCHECKED_CAST")
        return data[x + y * maxWidth] as T
    }


    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var nextPos = -1

            init {
                moveToNextValidPos()
            }

            override fun hasNext(): Boolean = nextPos < data.size

            @Suppress("UNCHECKED_CAST")
            override fun next(): T = (data[nextPos] as T).also {
                moveToNextValidPos()
            }

            private fun moveToNextValidPos() {
                do {
                    nextPos++
                } while (nextPos < data.size && !isValid(nextPos % maxWidth, nextPos / maxWidth))
            }
        }
    }

}

/**
 * Implementation of a [MutableSparseMatrix] based upon an array to store the data.
 */
class ArrayMutableSparseMatrix<T> : ArrayMutableMatrixBase<T>,
                                    SparseMatrix<T> {

    private val validate: (Int, Int) -> Boolean

    private constructor(maxWidth: Int, data: Array<T?>, validate: (Int, Int) -> Boolean) :
            super(maxWidth, data) {
        this.validate = validate
    }

    /**
     * Create a new instance of the class with the given validation function. All elements of the
     * matrix will be set to the same initial value. The validation function does not need to
     * validate that the coordinate is within
     *
     * @param maxWidth The width of the matrix in columns
     * @param maxHeight The height of the matrix in rows
     * @param initValue The initial value for all cells
     * @param validate The function used to determine whether a cell is used in the matrix.
     */
    constructor(maxWidth: Int, maxHeight: Int, initValue: T, validate: (Int, Int) -> Boolean) :
            super(maxWidth, maxHeight, initValue) {
        this.validate = validate
    }

    /**
     * Create a new instance that is a copy of the original matrix. This will be a shallow copy as
     * in the elements will not be copied.
     */
    constructor(original: ArrayMutableSparseMatrix<T>) : super(original) {
        this.validate = original.validate
    }

    /** Create a copy of this matrix */
    override fun copyOf(): ArrayMutableSparseMatrix<T> =
        ArrayMutableSparseMatrix(this)

    /**
     * Determine whether a given coordinate is valid for the matrix.
     */
    override fun isValid(x: Int, y: Int): Boolean {
        return x in 0 until maxWidth && y in 0 until maxHeight && validate(x, y)
    }

    override fun toString(): String {
        val bs = StringBuilder().append("ArrayMutableSparseMatrix[")
        var firstRow = true
        for (y in 0 until maxHeight) {
            if (firstRow) {
                firstRow = false
            } else {
                bs.append(",")
            }
            var firstCol = true
            for (x in 0 until maxWidth) {
                if (firstCol) {
                    bs.appendln().append("    ")
                    firstCol = false
                } else {
                    bs.append(", ")
                }
                if (validate(x, y)) {
                    val v = get(x, y)
                    bs.append(if (v === this) "(this matrix)" else v)
                } else {
                    bs.append("-- ")
                }

            }
        }
        if (!firstRow) bs.appendln()
        bs.append("]")
        return bs.toString()
    }
}

class ArrayMutableMatrix<T> : ArrayMutableMatrixBase<T>,
                              MutableMatrix<T> {
    private constructor(maxWidth: Int, data: Array<T?>) : super(maxWidth, data)

    constructor(width: Int, height: Int, initValue: T) : super(width, height, initValue)
    constructor(original: ArrayMutableMatrixBase<T>) : super(original)

    override fun copyOf(): ArrayMutableMatrix<T> =
        ArrayMutableMatrix(this)
}

private fun <T> Array<T?>.fillWith(value: T): Array<T> {
    fill(value)
    @Suppress("UNCHECKED_CAST") // this will be correct per rules of fill
    return this as Array<T>
}