package tech.salroid.filmy

import org.junit.Assert
import org.junit.Test
import java.lang.Exception
import kotlin.Throws

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        Assert.assertEquals("Equals", 4, (2 + 2).toLong())
    }
}