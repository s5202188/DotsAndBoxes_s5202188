package uk.ac.bournemouth.ap.dotsandboxeslib

/**
 * This is a sealed base class for players. It has two direct subtypes: [HumanPlayer] and
 * [ComputerPlayer]. This sealed type (which does not allow other direct subtypes) ensures that
 * code can be certain to check whether a player is a [ComputerPlayer] and allow its moves to be
 * triggered automatically.
 */
sealed class Player

/**
 * Base class for human players. In principle the type can be used as is, but a subclass can be
 * created to allow for attaching information such as player name.
 */
open class HumanPlayer : Player()

/**
 * Base class for computer players. This class adds the [makeMove] function that allows the player
 * to make a move for the given game.
 */
abstract class ComputerPlayer : Player() {

    /**
     * Make a move for the player. This is normally called by the game, not directly. If the player
     * is set on multiple games (and used to make moves on these separate games), the implementation
     * is required to be implemented to allow this to work correctly (if it records game specific
     * information this needs to be separated from any other game data). The easiest solution is to
     * create a different player instance for each game. It is the responsibility of the game to
     * call this function again if another move can be made in the same turn, this function is only
     * for individual moves.
     *
     * @param game The game to make a move on.
     */
    abstract fun makeMove(game: DotsAndBoxesGame)

}
