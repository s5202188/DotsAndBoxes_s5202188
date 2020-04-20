package uk.ac.bournemouth.ap.dotsandboxeslib

import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix

/**
 * Base interface for the Dots and boxes game. This interface just provides access to game state. To
 * make it work it is required that two coordinate systems are designed. One for boxes, which for a
 * rectangular game is straightforward (a matrix with width and height dimensions). Another system
 * for lines, with the note that the quantity of horizontal and vertical lines in a single row/column
 * is different.
 */
interface DotsAndBoxesGame {
    /**
     * A key part of the game are the lines that the user can click/draw. It is probably best to use
     * a structure such as the [SparseMatrix] type for this. That allows for x/y access but iterating
     * will still skip those values that are not. Lines will also know what the neighboring boxes are.
     */
    val lines: Iterable<Line>

    /**
     * The second important bit is the boxes. They store information of the player that owns/won
     * that box. Boxes also know what dots they have and what lines are around them. For a rectangular
     * game the best type would be a [Matrix] / [ArrayMutableMatrix]
     */
    val boxes: Iterable<Box>

    /**
     * The list of players for the game. A player is either a human or a computer player. The
     * game should automatically play computer players.
     */
    val players: List<Player>
    /**
     * The currently active player. Note that any implementation will need to change this as the
     * turns go, but the code accessing the game should not be able to change this directly.
     */
    val currentPlayer: Player

    /**
     * Determines whether the game has finished. [getScores] can then be used to determine the winner
     */
    val isFinished: Boolean

    /**
     * The current scores for the game. This default implementation just loops through all boxes and
     * counts the boxes owned by each player, but other ways could work as well
     */
    fun getScores(): IntArray = IntArray(players.size).also { s ->
        for (box in boxes) {
            val owningPlayer = box.owningPlayer
            if (owningPlayer != null) {
                val indexOfOwner = players.indexOf(owningPlayer)
                s[indexOfOwner]++
            }
        }
    }

    /**
     * Get the game to perform all computer turns it can. This means until the next human player,
     * or - if there are no human players - play the entire game
     */
    fun playComputerTurns(): Unit

    /**
     * Add a listener that can react to game completion with the relevant score.
     */
    fun addOnGameOverListener(listener: GameOverListener)

    /**
     * Add a listener that can react to general game state changes.
     */
    fun addOnGameChangeListener(listener: GameChangeListener)

    /**
     * Remove the given listener from the game over listeners.
     */
    fun removeOnGameOverListener(listener: GameOverListener)

    /**
     * Remove the given listener from the game over listeners.
     */
    fun removeOnGameChangeListener(listener: GameChangeListener)

    /**
     * An interface for a line in the dots and boxes game. For drawing purposes potential lines
     * that were not drawn yet are represented for ease of use and drawing purposes.
     *
     * When implementing this, it is advised to implement it as an inner class to the game. It does
     * need access to the game, for example for the [drawLine] function.
     */
    interface Line {
        /**
         * The x position of the line in the [DotsAndBoxesGame.lines] matrix.
         */
        val lineX: Int

        /**
         * The y position of the line in the [DotsAndBoxesGame.lines] matrix.
         */
        val lineY: Int

        /**
         * This value represent the current status of the line, and whether it has been drawn.
         * Note that in the game, there is no owner of lines or any difference between lines drawn
         * by any of the players.
         */
        val isDrawn: Boolean

        /**
         * A line that is not on the edge separates two boxes. This property provides those boxes.
         * There should always be at least one box that is not `null`.
         */
        val adjacentBoxes: Pair<Box?, Box?>

        /**
         * This method is used to actually play the game. An implementation may allow computer player
         * moves for subsequent games to be triggered. This can be done by checking that the player
         * with a turn at the end of the implementation of this function implements [ComputerPlayer].
         *
         * Note that there is no requirement that after this has been called that anything except [lineX]
         * or [lineY] is still valid. Of course your implementation could keep this valid.
         */
        fun drawLine()
    }

    /**
     * Type representing the interface of a box that is enclosed by lines between boxes
     */
    interface Box {
        /**
         * The x coordinate of the box as related to [DotsAndBoxesGame.boxes].
         */
        val boxX: Int
        /**
         * The x coordinate of the box as related to [DotsAndBoxesGame.boxes].
         */
        val boxY: Int

        /**
         * The current player that owns this particular box. As the game processes, the box for the same
         * coordinates may likely have an updated player.
         */
        val owningPlayer: Player?

        /**
         * A collection of the lines that surround the box.
         */
        val boundingLines: Iterable<Line>
    }

    /**
     * Event handler for game over events.
     */
    interface GameOverListener {
        fun onGameOver(game: DotsAndBoxesGame, scores: List<Pair<Player, Int>>)
    }

    /**
     * Event handler for game state change events.
     */
    interface GameChangeListener {
        fun onGameChange(game: DotsAndBoxesGame)
    }
}

