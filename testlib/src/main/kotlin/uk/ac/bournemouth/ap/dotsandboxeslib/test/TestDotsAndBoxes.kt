package uk.ac.bournemouth.ap.dotsandboxeslib.test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import uk.ac.bournemouth.ap.dotsandboxeslib.AbstractDotsAndBoxesGame.AbstractBox
import uk.ac.bournemouth.ap.dotsandboxeslib.AbstractDotsAndBoxesGame.AbstractLine
import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame.Box
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame.Line
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.ext.Coordinate
import kotlin.random.Random

abstract class TestDotsAndBoxes {

    abstract fun createGame(
        columns: Int = 8,
        rows: Int = 8,
        players: List<Player> = listOf(HumanPlayer(), HumanPlayer())
                           ): DotsAndBoxesGame

    /**
     * Test that game box coordinates horizontally are in line with game width.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMaxWidth(width: Int, height: Int) {
        val game = createGame(width, height)
        val lastX = game.boxes.maxBy { it.boxX }?.boxX
        assertEquals(
            width - 1,
            lastX,
            "The highest horizontal box index is expected to be 1 less than the width of the game (in boxes) - This may not be true if you use a different coordinate system"
                    )
    }

    /**
     * Test that game box coordinates horizontally are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMinWidth(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstX = game.boxes.minBy { it.boxX }?.boxX
        assertEquals(
            0,
            firstX,
            "The smallest horizontal box index is expected to be 0 - This may not be true if you use a different coordinate system"
                    )
    }

    /**
     * Test that game box coordinates vertically are in line with game height.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMaxHeight(width: Int, height: Int) {
        val game = createGame(width, height)
        val lastY = game.boxes.maxBy { it.boxY }?.boxY
        assertEquals(
            height - 1,
            lastY,
            "The higest vertical box index is expected to be 1 less than the height of the game (in boxes) - This may not be true if you use a different coordinate system"
                    )
    }

    /**
     * Test that game box coordinates vertically are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMinHeight(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstY = game.boxes.minBy { it.boxY }?.boxY
        assertEquals(
            0,
            firstY,
            "The smallest vertical box index is expected to be 0 - This may not be true if you use a different coordinate system"
                    )
    }

    /**
     * Test that horizontal line coordinates are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameLineIndexMinX(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstX = game.lines.minBy { it.lineX }?.lineX
        assertNotNull(firstX, "There should be at least one line (even for a 1x1 game)")
        assertTrue(firstX!! >= 0, "The lowest line index should be at least 0")
    }

    /**
     * Test that vertical line coordinates are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameLineIndexMinY(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstY = game.lines.minBy { it.lineY }?.lineY
        assertNotNull(firstY, "There should be at least one line (even for a 1x1 game)")
        assertTrue(firstY!! >= 0, "The lowest line index should be at least 0")
    }

    /**
     * Test that by default a game has 2 players.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGamePlayers(width: Int, height: Int) {
        val game = createGame(width, height)
        assertEquals(2, game.players.size, "Default games should have 2 players")
    }

    /**
     * Test that when given a specific list of players, this list of players
     * is retained/stored in the game.
     */
    @ParameterizedTest(name = "playerCount = {0}")
    @ValueSource(ints = [1, 2, 3, 5])
    fun testPlayerList(playerCount: Int) {
        val players = List(playerCount) { HumanPlayer() }
        val game = createGame(players = players)
        assertEquals(
            players,
            game.players,
            "The amount of players should be the same as that passed in to the createGame function"
                    )
    }

    /**
     * Test that the game copies the list of players, so that only the list elements are shared,
     * not the list itself (so changing the list after creating the game should have any effect).
     */
    @ParameterizedTest(name = "playerCount = {0}")
    @ValueSource(ints = [1, 2, 3, 5])
    fun testPlayerListDisconnected(playerCount: Int) {
        val players = MutableList(playerCount) { HumanPlayer() }
        val game = createGame(players = players)
        players.clear() // Remove players from the list
        assertEquals(
            playerCount, game.players.size,
            "The player list in the game should be a copy of the list passed in, not the same object"
                    )
    }

