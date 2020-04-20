package uk.ac.bournemouth.ap.dotsandboxeslib.matrix

import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext.Coordinate

/** A sparse matrix for storing integers. It works just like SparseMatrix but optimizes int storage.
 * This particular interface only provides read access. The matrix needs to be initialized
 * appropriately or used on a class that is actually mutable.
 */
interface SparseIntMatrix : SparseMatrixCommon {
    operator fun get(x: Int, y: Int): Int

    override fun copyOf(): SparseIntMatrix
}

/**
 * A mutable sparse matrix for integers. This interface supports mutating the values.
 */
interface MutableSparseIntMatrix : SparseIntMatrix {
    operator fun set(x: Int, y: Int, value: Int)

    fun fill(value: Int)
    override fun copyOf(): MutableSparseIntMatrix

    fun contentEquals(other: SparseIntMatrix): Boolean
}

/**
 * Helper function to set values into a [MutableSparseIntMatrix].
 */
inline fun MutableSparseIntMatrix.fill(setter: (Int, Int) -> Int) {
    for ((x, y) in indices) {
        this[x, y] = setter(x, y)
    }
}


interface IntMatrix : MatrixCommon, SparseIntMatrix {
    fun contentEquals(other: IntMatrix): Boolean
    override fun copyOf(): IntMatrix
}

inline fun <T> Matrix<T>.map(transform: (T) -> Int): IntMatrix {
    return IntMatrix(width, height) { x, y -> transform(get(x, y)) }
}

inline fun IntMatrix.map(transform: (Int) -> Int): IntMatrix {
    return IntMatrix(width, height) { x, y -> transform(get(x, y)) }
}

inline fun <R> IntMatrix.map(transform: (Int) -> R): Matrix<R> {
    return MutableMatrix(width, height) { x, y -> transform(get(x, y)) }
}

operator fun IntMatrix.plus(other: Int) = map { it + other }
operator fun IntMatrix.minus(other: Int) = map { it - other }
operator fun IntMatrix.times(other: Int) = map { it * other }
operator fun IntMatrix.div(other: Int) = map { it / other }
operator fun IntMatrix.rem(other: Int) = map { it % other }

operator fun MutableIntMatrix.plusAssign(other: Int) =
    forEachIndex { x, y -> set(x, y, get(x, y) + other) }

operator fun MutableIntMatrix.minusAssign(other: Int) =
    forEachIndex { x, y -> set(x, y, get(x, y) - other) }

operator fun MutableIntMatrix.timesAssign(other: Int) =
    forEachIndex { x, y -> set(x, y, get(x, y) * other) }

operator fun MutableIntMatrix.divAssign(other: Int) =
    forEachIndex { x, y -> set(x, y, get(x, y) / other) }

operator fun MutableIntMatrix.remAssign(other: Int) =
    forEachIndex { x, y -> set(x, y, get(x, y) % other) }

/**
 * Multiply the two matrices. This requires the width of the left matrix to be equal to the height of
 * the right matrix.
 */
operator fun IntMatrix.times(other: IntMatrix): IntMatrix {
    if (width!=other.height) throw IllegalArgumentException("Matrix multiplication requires the width of the first operand to match the height of the second")
    val common = width
    return IntMatrix(other.width, height) { x, y ->
        var sum = 0
        for(n in 0 until common) {
            sum+=get(n,y) * other.get(x, n)
        }
        sum
    }
}


interface MutableIntMatrix : IntMatrix, MutableSparseIntMatrix {
    override fun copyOf(): MutableIntMatrix
}

/**
 * Create an [IntMatrix] initialized with the given value
 *
 * @param width Width of the matrix
 * @param height Height of the matrix
 * @param value The value of all cells
 */
fun IntMatrix(width: Int, height: Int, value: Int): IntMatrix {
    return MutableIntMatrix(width, height).apply {
        fill(value)
    }
}

/**
 * Create an [IntMatrix] initialized according to the init function.
 * @param width Width of the matrix
 * @param height Height of the matrix
 * @param init Function used to initialise each cell.
 */
inline fun IntMatrix(width: Int, height: Int, init: (Int, Int) -> Int): IntMatrix {
    return MutableIntMatrix(width, height).apply {
        fill(init)
    }
}

/**
 * Create a [MutableIntMatrix] initialized with the default value of the [Int] type (`0`)
 *
 * @param width Width of the matrix
 * @param height Height of the matrix
 */
fun MutableIntMatrix(width: Int, height: Int): MutableIntMatrix {
    return ArrayMutableIntMatrix(width, height)
}

/**
 * Create a [SparseIntMatrix] initialized with the given value
 *
 * @param width Width of the matrix
 * @param height Height of the matrix
 * @param value The value of all cells
 * @param validator The function that determines whether a given cell is valid.
 */
