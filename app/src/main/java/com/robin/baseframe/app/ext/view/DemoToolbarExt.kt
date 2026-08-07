package com.robin.baseframe.app.ext.view

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.robin.baseframe.R

/**
 * Applies the shared navigation behavior to demo pages that expose a Toolbar.
 * The home page keeps the toolbar without an up affordance because it is the
 * root destination.
 */
fun Fragment.bindDemoToolbar(root: View) {
    val toolbar = root.findViewById<Toolbar>(R.id.tool_bar) ?: return
    val navController = findNavController()
    toolbar.title = navController.currentDestination?.label ?: toolbar.title
    if (navController.previousBackStackEntry != null) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationContentDescription(R.string.navigate_up)
        toolbar.setNavigationOnClickListener { navController.navigateUp() }
    } else {
        toolbar.navigationIcon = null
        toolbar.navigationContentDescription = null
        toolbar.setNavigationOnClickListener(null)
    }
}