    /**
     * Test that a fresh game has no drawn lines.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllLinesBlank(width: Int, height: Int) {
        val game = createGame(width, height)
        assertTrue({
                       game.lines.all { !it.isDrawn }
                   }, {
                       "Not all lines are unset: ${game.lines}"
                   })
    }

    /**
     * Test that for a fresh game no boxes have an owner.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllBoxesUnowned(width: Int, height: Int) {
        val game = createGame(width, height)
        assertTrue({
                       game.boxes.all { it.owningPlayer == null }
                   }, {
                       "Not all boxes are unowned: ${game.boxes}"
                   })
    }

    /**
     * Test that game.lines does not return any repeated lines.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllLinesUnique(width: Int, height: Int) {
        val game = createGame(width, height)
        val seenLines = mutableSetOf<Coordinate<Line>>()
        for (line in game.lines) {
            assertTrue(
                seenLines.add(line.coordinates),
                "Line at coordinate ${line.coordinates} occurs multiple times in the game"
                      )
        }
    }

    /**
     * Test that all lines have at least one neighbor box.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllHaveANeighbor(width: Int, height: Int) {
        val game = createGame(width, height)
        for (line in game.lines) {
            assertTrue(
                line.adjacentBoxes.toList().filterNotNull().size in 1..2,
                "Each box should return 1 or 2 adjacent boxes - set them to null if they are not valid"
                      )
        }
    }

    /**
     * Test that all boxes returned from `game.boxes` can be found through getting
     * the neighbors for all lines return from `game.lines`. Also make sure that no
     * unexpected boxes are returned as neighbors.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllBoxesAreNeighbors(width: Int, height: Int) {
        val game = createGame(width, height)
        val expectedBoxes = game.boxes.asSequence().map { it.coordinates }.toSet()
        val seenBoxes = mutableSetOf<Coordinate<Box>>()
        for (line in game.lines) {
            for (n in line.adjacentBoxes) {
                val c = n.coordinates
                assertTrue(
                    c in expectedBoxes,
                    "The box with coordinates $c (found as adjacent box to the line with coordinates ${line.coordinates}) should also be present in the boxes returned by DotsAndBoxesGame.boxes"
                          )
                seenBoxes.add(c)

            }
        }
        assertEquals(
            expectedBoxes.size,
            seenBoxes.size,
            "game.boxes should return boxes with exactly those coordinates that are returned through the adjacent boxes of a line"
                    )
    }

    /**
     * Test that all lines returned through `game.lines` are the bound of a box, and that no
     * box bounds are not returned through `game.lines`.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllLinesAreBounding(width: Int, height: Int) {
        val game = createGame(width, height)
        val expectedLines = game.lines.asSequence().map { it.coordinates }.toSet()
        val seenLines = mutableSetOf<Coordinate<Line>>()
        for (box in game.boxes) {
            for (l in box.boundingLines) {
                val c = l.coordinates
                assertTrue(
                    c in expectedLines,
                    "The line $c returned as bounding line for the box at ${box.coordinates} should also be returned in DotsAndBoxesGame.lines"
                          )
                seenLines.add(c)
            }
        }
        assertEquals(expectedLines.size, seenLines.size) {
            val missingLines = expectedLines.toMutableSet()
            missingLines.removeAll(seenLines)
            "Some lines returned by DotsAndBoxesGame.lines were not returned as bounding lines of the boxes in the game." +
                    "Most likely the validation function on the lines sparseMatrix is incorrect. The extra lines are: $missingLines"

        }
    }

    /**
     * Test that no box returns duplicate bounding lines.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllBoundingLinesUnique(width: Int, height: Int) {
        val game = createGame(width, height)
        for (box in game.boxes) {
            val seenLines = mutableSetOf<Coordinate<Line>>()
            for (line in box.boundingLines) {
                assertTrue(
                    seenLines.add(line.coordinates),
                    "Line at coordinate ${line.coordinates} occurs multiple times in box ${box} at ${box.coordinates}"
                          )
            }
        }
    }

    /**
     * Test that game.boxes does not return duplicate boxes/box coordinates.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllBoxesUnique(width: Int, height: Int) {
        val game = createGame(width, height)
        val seenBoxes = mutableSetOf<Coordinate<Box>>()
        for (box in game.boxes) {
            assertTrue(
                seenBoxes.add(box.coordinates),
                "Box $box at coordinate ${box.coordinates} occurs multiple times in the game"
                      )
        }
    }

    /**
     * Test that all neighbor boxes returned as neighbors to any line are also returned from `game.boxes`,
     * and that all boxes are a neighbor of at least one line.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    @Deprecated("Duplicates testAllBoxesAreNeighbors")
    fun testAllKnownNeighbors(width: Int, height: Int) {
        val game = createGame(width, height)
        val allBoxCoordinates = game.boxes.asSequence().map(Box::coordinates).toSet()
        val neighborCoordinates = mutableSetOf<Coordinate<Box>>()
        for (line in game.lines) {
            for (n in line.adjacentBoxes) {
                val coordinates = n.coordinates
                // Check that all neighbor boxes
                assertTrue(
                    coordinates in allBoxCoordinates,
                    "The box with coordinates $coordinates (found as adjacent box to the line with coordinates ${line.coordinates}) should also be present in the boxes returned by DotsAndBoxesGame.boxes"
                          )
                neighborCoordinates.add(coordinates)
            }
        }
        assertEquals(
            allBoxCoordinates.size,
            neighborCoordinates.size,
            "game.boxes should return boxes with exactly those coordinates that are returned through the adjacent boxes of a line"
                    )
    }

    /**
     * - For all lines `l`
     *     - given the boxes `n1` and `n2` adjacent to line `l`
     *     - for each of `n1` and `n2` -> labeling them as `adjBox` and `other` in turn / ignoring nulls
     *         - If box `other` is not `null` (it exists)
     *             - find a single, unique, line `sameLine` that has both boxes `n1` and `n2` as neighbors
     *             - check that the coordinates of line `l` and `sameLine` are actually the same.
     *         - else if there is no other
     *             - Just check that there is a single, unique, line with the same coordinates and `adjBox`
     *               as its single neighbor.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testReflectiveNeighbors(width: Int, height: Int) {
        val game = createGame(width, height)
        for (line in game.lines) {
            val (n1, n2) = line.adjacentBoxes
            for (boxesToTest in arrayOf(n1 to n2, n2 to n1)) {
                val (adjBox, other) = boxesToTest
                if (adjBox != null) {
                    if (other != null) {
                        val sameLines = adjBox.boundingLines.filter { candidateLine ->
                            candidateLine.adjacentBoxes.equiv(boxesToTest)
                        }
                        when (sameLines.size) {
                            0 -> fail("No single line copy found")
                            1 -> {
                            }
                            else -> fail<Unit>("multiple lines found ${sameLines}")
                        }
                        assertEquals(
                            line.coordinates, sameLines.single().coordinates,
                            "There should be exactly 1 line in common between neigbors box $adjBox at " +
                                    "${adjBox.coordinates} and box $other at ${other.coordinates} "
                                    )
                    } else {
                        adjBox.boundingLines.singleOrNull { candidateLine ->
                            candidateLine.adjacentBoxes.equiv(boxesToTest) &&
                                    line.isSame(candidateLine)
                        } ?: fail("No single line copy found for line $line")
                    }
                }
            }
        }
    }

    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testMoveTwiceDisallowed(width: Int, height: Int, rnd: Random) {
        val game = createGame(width, height)
        val lineCoordinates: Coordinate<Line>
        run {
            val line = game.lines.toList().random(rnd)
            line.drawLine()
            lineCoordinates = line.coordinates
        }
        run {
            val line = game.lines[lineCoordinates]
            assertTrue(line.isDrawn, "After drawing a line, its isDrawn property should now be true")
            assertThrows<Exception>("Attempting to draw a line already drawn should throw an exception (any exception)") {
                line.drawLine()
            }
        }
    }

    /**
     * Test making a single move on an otherwise empty game.
     */
    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testMakeMove(width: Int, height: Int, rnd: Random) {
        val game = createGame(width, height)

        // Add a game listener to make sure that works
        val gameListener = TestGameListener(game)
        game.addOnGameChangeListener(gameListener)
        game.addOnGameOverListener(gameListener)

        val origLines = game.lines.toList()
        val lineToPlay = origLines.random(rnd) // Pick a random line to play
        assertEquals(false, origLines[lineToPlay.coordinates].isDrawn, "A game just created should not have any drawn line")
        // The player before drawing
        val origPlayer = game.currentPlayer

        lineToPlay.drawLine()

        // The amount of lines in the game should not have changed.
        assertEquals(origLines.size, game.lines.count(), "The amount of lames in a game should be constant")

        // The line that was just played. There is no requirement for this to be the same object
        // as the `lineToPlay` - or for `lineToPlay` to still be valid so we find it again.
        val newLine = game.lines[lineToPlay.coordinates]

        // The reloaded line needs to be drawn
        assertEquals(true, newLine.isDrawn, "The line returned from lines with the drawn coordinates should have the isDrawn property set, even if it is not the exact same object")
        // There should be exactly one drawn line in th game
        assertEquals(1, game.lines.count { it.isDrawn }, "A game with one move should have exactly 1 line drawn")
        // Playing the single line cannot complete a box, so no repeat turns. New player is needed
        assertNotEquals(origPlayer, game.currentPlayer, "After a single move, the first player cannot be the current player as a repeat move requires a completed box (at least 3 lines for triangles, 4 for rectangles etc.)")
        // Check that the playing triggered the listener exactly once for a game state change
        assertEquals(1, gameListener.onGameChangeCalled, "The game change listener should have been invoked exactly once")
        // Check that the game over listener was not called
        assertFalse(gameListener.onGameOverCalled, "The game cannot be complete after a single move")
        // Check that none of the boxes next to the line has become complete/gotten an owner
        for (b in newLine.adjacentBoxes) {
            assertNull(b.owningPlayer, "After only 1 move, no box can have an owner")
        }
    }

