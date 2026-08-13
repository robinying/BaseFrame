package com.robin.module_web

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WebViewPolicyTest {

    @Test
    fun defaultPolicyDeniesAllOriginsAndCapabilities() {
        val url = "https://trusted.example.com"

        assertFalse(WebViewPolicy.DEFAULT.isTrusted(url))
        assertFalse(WebViewPolicy.DEFAULT.allowsJavaScript(url))
        assertFalse(WebViewPolicy.DEFAULT.allowsJavaScriptBridge(url))
        assertFalse(WebViewPolicy.DEFAULT.allowsGeolocation(url))
        assertFalse(WebViewPolicy.DEFAULT.allowsFileChooser(url))
    }

    @Test
    fun trustedOriginAcceptsOnlyMatchingHttpsHostOnDefaultPort() {
        val policy = WebViewPolicy(
            trustedOrigins = setOf(TrustedWebOrigin("trusted.example.com"))
        )

        assertTrue(policy.isTrusted("https://trusted.example.com/path"))
        assertTrue(policy.isTrusted("HTTPS://TRUSTED.EXAMPLE.COM:443/path"))
        assertFalse(policy.isTrusted("https://trusted.example.com:8443/path"))
        assertFalse(policy.isTrusted("https://untrusted.example.com/path"))
        assertFalse(policy.isTrusted("https://trusted.example.com.evil.example/path"))
        assertFalse(policy.isTrusted("http://trusted.example.com/path"))
    }

    @Test
    fun trustedOriginRequiresConfiguredNonDefaultPort() {
        val policy = WebViewPolicy(
            trustedOrigins = setOf(TrustedWebOrigin("trusted.example.com", 8443))
        )

        assertTrue(policy.isTrusted("https://trusted.example.com:8443/path"))
        assertFalse(policy.isTrusted("https://trusted.example.com/path"))
        assertFalse(policy.isTrusted("https://trusted.example.com:443/path"))
    }

    @Test
    fun capabilitiesRequireBothTrustedOriginAndExplicitCapability() {
        val policy = WebViewPolicy(
            trustedOrigins = setOf(TrustedWebOrigin("trusted.example.com")),
            allowJavaScript = true,
            allowJavaScriptBridge = true,
            allowGeolocation = true,
            allowFileChooser = true
        )
        val trustedUrl = "https://trusted.example.com/page"
        val untrustedUrl = "https://untrusted.example.com/page"

        assertTrue(policy.allowsJavaScript(trustedUrl))
        assertTrue(policy.allowsJavaScriptBridge(trustedUrl))
        assertTrue(policy.allowsGeolocation(trustedUrl))
        assertTrue(policy.allowsFileChooser(trustedUrl))

        assertFalse(policy.allowsJavaScript(untrustedUrl))
        assertFalse(policy.allowsJavaScriptBridge(untrustedUrl))
        assertFalse(policy.allowsGeolocation(untrustedUrl))
        assertFalse(policy.allowsFileChooser(untrustedUrl))
    }

    @Test
    fun javaScriptBridgeRequiresJavaScriptCapability() {
        val policy = WebViewPolicy(
            trustedOrigins = setOf(TrustedWebOrigin("trusted.example.com")),
            allowJavaScriptBridge = true
        )

        assertFalse(policy.allowsJavaScript("https://trusted.example.com"))
        assertFalse(policy.allowsJavaScriptBridge("https://trusted.example.com"))
    }
}
