package uk.ac.bournemouth.ap.dotsandboxeslib

import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame.Box
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame.Line
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext.Coordinate

/**
 * Simple base class for the Dots and Boxes game that provides some implementation for the core
 * requirements of the game. It is there merely for convenience and does not need to be used.
 * This type should not be used for parameters or properties.
 */
abstract class AbstractDotsAndBoxesGame: DotsAndBoxesGame {
    private val onGameOverListeners = mutableListOf<DotsAndBoxesGame.GameOverListener>()
    private val onGameChangeListeners = mutableListOf<DotsAndBoxesGame.GameChangeListener>()

    override fun addOnGameOverListener(listener: DotsAndBoxesGame.GameOverListener) {
        onGameOverListeners.add(listener)
    }

    override fun addOnGameChangeListener(listener: DotsAndBoxesGame.GameChangeListener) {
        onGameChangeListeners.add(listener)
    }

    override fun removeOnGameOverListener(listener: DotsAndBoxesGame.GameOverListener) {
        onGameOverListeners.remove(listener)
    }

    override fun removeOnGameChangeListener(listener: DotsAndBoxesGame.GameChangeListener) {
        onGameChangeListeners.remove(listener)
    }

    /** Helper function that informs all listeners of the game over event. */
    fun fireGameOver(scores: List<Pair<Player, Int>>) {
        for(listener in onGameOverListeners) {
            listener.onGameOver(this, scores)
        }
    }

    /**
     * Helper function that informs all listeners of the game state change.
     */
    fun fireGameChange() {
        for(listener in onGameChangeListeners) {
            listener.onGameChange(this)
        }
    }

    /**
     * Simple base class for [Box] implementations. There is no requirement to use this type at all.
     */
    abstract inner class AbstractBox(val pos: Coordinate<Box>): Box {
        constructor(boxX: Int, boxY: Int): this(Coordinate(boxX, boxY))

        final override val boxX: Int get() = pos.x
        final override val boxY: Int get() = pos.y
    }

    /**
     * Simple base class for [Line] implementations. There is no requirement to use this type at all.
     */
    abstract inner class AbstractLine(val pos: Coordinate<Line>): Line {
        constructor(lineX: Int, lineY: Int): this(Coordinate(lineX, lineY))

        final override val lineX: Int get() = pos.x
        final override val lineY: Int get() = pos.y
    }
}