    /**
     * Test playing a single box in its entirety. This test will also test that getting lines
     * from box or game makes no difference, either in playing or in state. To do this it will
     * get a line both ways and randomly choose which line to play (and then verifying the new
     * state both ways.
     *
     * Note that it is perfectly valid (but not required) for the game to always return the
     * same box in which case the whole (get it different ways) rigmarole is a bit of overkill.
     */
    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testGamePlayBox(width: Int, height: Int, rnd: Random) {
        val game = createGame(width, height)
        val boxToPlay = game.boxes.toList().random(rnd)
        val targetCoordinate = boxToPlay.coordinates
        val lineCoordinatesToPlay = boxToPlay.boundingLines.asSequence()
            .map { it.coordinates }
            .toMutableList().apply { shuffle(rnd) }

        for (lineCoordinate in lineCoordinatesToPlay) {
            run {
                val lineFromGame = game.lines[lineCoordinate]
                val box = game.boxes[targetCoordinate]
                val lineFromBox = box.boundingLines.singleOrNull { it.coordinates == lineCoordinate } ?: fail("There should be a single line surrounding the box with the expected coordinates")
                assertNull(box.owningPlayer, "When starting a game, no box should have an owner")
                assertEquals(lineCoordinate, lineFromGame.coordinates, "The coordinates of a line should match those used to retrieve it (in DotsAndBoxesGame.lines)")
                assertEquals(lineCoordinate, lineFromBox?.coordinates, "When getting the box again, there should be exactly 1 line in the bounding lines of the box with the coordinates")
                assertFalse(lineFromBox.isDrawn, "This game draws its own lines, so the line should not be magically become drawn when getting it through the box")
                assertFalse(lineFromGame.isDrawn, "This game draws its own lines, so the line should not be magically become drawn when getting it from DotsAndBoxesGame.line")
                val playFromGame = rnd.nextBoolean()
                if (playFromGame) {
                    lineFromGame.drawLine()
                } else {
                    lineFromBox.drawLine()
                }
            }
            run {
                val lineFromGame = game.lines[lineCoordinate]
                val box = game.boxes[targetCoordinate]
                val lineFromBox = box.boundingLines.single { it.coordinates == lineCoordinate }
                assertTrue(lineFromBox.isDrawn, "After drawing a line it should be drawn when retrieving it as bounding line of a box")
                assertTrue(lineFromGame.isDrawn, "After drawing a line it should be drawn when retrieving it from DotsAndBoxesGame.lines")
            }
        }
        run {
            val playedBox = game.boxes[targetCoordinate]
            assertNotNull(playedBox.owningPlayer, "When all bounding lines of a box are drawn, the box should now have an owning player")
            for (line in playedBox.boundingLines) {
                assertTrue(line.isDrawn, "All the lines that bound the box should have their isDrawn property as true.")
            }
            for (lineCoordinate in lineCoordinatesToPlay) {
                assertTrue(game.lines[lineCoordinate].isDrawn, "For the bounding line coordinates, retrieving the lines with those coordinates should have drawn lines")
            }
        }

    }

