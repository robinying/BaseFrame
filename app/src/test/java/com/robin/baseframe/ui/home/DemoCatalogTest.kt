package com.robin.baseframe.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoCatalogTest {

    @Test
    fun catalogItemsHaveUniqueIdsAndExecutableActions() {
        val items = DemoCatalog.items

        assertTrue(items.isNotEmpty())
        assertEquals(items.size, items.map { it.id }.distinct().size)
        assertFalse(items.any { it.titleRes == 0 })
        assertTrue(items.all { it.action is DemoAction.Navigate || it.action is DemoAction.ShowDialog })
    }

    @Test
    fun rowsKeepCatalogCategoriesInDisplayOrder() {
        val rows = DemoCatalog.rows()
        val headers = rows.filterIsInstance<DemoCatalogRow.CategoryHeader>()
        val items = rows.filterIsInstance<DemoCatalogRow.DemoItem>()

        assertEquals(DemoCatalog.items.size, items.size)
        assertEquals(
            DemoCatalog.items.map { it.category }.distinct(),
            headers.map { it.category }
        )
        assertTrue(rows.first() is DemoCatalogRow.CategoryHeader)
    }
}
