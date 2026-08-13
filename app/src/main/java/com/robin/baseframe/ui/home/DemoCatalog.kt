package com.robin.baseframe.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.robin.baseframe.R

enum class DemoCategory(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {
    OVERLAY(R.string.demo_category_overlay, R.drawable.ic_demo_overlay),
    LAYOUT(R.string.demo_category_layout, R.drawable.ic_demo_layout),
    STATE(R.string.demo_category_state, R.drawable.ic_demo_state),
    DEVICE(R.string.demo_category_device, R.drawable.ic_demo_device)
}

sealed interface DemoAction {
    data class Navigate(@IdRes val destinationAction: Int) : DemoAction
    data object ShowDialog : DemoAction
}

/**
 * Describes whether a demo can be launched on the current device.
 */
sealed interface DemoAvailability {
    data object Available : DemoAvailability

    data class Unavailable(@StringRes val reasonRes: Int) : DemoAvailability
}

/**
 * Immutable metadata used to render and launch one item in the demo catalog.
 */
data class DemoSpec(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val summaryRes: Int,
    val category: DemoCategory,
    @DrawableRes val iconRes: Int,
    val action: DemoAction,
    val availability: DemoAvailability = DemoAvailability.Available
) {
    val isAvailable: Boolean
        get() = availability is DemoAvailability.Available
}

sealed interface DemoCatalogRow {
    data class CategoryHeader(val category: DemoCategory) : DemoCatalogRow
    data class DemoItem(val item: DemoSpec) : DemoCatalogRow
}

object DemoCatalog {
    val items: List<DemoSpec> = listOf(
        DemoSpec(
            id = "dialog",
            titleRes = R.string.demo_title_dialog,
            summaryRes = R.string.demo_summary_dialog,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.ShowDialog
        ),
        DemoSpec(
            id = "any_layer",
            titleRes = R.string.demo_title_any_layer,
            summaryRes = R.string.demo_summary_any_layer,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_anyLayerFragment)
        ),
        DemoSpec(
            id = "popup",
            titleRes = R.string.demo_title_popup,
            summaryRes = R.string.demo_summary_popup,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_popupWindowFragment)
        ),
        DemoSpec(
            id = "notification",
            titleRes = R.string.demo_title_notification,
            summaryRes = R.string.demo_summary_notification,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_notificationFragment)
        ),
        DemoSpec(
            id = "motion_layout",
            titleRes = R.string.demo_title_motion_layout,
            summaryRes = R.string.demo_summary_motion_layout,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_motionFragment)
        ),
        DemoSpec(
            id = "coordinator_layout",
            titleRes = R.string.demo_title_coordinator_layout,
            summaryRes = R.string.demo_summary_coordinator_layout,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_coordinatorFragment)
        ),
        DemoSpec(
            id = "over_scroll",
            titleRes = R.string.demo_title_over_scroll,
            summaryRes = R.string.demo_summary_over_scroll,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_scrollFragment)
        ),
        DemoSpec(
            id = "flow",
            titleRes = R.string.demo_title_flow,
            summaryRes = R.string.demo_summary_flow,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_flowFragment)
        ),
        DemoSpec(
            id = "scoped_storage",
            titleRes = R.string.demo_title_scoped_storage,
            summaryRes = R.string.demo_summary_scoped_storage,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_storageFragment)
        ),
        DemoSpec(
            id = "count_down",
            titleRes = R.string.demo_title_count_down,
            summaryRes = R.string.demo_summary_count_down,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_countDownFragment)
        ),
        DemoSpec(
            id = "dsl",
            titleRes = R.string.demo_title_dsl,
            summaryRes = R.string.demo_summary_dsl,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_dslFragment)
        ),
        DemoSpec(
            id = "behavior",
            titleRes = R.string.demo_title_behavior,
            summaryRes = R.string.demo_summary_behavior,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_behaviorFragment)
        ),
        DemoSpec(
            id = "camera",
            titleRes = R.string.demo_title_camera,
            summaryRes = R.string.demo_summary_camera,
            category = DemoCategory.DEVICE,
            iconRes = DemoCategory.DEVICE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_cameraFragment)
        )
    )

    /**
     * Filters the catalog while preserving original order.
     *
     * The Android UI supplies localized title and summary text through
     * [searchableText]. The default keeps this model usable from plain unit
     * tests and callers that only need to search stable ids.
     */
    fun filter(
        query: String = "",
        category: DemoCategory? = null,
        searchableText: (DemoSpec) -> String = { it.id },
        specs: List<DemoSpec> = items
    ): List<DemoSpec> {
        val normalizedQuery = query.trim()
        return specs.filter { item ->
            val matchesCategory = category == null || item.category == category
            val matchesQuery = normalizedQuery.isEmpty() || searchableText(item)
                .contains(normalizedQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    /**
     * Finds a catalog item by its stable identifier.
     */
    fun findById(id: String): DemoSpec? = items.firstOrNull { it.id == id }

    /**
     * Returns a user-visible availability reason for a known unavailable demo.
     */
    @StringRes
    fun unavailableReason(id: String): Int? =
        (findById(id)?.availability as? DemoAvailability.Unavailable)?.reasonRes

    fun rows(items: List<DemoSpec> = this.items): List<DemoCatalogRow> = buildList {
        items.groupBy { it.category }.forEach { (category, categoryItems) ->
            add(DemoCatalogRow.CategoryHeader(category))
            categoryItems.forEach { add(DemoCatalogRow.DemoItem(it)) }
        }
    }
}
