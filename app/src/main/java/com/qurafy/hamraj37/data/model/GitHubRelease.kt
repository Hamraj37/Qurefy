package com.qurafy.hamraj37.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubAsset(
    @Json(name = "name") val name: String,
    @Json(name = "browser_download_url") val browserDownloadUrl: String
)

@JsonClass(generateAdapter = true)
data class GitHubRelease(
    @Json(name = "tag_name") val tagName: String,
    @Json(name = "name") val name: String? = null,
    @Json(name = "body") val body: String? = null,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "assets") val assets: List<GitHubAsset> = emptyList()
)

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val releaseName: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val releaseUrl: String
)