    /**
     * Test that if a game listener is removed, it will no longer receive game related messages.
     * Also test that adding a listener back will work correctly. This also tests that multiple
     * listeners are properly supported.
     */
    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testRemoveGameChangeListener(width: Int, height: Int, rnd: Random) {
        val game = createGame(width, height)

        val gameListener1 = TestGameListener(game)
        val gameListener2 = TestGameListener(game)
        game.addOnGameChangeListener(gameListener1)
        game.addOnGameChangeListener(gameListener2)

        // Pick a random line to play
        val lineToPlay = game.lines.toList().random(rnd)

        // Both listeners were registered
        lineToPlay.drawLine()

        // Both listeners should have been called
        assertEquals(1, gameListener1.onGameChangeCalled, "The 1st gameChangeListener should have been called exactly once")
        assertEquals(1, gameListener2.onGameChangeCalled, "The 2nd gameChangeListener should have been called exactly once")
        // Game over should not have been called
        assertFalse(gameListener2.onGameOverCalled, "The game over listener should not have been called at all")

        run {
            // When removing listener 2 it should not be updated when playing
            // another random line, but listener 1 should.

            game.removeOnGameChangeListener(gameListener2)
            val nextLineToPlay = game.lines.filter { !it.isDrawn }.random(rnd)
            nextLineToPlay.drawLine()
            assertEquals(2, gameListener1.onGameChangeCalled, "After the 2nd gameChangeListener has been removed, the first one should have still been called on a drawLine")
            assertEquals(1, gameListener2.onGameChangeCalled, "After the 2nd gameChangeListener has been removed, it should not be called on a drawLine")
        }

        run {
            // When reregistering listener 2 and removing listener 1, listener 2 should
            // be notified, but listener 1 not.
            game.addOnGameChangeListener(gameListener2)
            game.removeOnGameChangeListener(gameListener1)
            val nextLineToPlay = game.lines.filter { !it.isDrawn }.random(rnd)
            nextLineToPlay.drawLine()
            assertEquals(2, gameListener1.onGameChangeCalled, "After removing the 1st gameChnageListener it should not be called on a drawLine")
            assertEquals(2, gameListener2.onGameChangeCalled, "After re-adding the 2nd gameChangeListener (and removing the 1st), it should be called again on drawLine")
        }
    }