fun SparseIntMatrix(width: Int, height: Int, value: Int, validator: (Int, Int) -> Boolean): SparseIntMatrix {
    return MutableSparseIntMatrix(width, height, validator).apply { fill(value) }
}

/**
 * Create a [SparseIntMatrix] initialized with the default value of the [Int] type (`0`)
 *
 * @param width Width of the matrix
 * @param height Height of the matrix
 * @param value The value of all cells
 */
fun MutableSparseIntMatrix(
    width: Int,
    height: Int,
    validator: (Int, Int) -> Boolean
                          ): MutableSparseIntMatrix {
    return ArrayMutableSparseIntMatrix(width, height, validator)
}

/**
 * Helper base class for array based int matrices.
 */
abstract class ArrayMutableIntMatrixBase internal constructor(
    override val maxWidth: Int,
    override val maxHeight: Int,
    protected val data: IntArray
                                                             ) : MutableSparseIntMatrix {

    constructor(maxWidth: Int, maxHeight: Int) :
            this(maxWidth, maxHeight, IntArray(maxWidth * maxHeight))

    override fun get(x: Int, y: Int): Int {
        validate(x, y)
        return data[x * maxWidth + y]
    }

    override fun set(x: Int, y: Int, value: Int) {
        validate(x, y)
        data[x * maxWidth + y] = value
    }


    override fun contentEquals(other: SparseIntMatrix): Boolean {
        val maxX = maxOf(maxWidth, other.maxWidth)
        val maxY = maxOf(maxHeight, other.maxHeight)
        for (x in 0 until maxX) {
            for (y in 0 until maxY) {
                val valid = isValid(x, y)
                val otherValid = other.isValid(x, y)
                if (valid != otherValid) return false
                if (valid) {
                    if (get(x, y) != other.get(x, y)) return false
                }
            }
        }
        return true
    }

}

inline fun ArrayMutableSparseIntMatrix(
    maxWidth: Int,
    maxHeight: Int,
    noinline validator: (Int, Int) -> Boolean,
    init: (Int, Int) -> Int
                                      ): ArrayMutableSparseIntMatrix {
    return ArrayMutableSparseIntMatrix(maxWidth, maxHeight, validator).apply {
        fill(init)
    }
}

class ArrayMutableSparseIntMatrix : ArrayMutableIntMatrixBase {

    private val validator: (Int, Int) -> Boolean

    override val indices: Iterable<Coordinate<Int>> = SparseMatrixIndices(this)

    constructor(maxWidth: Int, maxHeight: Int, validator: (Int, Int) -> Boolean) :
            super(maxWidth, maxHeight) {
        this.validator = validator
    }

    constructor(maxWidth: Int, maxHeight: Int, data: IntArray, validator: (Int, Int) -> Boolean) :
            super(maxWidth, maxHeight, data) {
        this.validator = validator
    }

    override fun copyOf(): MutableSparseIntMatrix {
        return ArrayMutableSparseIntMatrix(maxWidth, maxHeight, data.copyOf(), validator)
    }

    override fun isValid(x: Int, y: Int): Boolean {
        return x in 0 until maxWidth &&
                y in 0 until maxHeight &&
                validator(x, y)
    }


    override fun fill(element: Int) {
        data.fill(element)
    }
}

inline fun ArrayMutableIntMatrix(
    maxWidth: Int,
    maxHeight: Int,
    init: (Int, Int) -> Int
                                ): ArrayMutableIntMatrix {
    val matrix = ArrayMutableIntMatrix(maxWidth, maxHeight)
    for (x in 0 until maxWidth) {
        for (y in 0 until maxHeight) {
            matrix[x, y] = init(x, y)
        }
    }
    return matrix
}

class ArrayMutableIntMatrix :
    ArrayMutableIntMatrixBase,
    MutableIntMatrix {

    constructor(width: Int, height: Int) : super(width, height)

    private constructor(maxWidth: Int, maxHeight: Int, data: IntArray) : super(maxWidth, maxHeight, data)

    override val indices: Iterable<Coordinate<Int>> = SparseMatrixIndices(this)

    override fun fill(element: Int) {
        data.fill(element)
    }

    override fun copyOf(): ArrayMutableIntMatrix {
        return ArrayMutableIntMatrix(width, height, data.copyOf())
    }

    override fun isValid(x: Int, y: Int): Boolean {
        return x in 0 until maxWidth &&
                y in 0 until maxHeight
    }

    override fun contentEquals(other: IntMatrix): Boolean {
        if (width!=other.width || height!=other.height) return false
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (get(x, y) != other.get(x, y)) return false
            }
        }
        return true
    }

    override fun contentEquals(other: SparseIntMatrix): Boolean = when (other) {
        is IntMatrix -> contentEquals(other)
        else         -> super.contentEquals(other)
    }
}