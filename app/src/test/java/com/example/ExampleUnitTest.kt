package com.example

import com.example.data.StickyColor
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `sticky colors resolve properly`() {
        assertEquals(StickyColor.YELLOW, StickyColor.fromName("YELLOW"))
        assertEquals(StickyColor.PINK, StickyColor.fromName("PINK"))
        assertEquals(StickyColor.GREEN, StickyColor.fromName("GREEN"))
        assertEquals(StickyColor.YELLOW, StickyColor.fromName("UNKNOWN_COLOR"))
    }
}