    /**
     * Test playing a complete game by randomly playing each line returned by
     * game.lines. This stores the coordinates for reloading the line objects, but
     * coordinates need to be stable.
     */
    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testCompleteGame(width: Int, height: Int, rnd: Random) {
        val game = createGame(width, height)
        assertFalse(game.isFinished, "A game should not start out finished")

        // We use two listeners to allow us to better test onGameOver behaviour.
        val gameListener1 = TestGameListener(game)
        game.addOnGameChangeListener(gameListener1)
        game.addOnGameOverListener(gameListener1)
        val gameListener2 = TestGameListener(game)
        game.addOnGameChangeListener(gameListener2)
        game.addOnGameOverListener(gameListener2)


        val origLines = game.lines.toList()
        // Pick a random line not to play yet
        var lastGameChangeCount = -1
        val lineCoordinatesToPlay = origLines.asSequence()
            .map { it.coordinates }
            .toMutableList()
            .apply { shuffle(rnd) }

        // Remove the last coordinate from the list so we can play it outside
        // of the loop. (and do extra checks)
        val lastCoordinateToPlay = lineCoordinatesToPlay.removeAt(lineCoordinatesToPlay.size - 1)

        for (lineCoord in lineCoordinatesToPlay) {
            val line = game.lines[lineCoord]
            assertFalse(line.isDrawn, "Lines should not just turn up as drawn as the coordinates for lines are supposed to be unique") // It can not have been drawn yet

            // record the current player to check complete box ownership
            val player = game.currentPlayer
            line.drawLine() // make the move

            assertFalse(game.isFinished, "Until the last line has been drawn, the game cannot be complete/finished") // The game cannot be finished
            /*
             * If this completed a box, then the owning player must be the current player,
             * otherwise it must be `null`
             */
            for (neighbor in game.lines[lineCoord].adjacentBoxes) {
                if (neighbor.boundingLines.all(Line::isDrawn)) {
                    assertEquals(player, neighbor.owningPlayer, "If a neighboring box of a just drawn line has all its lines drawn, the owner of the box should be the player that was the current player before calling drawLine")
                } else {
                    assertNull(neighbor.owningPlayer, "If a box doesn't have all its lines drawn it should not have an owner")
                }
            }
            // Check that no gameover listener was called
            assertFalse(gameListener2.onGameOverCalled, "The game isn't finished, so gameOver should not have been called on listener 1")
            assertFalse(gameListener1.onGameOverCalled, "The game isn't finished, so gameOver should not have been called on listener 2")
            // Check that onGameChange was called
            assertNotEquals(lastGameChangeCount, gameListener1.onGameChangeCalled, "onGameChange should have been called on listener 1")
            assertNotEquals(lastGameChangeCount, gameListener2.onGameChangeCalled, "onGameChange should have been called on listener 2")
            assertEquals(gameListener1.onGameChangeCalled, gameListener2.onGameChangeCalled, "both game change listeners should have been called with the same frequency")
            lastGameChangeCount = gameListener1.onGameChangeCalled
        }

        val lastLineToPlay = game.lines[lastCoordinateToPlay]
        // Check that all boxes except the ones adjacent to the last line are complete.
        val adjacents = lastLineToPlay.adjacentBoxes.run { first?.coordinates to second?.coordinates }
        for (box in game.boxes) {
            if (box.coordinates in adjacents) {
                assertEquals(1, box.boundingLines.count { !it.isDrawn }, "The boxes adjacent to the last undrawn line should only have 1 undrawn line")
                assertNull(box.owningPlayer, "THe boxes adjacent to the last undrawn line should not have an owner")
            } else {
                assertTrue(box.boundingLines.all { it.isDrawn }, "A box not adjacent to the last undrawn line should have all its lines drawn")
                assertNotNull(box.owningPlayer, "A box not adjacent to the last undrawn line should have an owning player")
            }
        }

        // Record the current player to be able to check box ownership later
        val lastPlayerToPlay = game.currentPlayer

        // Randomly decide which of the two listeners to drop, just to ensure correctness.
        val isDropFirstListener = rnd.nextBoolean()
        if (isDropFirstListener) {
            game.removeOnGameOverListener(gameListener1)
            game.removeOnGameChangeListener(gameListener1)
        } else {
            game.removeOnGameOverListener(gameListener2)
            game.removeOnGameChangeListener(gameListener2)
        }

        lastLineToPlay.drawLine()
        assertTrue(game.isFinished, "After drawing the last undrawn line the game should be finished.")

        // Depending on which listener was dropped we check the stats on the listener being called
        if (isDropFirstListener) {
            assertEquals(lastGameChangeCount, gameListener1.onGameChangeCalled, "When listener 1 is unregistered, it should not be called on the last game play")
            assertNotEquals(lastGameChangeCount, gameListener2.onGameChangeCalled, "When listener 1 is unregistered, listener 2 should still be informed of game change on the last play")
            assertFalse(gameListener1.onGameOverCalled, "When listener 1 is unregistered, it should not be informed of game completion")
            assertTrue(gameListener2.onGameOverCalled, "When listener 1 is unregistered, listener 2 should still be informed of game completion")
        } else {
            assertNotEquals(lastGameChangeCount, gameListener1.onGameChangeCalled, "When listener 2 is unregistered, listener 1 should still be informed of game change on the last play")
            assertEquals(lastGameChangeCount, gameListener2.onGameChangeCalled, "When listener 2 is unregistered, it should not be called on the last game play")
            assertTrue(gameListener1.onGameOverCalled, "When listener 2 is unregistered, listener 1 should still be informed of game completion")
            assertFalse(
                gameListener2.onGameOverCalled,
                "On game over called even though the second listener should have been removed"
                       )
        }
        // One of the listeners should not have been called
        assertNotEquals(gameListener1.onGameChangeCalled, gameListener2.onGameChangeCalled, "The two listeners should not have been called about game changes the same amount of time")

        // Of course all boxes adjacent to the last line should be owned by the last player
        // and all the lines should be drawn
        for (c in adjacents) {
            val b = game.boxes[c]
            assertEquals(lastPlayerToPlay, b.owningPlayer, "The boxes adjacent to the last line should have the last player as owner, box $b at $c does not")
            assertTrue(b.boundingLines.all(Line::isDrawn), "All bounding lines of the adjacent boxes to the last line should be drawn")
        }

        // In general all lines should be drawn and all boxes owned
        assertTrue(game.lines.all(Line::isDrawn), "All lines in the game/returned by DotsAndBoxesGame.lines should be drawn as the game is finished")
        assertTrue(game.boxes.all { it.owningPlayer != null }, "All boxes in the game/returned by DotsAndBoxesGame.boxes should have an owner")
    }

