package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [InMemoryPlayerLocalDataSource] (pure JVM, no Android shadows).
 */
class InMemoryPlayerLocalDataSourceTest {

    private lateinit var dataSource: InMemoryPlayerLocalDataSource

    /**
     * Creates a fresh [InMemoryPlayerLocalDataSource] so list mutations in one test do not leak to the next.
     */
    @Before
    fun setUp() {
        dataSource = InMemoryPlayerLocalDataSource()
    }

    /**
     * Verifies `addPlayer_thenGetPlayers_containsPlayer` behavior.
     */
    @Test
    fun addPlayer_thenGetPlayers_containsPlayer() {
        val p = Player("A", arrayListOf())

        dataSource.addPlayer(p)

        assertEquals(1, dataSource.getPlayers().size)
        assertSame(p, dataSource.getPlayers()[0])
    }

    /**
     * Verifies `deletePlayer_removesAtIndex` behavior.
     */
    @Test
    fun deletePlayer_removesAtIndex() {
        dataSource.addPlayer(Player("1", arrayListOf()))
        dataSource.addPlayer(Player("2", arrayListOf()))

        dataSource.deletePlayer(0)

        assertEquals(1, dataSource.getPlayers().size)
        assertEquals("2", dataSource.getPlayers()[0].name)
    }

    /**
     * Verifies `updatePlayer_replacesAtIndex` behavior.
     */
    @Test
    fun updatePlayer_replacesAtIndex() {
        dataSource.addPlayer(Player("old", arrayListOf()))
        val next = Player("new", arrayListOf())

        dataSource.updatePlayer(0, next)

        assertSame(next, dataSource.getPlayer(0))
    }

    /**
     * Verifies `getPlayer_returnsElementAtIndex` behavior.
     */
    @Test
    fun getPlayer_returnsElementAtIndex() {
        val p = Player("x", arrayListOf())
        dataSource.addPlayer(p)

        assertSame(p, dataSource.getPlayer(0))
    }
}
