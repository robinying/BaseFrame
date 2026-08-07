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

    @Test
    fun emptyQueryAndNoCategoryReturnAllItems() {
        assertEquals(DemoCatalog.items, DemoCatalog.filter())
    }

    @Test
    fun queryMatchesTitleOrSummaryText() {
        val searchableText = mapOf(
            "flow" to "Flow 数据流的加载、成功与更新",
            "camera" to "Camera 相机预览与设备能力接入"
        )

        assertEquals(
            listOf("flow"),
            DemoCatalog.filter(searchableText = { searchableText[it.id].orEmpty() }, query = "加载")
                .map { it.id }
        )
        assertEquals(
            listOf("camera"),
            DemoCatalog.filter(searchableText = { searchableText[it.id].orEmpty() }, query = "Camera")
                .map { it.id }
        )
    }

    @Test
    fun categoryAndQueryCanBeCombined() {
        val searchableText = mapOf(
            "flow" to "Flow 数据流",
            "count_down" to "CountDown 状态",
            "camera" to "Camera 状态"
        )

        assertEquals(
            listOf("count_down"),
            DemoCatalog.filter(
                query = "状态",
                category = DemoCategory.STATE,
                searchableText = { searchableText[it.id].orEmpty() }
            ).map { it.id }
        )
    }

    @Test
    fun unmatchedQueryReturnsNoRows() {
        assertTrue(DemoCatalog.filter(query = "不存在的 Demo").isEmpty())
        assertTrue(DemoCatalog.rows(DemoCatalog.filter(query = "不存在的 Demo")).isEmpty())
    }

    @Test
    fun filteredRowsKeepOnlyCategoriesThatHaveItems() {
        val rows = DemoCatalog.rows(DemoCatalog.filter(category = DemoCategory.DEVICE))

        assertEquals(
            listOf(DemoCategory.DEVICE),
            rows.filterIsInstance<DemoCatalogRow.CategoryHeader>().map { it.category }
        )
        assertEquals(1, rows.filterIsInstance<DemoCatalogRow.DemoItem>().size)
    }
}
