package com.example.businesscardapp

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(EXPECTED_SUM, FIRST_ADDEND + SECOND_ADDEND)
    }

    private companion object {
        const val EXPECTED_SUM = 4
        const val FIRST_ADDEND = 2
        const val SECOND_ADDEND = 2
    }
}
