package com.robin.baseframe.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DemoDeepLinkTest {

    @Test
    fun parseDemoIdReturnsKnownCatalogId() {
        assertEquals("flow", DemoDeepLink.parseDemoId("baseframe://demo/flow"))
    }

    @Test
    fun parseDemoIdRejectsInvalidUriAndUnknownId() {
        assertNull(DemoDeepLink.parseDemoId("https://demo/flow"))
        assertNull(DemoDeepLink.parseDemoId("baseframe://other/flow"))
        assertNull(DemoDeepLink.parseDemoId("baseframe://demo/missing"))
        assertNull(DemoDeepLink.parseDemoId("baseframe://demo/flow/extra"))
    }
}
