package com.qurafy.hamraj37.data.repository

import com.qurafy.hamraj37.data.model.GitHubRelease
import com.qurafy.hamraj37.data.model.UpdateInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class GitHubUpdateChecker {

    private val repoOwner = "Hamraj37"
    private val repoName = "Qurefy"

    fun isVersionNewer(latestTag: String, currentVersion: String): Boolean {
        val cleanLatest = latestTag.trim().removePrefix("v").removePrefix("V")
        val cleanCurrent = currentVersion.trim().removePrefix("v").removePrefix("V")

        val latestParts = cleanLatest.split(".").mapNotNull { it.takeWhile { char -> char.isDigit() }.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.takeWhile { char -> char.isDigit() }.toIntOrNull() }

        val maxLength = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLength) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val url = "https://api.github.com/repos/$repoOwner/$repoName/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "Qurafy-Android-App")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyString = response.body?.string() ?: return@withContext null

                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter(GitHubRelease::class.java)
                val release = adapter.fromJson(bodyString) ?: return@withContext null

                val hasUpdate = isVersionNewer(release.tagName, currentVersionName)
                if (!hasUpdate) return@withContext null

                val apkAsset = release.assets.firstOrNull {
                    it.name.contains("release", ignoreCase = true) && it.name.endsWith(".apk", ignoreCase = true)
                } ?: release.assets.firstOrNull {
                    !it.name.contains("debug", ignoreCase = true) && it.name.endsWith(".apk", ignoreCase = true)
                } ?: release.assets.firstOrNull {
                    it.name.endsWith(".apk", ignoreCase = true)
                }
                val downloadUrl = apkAsset?.browserDownloadUrl ?: release.htmlUrl

                return@withContext UpdateInfo(
                    hasUpdate = true,
                    latestVersion = release.tagName.removePrefix("v").removePrefix("V"),
                    currentVersion = currentVersionName,
                    releaseName = release.name ?: "Qurafy ${release.tagName}",
                    releaseNotes = release.body ?: "A new update is available on GitHub.",
                    downloadUrl = downloadUrl,
                    releaseUrl = release.htmlUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
