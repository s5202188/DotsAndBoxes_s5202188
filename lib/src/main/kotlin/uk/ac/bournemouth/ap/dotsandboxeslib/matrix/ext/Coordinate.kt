package uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext

import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix

/**
 * In many cases it is easier to work with a single coordinate rather than
 * with a pair of coordinates. The coordinate class allows this.
 */
inline class Coordinate<out T> constructor(@PublishedApi internal val packed: Int) {

    /** Helper constructor to create a coordinate. */
    constructor(x: Int, y: Int): this((x and 0xffff) shl 16 or (y and 0xffff))

    /**
     * The x part of the coordinate
     */
    val x:Int inline get() = (packed shr 16) and 0xffff

    /**
     * The y part of the coordinate
     */
    val y:Int inline get() = packed and 0xffff

    /** Decomposition operator for x coordinate */
    operator fun component1(): Int=x
    /** Decomposition operator for y coordinate */
    operator fun component2(): Int=y

    override fun toString(): String = "($x, $y)"
}

/**
 * Helper function that implemens [SparseMatrix.isValid] for coordinates
 */
fun <T> SparseMatrix<T>.isValid(pos: Coordinate<T>): Boolean = isValid(pos.x, pos.y)

/**
 * Helper operator to get values based on a coordinate
 */
operator fun <T> SparseMatrix<T>.get(pos: Coordinate<T>): T = get(pos.x, pos.y)

/**
 * Helper operator to set values based upon a coordinate
 */
operator fun <T> MutableSparseMatrix<T>.set(pos: Coordinate<T>, value: T){ set(pos.x, pos.y, value) }

/**
 * Get the position of a [DotsAndBoxesGame.Line] as a coordinate.
 */
val DotsAndBoxesGame.Line.pos: Coordinate<DotsAndBoxesGame.Line>
    get() = Coordinate(lineX, lineY)

/**
 * Get the position of a [DotsAndBoxesGame.Box] as a coordinate.
 */
val DotsAndBoxesGame.Box.pos: Coordinate<DotsAndBoxesGame.Line>
    get() = Coordinate(boxX, boxY)