    /**
     * Test that computer turns get triggered automatically, and correctly.
     */
    @ParameterizedTest(name = "size = ({0}, {1}, #{index})")
    @MethodSource("gameMoveData")
    fun testComputerGame(width: Int, height: Int, rnd: Random) {
        val computerPlayer = TestComputerPlayer()
        val players = listOf(HumanPlayer(), computerPlayer)
        val game = createGame(width, height, players)

        // Pick a random line not to play yet
        var lastGameChangeCount = -1
        val lineCoordinatesToPlayIterator = game.lines.asSequence()
            .map { it.coordinates }
            .toMutableList()
            .apply { shuffle(rnd) }
            .iterator()

        computerPlayer.moveIterator = lineCoordinatesToPlayIterator
        if (game.currentPlayer is ComputerPlayer) {
            game.playComputerTurns()
        }
        var humanTurns = 0

        while (lineCoordinatesToPlayIterator.hasNext()) {
            assertEquals(players[0], game.currentPlayer)
            val drawnLines = game.lines.count { it.isDrawn }
            val lineToPlay = game.lines[lineCoordinatesToPlayIterator.next()]
            val oldComputerTurns = computerPlayer.computerTurns

            // We must record these here as playing a line as human may trigger the
            // computer player, that could be completing the box itself.
            val boxesThatWillComplete = mutableListOf<Coordinate<Box>>()
            for (b in lineToPlay.adjacentBoxes) {
                assertNull(b.owningPlayer, "The adjacent boxes to a line to be played should not have an owner")
                if (b.boundingLines.count { !it.isDrawn } == 1) {
                    boxesThatWillComplete.add(b.coordinates)
                }
            }
            lineToPlay.drawLine()
            humanTurns++

            var didCompleteBox = false
            for (bcoord in boxesThatWillComplete) {
                val b = game.boxes[bcoord]
                // Check box owners
                if (b.boundingLines.all { it.isDrawn }) {
                    didCompleteBox = true
                    assertEquals(players[0], b.owningPlayer, "For all boxes that had 1 undrawn line, check that they are now owned by the human player")
                } else {
                    fail("Boxes that have a line drawn and only had 1 undrawn line should never not have all lines drawn afterwards")
                    assertNull(b.owningPlayer, "If boxes are not drawn they should not have an owner")
                }
            }

            if (didCompleteBox) {
                // We drew a box, no computer turn
                assertEquals(oldComputerTurns, computerPlayer.computerTurns, "If the human player completes a box, the computer player should not get a turn")
                assertEquals(drawnLines + 1, game.lines.count { it.isDrawn }, "the amount of lines expected to be drawn should equal the amount of lines actually drawn")
            } else {
                // Computer gets one or multiple turns
                assertTrue(oldComputerTurns < computerPlayer.computerTurns, "The amount of computer turns should be at least 1 more than previously")
                assertTrue(drawnLines + 1 < game.lines.count { it.isDrawn }, "The amount of actually drawn line needs to be at least 2 more than previous (1 human turn, 1+ computer turns)")
            }
        }
        assertEquals(game.lines.count(), humanTurns + computerPlayer.computerTurns, "The total amount of turns by the computer + the total amount of turns by the \"human\" should equal the actual amount of lines")
    }


