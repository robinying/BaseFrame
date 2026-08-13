package com.robin.baseframe.ui.home

import java.net.URI

/**
 * Parses internal demo deep links of the form `baseframe://demo/{demoId}`.
 *
 * Only catalog IDs are accepted so unrecognized or malformed links safely remain on the catalog.
 */
object DemoDeepLink {
    const val SCHEME = "baseframe"
    const val HOST = "demo"

    fun parseDemoId(rawUri: String?): String? {
        val uri = try {
            rawUri?.let(::URI)
        } catch (error: IllegalArgumentException) {
            null
        } ?: return null
        if (uri.scheme != SCHEME || uri.host != HOST) {
            return null
        }

        val pathSegments = uri.path.orEmpty().split('/').filter { it.isNotBlank() }
        val demoId = pathSegments.singleOrNull() ?: return null
        return demoId.takeIf { candidate -> DemoCatalog.items.any { it.id == candidate } }
    }
}
