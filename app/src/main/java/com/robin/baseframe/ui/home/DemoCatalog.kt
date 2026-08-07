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

data class DemoCatalogItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val summaryRes: Int,
    val category: DemoCategory,
    @DrawableRes val iconRes: Int,
    val action: DemoAction
)

sealed interface DemoCatalogRow {
    data class CategoryHeader(val category: DemoCategory) : DemoCatalogRow
    data class DemoItem(val item: DemoCatalogItem) : DemoCatalogRow
}

object DemoCatalog {
    val items: List<DemoCatalogItem> = listOf(
        DemoCatalogItem(
            id = "dialog",
            titleRes = R.string.demo_title_dialog,
            summaryRes = R.string.demo_summary_dialog,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.ShowDialog
        ),
        DemoCatalogItem(
            id = "any_layer",
            titleRes = R.string.demo_title_any_layer,
            summaryRes = R.string.demo_summary_any_layer,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_anyLayerFragment)
        ),
        DemoCatalogItem(
            id = "popup",
            titleRes = R.string.demo_title_popup,
            summaryRes = R.string.demo_summary_popup,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_popupWindowFragment)
        ),
        DemoCatalogItem(
            id = "notification",
            titleRes = R.string.demo_title_notification,
            summaryRes = R.string.demo_summary_notification,
            category = DemoCategory.OVERLAY,
            iconRes = DemoCategory.OVERLAY.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_notificationFragment)
        ),
        DemoCatalogItem(
            id = "motion_layout",
            titleRes = R.string.demo_title_motion_layout,
            summaryRes = R.string.demo_summary_motion_layout,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_motionFragment)
        ),
        DemoCatalogItem(
            id = "coordinator_layout",
            titleRes = R.string.demo_title_coordinator_layout,
            summaryRes = R.string.demo_summary_coordinator_layout,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_coordinatorFragment)
        ),
        DemoCatalogItem(
            id = "over_scroll",
            titleRes = R.string.demo_title_over_scroll,
            summaryRes = R.string.demo_summary_over_scroll,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_scrollFragment)
        ),
        DemoCatalogItem(
            id = "flow",
            titleRes = R.string.demo_title_flow,
            summaryRes = R.string.demo_summary_flow,
            category = DemoCategory.LAYOUT,
            iconRes = DemoCategory.LAYOUT.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_flowFragment)
        ),
        DemoCatalogItem(
            id = "scoped_storage",
            titleRes = R.string.demo_title_scoped_storage,
            summaryRes = R.string.demo_summary_scoped_storage,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_storageFragment)
        ),
        DemoCatalogItem(
            id = "count_down",
            titleRes = R.string.demo_title_count_down,
            summaryRes = R.string.demo_summary_count_down,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_countDownFragment)
        ),
        DemoCatalogItem(
            id = "dsl",
            titleRes = R.string.demo_title_dsl,
            summaryRes = R.string.demo_summary_dsl,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_dslFragment)
        ),
        DemoCatalogItem(
            id = "behavior",
            titleRes = R.string.demo_title_behavior,
            summaryRes = R.string.demo_summary_behavior,
            category = DemoCategory.STATE,
            iconRes = DemoCategory.STATE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_behaviorFragment)
        ),
        DemoCatalogItem(
            id = "camera",
            titleRes = R.string.demo_title_camera,
            summaryRes = R.string.demo_summary_camera,
            category = DemoCategory.DEVICE,
            iconRes = DemoCategory.DEVICE.iconRes,
            action = DemoAction.Navigate(R.id.action_main_to_cameraFragment)
        )
    )

    fun rows(): List<DemoCatalogRow> = buildList {
        items.groupBy { it.category }.forEach { (category, categoryItems) ->
            add(DemoCatalogRow.CategoryHeader(category))
            categoryItems.forEach { add(DemoCatalogRow.DemoItem(it)) }
        }
    }
}