    companion object {
        /**
         * Helper method that returns a list of game sizes that JUnit will provide as parameters
         */
        @Suppress("unused")
        @JvmStatic
        fun gameSizes(): List<Array<Int>> = listOf(
            arrayOf(1, 1),
            arrayOf(1, 5),
            arrayOf(5, 1),
            arrayOf(5, 5),
            arrayOf(8, 8),
            arrayOf(10, 10)
                                                  )

        /**
         * Helper method that returns a list of game sizes plus random generators to use.
         * The random generator is seeded with a fixed seed random. This allows for repeatable
         * tests that nonetheless appear random. Note that it will create a number of
         * random generators for each size to allow for different random walks.
         */
        @Suppress("unused")
        @JvmStatic
        fun gameMoveData(): List<Array<Any>> {
            val seedSource = Random(0x12345678)
            val sizes = sequenceOf(
                arrayOf(1, 1, 4),
                arrayOf(1, 5, 10),
                arrayOf(5, 1, 10),
                arrayOf(5, 5, 20),
                arrayOf(8, 8, 30),
                arrayOf(10, 10, 40)
                                  )

            return sizes.flatMap { size ->
                (1..size[2]).asSequence().map { arrayOf(size[0], size[1], Random(seedSource.nextInt())) }
            }.toList()
        }
    }

}

/**
 * Helper to get a line with a given coordinate. Shortcircuit for SparseMatrix as it can do
 * index access rather than looping over all elements.
 */
operator fun Iterable<Line>.get(c: Coordinate<Line>) =
    (this as? SparseMatrix)?.get(c.x, c.y) ?: single { it.lineX == c.x && it.lineY == c.y }

