package uk.ac.bournemouth.ap.dotsandboxeslib.test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
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
        assertEquals(width - 1, lastX)
    }

    /**
     * Test that game box coordinates horizontally are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMinWidth(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstX = game.boxes.minBy { it.boxX }?.boxX
        assertEquals(0, firstX)
    }

    /**
     * Test that game box coordinates vertically are in line with game height.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMaxHeight(width: Int, height: Int) {
        val game = createGame(width, height)
        val lastY = game.boxes.maxBy { it.boxY }?.boxY
        assertEquals(height - 1, lastY)
    }

    /**
     * Test that game box coordinates vertically are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameMinHeight(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstY = game.boxes.minBy { it.boxY }?.boxY
        assertEquals(0, firstY)
    }

    /**
     * Test that horizontal line coordinates are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameLineIndexMinX(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstX = game.lines.minBy { it.lineX }?.lineX
        assertNotNull(firstX)
        assertTrue(firstX!! >= 0, "The lowest line index is at least 0")
    }

    /**
     * Test that vertical line coordinates are positive.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGameLineIndexMinY(width: Int, height: Int) {
        val game = createGame(width, height)
        val firstY = game.lines.minBy { it.lineY }?.lineY
        assertNotNull(firstY)
        assertTrue(firstY!! >= 0, "The lowest line index is at least 0")
    }

    /**
     * Test that by default a game has 2 players.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testGamePlayers(width: Int, height: Int) {
        val game = createGame(width, height)
        assertEquals(2, game.players.size)
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
        assertEquals(players, game.players)
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
            "The player list in the game should be a copy"
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
            assertTrue(line.adjacentBoxes.toList().size in 1..2)
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
                assertTrue(c in expectedBoxes)
                seenBoxes.add(c)

            }
        }
        assertEquals(expectedBoxes.size, seenBoxes.size)
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
                assertTrue(c in expectedLines)
                seenLines.add(c)
            }
        }
        assertEquals(expectedLines.size, seenLines.size) {
            val missingLines = expectedLines.toMutableSet()
            missingLines.removeAll(seenLines)
            "Expected lines: $missingLines"
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
                    "Line at coordinate ${line.coordinates} occurs multiple times in box ${box}"
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
                "Box at coordinate ${box.coordinates} occurs multiple times in the game"
                      )
        }
    }

    /**
     * Test that all neighbor boxes returned as neighbors to any line are also returned from `game.boxes`,
     * and that all boxes are a neighbor of at least one line.
     */
    @ParameterizedTest(name = "size = ({0}, {1})")
    @MethodSource("gameSizes")
    fun testAllKnownNeighbors(width: Int, height: Int) {
        val game = createGame(width, height)
        val allBoxCoordinates = game.boxes.asSequence().map(Box::coordinates).toSet()
        val neighborCoordinates = mutableSetOf<Coordinate<Box>>()
        for (line in game.lines) {
            for (n in line.adjacentBoxes) {
                val coordinates = n.coordinates
                // Check that all neighbor boxes
                assertTrue(coordinates in allBoxCoordinates)
                neighborCoordinates.add(coordinates)
            }
        }
        assertEquals(allBoxCoordinates.size, neighborCoordinates.size)
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
                            1 -> { }
                            else -> fail<Unit>("multiple lines found ${sameLines}")
                        }
                        assertEquals(line.coordinates, sameLines.single().coordinates)
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
            assertTrue(line.isDrawn)
            assertThrows<Exception> {
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
        assertEquals(false, origLines[lineToPlay.coordinates].isDrawn)
        // The player before drawing
        val origPlayer = game.currentPlayer

        lineToPlay.drawLine()

        // The amount of lines in the game should not have changed.
        assertEquals(origLines.size, game.lines.count())

        // The line that was just played. There is no requirement for this to be the same object
        // as the `lineToPlay` - or for `lineToPlay` to still be valid so we find it again.
        val newLine = game.lines[lineToPlay.coordinates]

        // The reloaded line needs to be drawn
        assertEquals(true, newLine.isDrawn)
        // There should be exactly one drawn line in th game
        assertEquals(1, game.lines.count { it.isDrawn })
        // Playing the single line cannot complete a box, so no repeat turns. New player is needed
        assertNotEquals(origPlayer, game.currentPlayer)
        // Check that the playing triggered the listener exactly once for a game state change
        assertEquals(1, gameListener.onGameChangeCalled)
        // Check that the game over listener was not calle
        assertFalse(gameListener.onGameOverCalled)
        // Check that none of the boxes next to the line has become complete/gotten an owner
        for (b in newLine.adjacentBoxes) {
            assertNull(b.owningPlayer)
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
                val lineFromBox = box.boundingLines.single { it.coordinates == lineCoordinate }
                assertNull(box.owningPlayer)
                assertEquals(lineCoordinate, lineFromGame.coordinates)
                assertEquals(lineCoordinate, lineFromBox.coordinates)
                assertFalse(lineFromBox.isDrawn)
                assertFalse(lineFromGame.isDrawn)
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
                assertTrue(lineFromBox.isDrawn)
                assertTrue(lineFromGame.isDrawn)
            }
        }
        run {
            val playedBox = game.boxes[targetCoordinate]
            assertNotNull(playedBox.owningPlayer)
            for (line in playedBox.boundingLines) {
                assertTrue(line.isDrawn)
            }
            for (lineCoordinate in lineCoordinatesToPlay) {
                assertTrue(game.lines[lineCoordinate].isDrawn)
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
        assertEquals(1, gameListener1.onGameChangeCalled)
        assertEquals(1, gameListener2.onGameChangeCalled)
        // Game over should not have been called
        assertFalse(gameListener2.onGameOverCalled)

        run {
            // When removing listener 2 it should not be updated when playing
            // another random line, but listener 1 should.

            game.removeOnGameChangeListener(gameListener2)
            val nextLineToPlay = game.lines.filter { !it.isDrawn }.random(rnd)
            nextLineToPlay.drawLine()
            assertEquals(2, gameListener1.onGameChangeCalled)
            assertEquals(1, gameListener2.onGameChangeCalled)
        }

        run {
            // When reregistering listener 2 and removing listener 1, listener 2 should
            // be notified, but listener 1 not.
            game.addOnGameChangeListener(gameListener2)
            game.removeOnGameChangeListener(gameListener1)
            val nextLineToPlay = game.lines.filter { !it.isDrawn }.random(rnd)
            nextLineToPlay.drawLine()
            assertEquals(2, gameListener1.onGameChangeCalled)
            assertEquals(2, gameListener2.onGameChangeCalled)
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
        assertFalse(game.isFinished)

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
            assertFalse(line.isDrawn) // It can not have been drawn yet

            // record the current player to check complete box ownership
            val player = game.currentPlayer
            line.drawLine() // make the move

            assertFalse(game.isFinished) // The game cannot be finished
            /*
             * If this completed a box, then the owning player must be the current player,
             * otherwise it must be `null`
             */
            for (neighbor in game.lines[lineCoord].adjacentBoxes) {
                if (neighbor.boundingLines.all(Line::isDrawn)) {
                    assertEquals(player, neighbor.owningPlayer)
                } else {
                    assertNull(neighbor.owningPlayer)
                }
            }
            // Check that no gameover listener was called
            assertFalse(gameListener2.onGameOverCalled)
            assertFalse(gameListener1.onGameOverCalled)
            // Check that onGameChange was called
            assertNotEquals(lastGameChangeCount, gameListener1.onGameChangeCalled)
            assertNotEquals(lastGameChangeCount, gameListener2.onGameChangeCalled)
            assertEquals(gameListener1.onGameChangeCalled, gameListener2.onGameChangeCalled)
            lastGameChangeCount = gameListener1.onGameChangeCalled
        }

        val lastLineToPlay = game.lines[lastCoordinateToPlay]
        // Check that all boxes except the ones adjacent to the last line are complete.
        val adjacents = lastLineToPlay.adjacentBoxes.run { first?.coordinates to second?.coordinates }
        for (box in game.boxes) {
            if (box.coordinates in adjacents) {
                assertEquals(1, box.boundingLines.count { !it.isDrawn })
                assertNull(box.owningPlayer)
            } else {
                assertTrue(box.boundingLines.all { it.isDrawn })
                assertNotNull(box.owningPlayer)
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
        assertTrue(game.isFinished)

        // Depending on which listener was dropped we check the stats on the listener being called
        if (isDropFirstListener) {
            assertEquals(lastGameChangeCount, gameListener1.onGameChangeCalled)
            assertNotEquals(lastGameChangeCount, gameListener2.onGameChangeCalled)
            assertFalse(gameListener1.onGameOverCalled)
            assertTrue(gameListener2.onGameOverCalled)
        } else {
            assertNotEquals(lastGameChangeCount, gameListener1.onGameChangeCalled)
            assertEquals(lastGameChangeCount, gameListener2.onGameChangeCalled)
            assertTrue(gameListener1.onGameOverCalled)
            assertFalse(
                gameListener2.onGameOverCalled,
                "On game over called even though the second listener should have been removed"
                       )
        }
        // One of the listeners should not have been called
        assertNotEquals(gameListener1.onGameChangeCalled, gameListener2.onGameChangeCalled)

        // Of course all boxes adjacent to the last line should be owned by the last player
        // and all the lines should be drawn
        for (c in adjacents) {
            val b = game.boxes[c]
            assertEquals(lastPlayerToPlay, b.owningPlayer)
            assertTrue(b.boundingLines.all(Line::isDrawn))
        }

        // In general all lines should be drawn and all boxes owned
        assertTrue(game.lines.all(Line::isDrawn))
        assertTrue(game.boxes.all { it.owningPlayer != null })
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

        while(lineCoordinatesToPlayIterator.hasNext()) {
            assertEquals(players[0], game.currentPlayer)
            val drawnLines = game.lines.count { it.isDrawn }
            val lineToPlay = game.lines[lineCoordinatesToPlayIterator.next()]
            val oldComputerTurns = computerPlayer.computerTurns

            // We must record these here as playing a line as human may trigger the
            // computer player, that could be completing the box itself.
            val boxesThatWillComplete = mutableListOf<Coordinate<Box>>()
            for (b in lineToPlay.adjacentBoxes) {
                assertNull(b.owningPlayer)
                if (b.boundingLines.count { !it.isDrawn } ==1) {
                    boxesThatWillComplete.add(b.coordinates)
                }
            }
            lineToPlay.drawLine()
            humanTurns++

            var didCompleteBox = false
            for(bcoord in boxesThatWillComplete) {
                val b = game.boxes[bcoord]
                // Check box owners
                if (b.boundingLines.all { it.isDrawn } ) {
                    didCompleteBox = true
                    assertEquals(players[0], b.owningPlayer)
                } else {
                    assertNull(b.owningPlayer)
                }
            }

            if (didCompleteBox) {
                // We drew a box, no computer turn
                assertEquals(oldComputerTurns, computerPlayer.computerTurns)
                assertEquals(drawnLines+1, game.lines.count { it.isDrawn })
            } else {
                // Computer gets one or multiple turns
                assertTrue(oldComputerTurns< computerPlayer.computerTurns)
                assertTrue(drawnLines+1< game.lines.count { it.isDrawn })
            }
        }
        assertEquals(game.lines.count(), humanTurns+computerPlayer.computerTurns)
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
        assertTrue(moveIterator.hasNext())
        val line = game.lines[moveIterator.next()]
        line.drawLine()
        assertTrue(game.lines[line.coordinates].isDrawn)
        computerTurns++
        for (b in line.adjacentBoxes) {
            if (b.boundingLines.all { it.isDrawn }) {
                assertEquals(this, b.owningPlayer)
            } else {
                assertNull(b.owningPlayer)
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
        assertEquals(expectedGame, game)
        assertEquals(false, onGameOverCalled, "A game can not be over twice")
        onGameOverCalled = true
    }

    override fun onGameChange(game: DotsAndBoxesGame) {
        assertEquals(expectedGame, game)
        onGameChangeCalled++
    }
}