/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: This file copied from the Android CTS Tests
 */
package tech.salroid.filmy.utility

import junit.framework.Assert
import tech.salroid.filmy.utility.PollingCheck
import java.lang.Exception
import java.util.concurrent.Callable
import kotlin.Throws

abstract class PollingCheck(timeout: Long) {

    private var mTimeout: Long = timeout
    protected abstract fun check(): Boolean

    fun run() {
        if (check()) {
            return
        }
        var timeout = mTimeout
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE)
            } catch (e: InterruptedException) {
                Assert.fail("unexpected InterruptedException")
            }
            if (check()) {
                return
            }
            timeout -= TIME_SLICE
        }
        Assert.fail("unexpected timeout")
    }

    companion object {
        private const val TIME_SLICE: Long = 50

        @Throws(Exception::class)
        fun check(message: CharSequence, timeout: Long, condition: Callable<Boolean>) {
            var timeout = timeout
            while (timeout > 0) {
                if (condition.call()) {
                    return
                }
                Thread.sleep(TIME_SLICE)
                timeout -= TIME_SLICE
            }
            Assert.fail(message.toString())
        }
    }
}