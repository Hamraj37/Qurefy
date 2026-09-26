package com.qurafy.hamraj37

import com.qurafy.hamraj37.data.repository.GitHubUpdateChecker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GitHubUpdateCheckerTest {

    private lateinit var checker: GitHubUpdateChecker

    @Before
    fun setUp() {
        checker = GitHubUpdateChecker()
    }

    @Test
    fun testIsVersionNewerWithHigherPatchVersion() {
        assertTrue(checker.isVersionNewer("v1.0.3", "1.0.2"))
        assertTrue(checker.isVersionNewer("1.0.3", "1.0.2"))
    }

    @Test
    fun testIsVersionNewerWithHigherMinorVersion() {
        assertTrue(checker.isVersionNewer("v1.1.0", "1.0.2"))
        assertTrue(checker.isVersionNewer("1.1.0", "1.0.2"))
    }

    @Test
    fun testIsVersionNewerWithHigherMajorVersion() {
        assertTrue(checker.isVersionNewer("v2.0.0", "1.9.9"))
    }

    @Test
    fun testIsVersionNewerWithSameVersion() {
        assertFalse(checker.isVersionNewer("v1.0.2", "1.0.2"))
        assertFalse(checker.isVersionNewer("1.0.2", "1.0.2"))
    }

    @Test
    fun testIsVersionNewerWithOlderVersion() {
        assertFalse(checker.isVersionNewer("v1.0.1", "1.0.2"))
        assertFalse(checker.isVersionNewer("1.0.0", "1.0.2"))
    }
}