/**
 * Helper to get a box with a given coordinate. Shortcircuit for SparseMatrix as it can do
 * index access rather than looping over all elements.
 */
operator fun Iterable<Box>.get(c: Coordinate<Box>) =
    (this as? SparseMatrix)?.get(c.x, c.y) ?: single { it.boxX == c.x && it.boxY == c.y }

/**
 * Simple helper that will allow iterating over a pair of elements skipping nulls.
 */
operator fun <T : Any> Pair<T?, T?>.iterator(): Iterator<T> = when {
    first == null && second == null -> emptyList()
    first == null                   -> listOf(second!!)
    second == null                  -> listOf(first!!)
    else                            -> listOf(first!!, second!!)
}.iterator()

/**
 * Simple helper to check that one of the values is the given value. It allows for the `x in y` syntax
 * to be used.
 */
operator fun <T : Any> Pair<T?, T?>.contains(value: T) = first == value || second == value

/**
 * Two boxes are the "same" if they have the same coordinates
 */
fun Box?.isSame(other: Box?) = when (this) {
    null -> other == null
    else -> (other != null && boxX == other.boxX && boxY == other.boxY)
}

/**
 * Two lines are the "same" if they have the same coordinates
 */
fun Line?.isSame(other: Line?) = when (this) {
    null -> other == null
    else -> (other != null && lineX == other.lineX && lineY == other.lineY)
}

/**
 * Two pairs of boxes are equivalent if they contain boxes with the same
 * coordinates, independent of order.
 */
fun Pair<Box?, Box?>.equiv(other: Pair<Box?, Box?>): Boolean {
    return (first.isSame(other.first) && second.isSame(other.second)) ||
            (second.isSame(other.first) && first.isSame(other.second))

}

/** Helper to get coordinates for a box */
val Box.coordinates get() = (this as? AbstractBox)?.pos ?: Coordinate(boxX, boxY)

/** Helper to get coordinates for a line */
val Line.coordinates get() = (this as? AbstractLine)?.pos ?: Coordinate(lineX, lineY)

/**
 * A very simple computer player. It uses an iterator over moves that is expected to be
 * shared with the "human" player. Do not implement your own computer player that way as
 * it cannot share moves with the actual human.
 *
 * Obviously there is also a lot of testing code in here.
 */
class TestComputerPlayer : ComputerPlayer() {

    var computerTurns = 0

    /** The test player will just take the next coordinate from the iterator.
     * This way the game will be identical for computer and human players
     */
    lateinit var moveIterator: Iterator<Coordinate<Line>>

    /**
     * This implementation just takes the next line from the iterator and makes the move/draws
     * the line.
     *
     * In addition it checks that it is checked, and that if it completes a box, the box is then
     * set to be owned by this computer player.
     */
    override fun makeMove(game: DotsAndBoxesGame) {
        assertTrue(moveIterator.hasNext(), "There should still be a move to play when the computer player is asked to make a move (was isFinished updated correctly?)")
        val line = game.lines[moveIterator.next()]
        line.drawLine()
        assertTrue(game.lines[line.coordinates].isDrawn, "After the computer draws a line, the line at that coordinate should be drawn")
        computerTurns++
        for (b in line.adjacentBoxes) {
            if (b.boundingLines.all { it.isDrawn }) {
                assertEquals(this, b.owningPlayer, "If the computer move completed a box, the computer player should be the owner")
            } else {
                assertNull(b.owningPlayer, "If the computer move didn't complete a box, the box should not have an owner")
            }
        }
    }
}

/**
 * Game listener implementation that counts the invocation count (for game change) and makes sure
 * that game over is only called once.
 */
class TestGameListener<G : DotsAndBoxesGame>(val expectedGame: G) : DotsAndBoxesGame.GameChangeListener,
                                                                    DotsAndBoxesGame.GameOverListener {

    var onGameChangeCalled = 0
    var onGameOverCalled = false

    override fun onGameOver(game: DotsAndBoxesGame, scores: List<Pair<Player, Int>>) {
        assertEquals(expectedGame, game, "The game passed to the listener should be the game that was created for the test")
        assertEquals(false, onGameOverCalled, "A game can not be over twice")
        onGameOverCalled = true
    }

    override fun onGameChange(game: DotsAndBoxesGame) {
        assertEquals(expectedGame, game, "The game passed to the listener should be the game created for the test")
        onGameChangeCalled++
    }
}