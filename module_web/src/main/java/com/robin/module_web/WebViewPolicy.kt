package com.robin.module_web

import java.net.URI
import java.net.URISyntaxException

/**
 * Declares the trusted HTTPS origins and optional capabilities available to WebView content.
 *
 * All capabilities are denied by default. Callers must supply an explicit policy for a
 * trusted origin before JavaScript, JavaScript bridge, geolocation, or file selection is used.
 */
data class WebViewPolicy(
    val trustedOrigins: Set<TrustedWebOrigin> = emptySet(),
    val allowJavaScript: Boolean = false,
    val allowJavaScriptBridge: Boolean = false,
    val allowGeolocation: Boolean = false,
    val allowFileChooser: Boolean = false
) {
    /** Returns whether this policy permits the supplied HTTPS URL. */
    fun isTrusted(url: String?): Boolean = trustedOrigin(url) != null

    /** Returns whether JavaScript may be enabled for the supplied URL. */
    fun allowsJavaScript(url: String?): Boolean = allowJavaScript && isTrusted(url)

    /** Returns whether a JavaScript bridge may be exposed to the supplied URL. */
    fun allowsJavaScriptBridge(url: String?): Boolean =
        allowJavaScriptBridge && allowsJavaScript(url)

    /** Returns whether geolocation may be granted to the supplied URL. */
    fun allowsGeolocation(url: String?): Boolean = allowGeolocation && isTrusted(url)

    /** Returns whether file selection may be initiated by the supplied URL. */
    fun allowsFileChooser(url: String?): Boolean = allowFileChooser && isTrusted(url)

    private fun trustedOrigin(url: String?): TrustedWebOrigin? {
        val uri = try {
            url?.let(::URI)
        } catch (_: URISyntaxException) {
            null
        } ?: return null
        return trustedOrigins.firstOrNull { it.matches(uri) }
    }

    companion object {
        /** Safest default: no remote origin receives privileged WebView capabilities. */
        val DEFAULT = WebViewPolicy()
    }
}

/** Exact HTTPS origin matcher. A configured port is matched exactly; otherwise default HTTPS port is used. */
data class TrustedWebOrigin(
    val host: String,
    val port: Int? = null
) {
    init {
        require(host.isNotBlank()) { "Trusted origin host must not be blank." }
    }

    fun matches(uri: URI): Boolean {
        if (!"https".equals(uri.scheme, ignoreCase = true)) {
            return false
        }
        if (!host.equals(uri.host, ignoreCase = true)) {
            return false
        }
        val expectedPort = port ?: HTTPS_DEFAULT_PORT
        val actualPort = if (uri.port == NO_PORT) HTTPS_DEFAULT_PORT else uri.port
        return expectedPort == actualPort
    }

    private companion object {
        const val HTTPS_DEFAULT_PORT = 443
        const val NO_PORT = -1
    }
}
