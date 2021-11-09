package com.robin.baseframe.app.ext

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

inline fun <reified T : DialogFragment> Fragment.showDialogFragment(
    arguments: Bundle.() -> Unit = {}
) {
    val dialog = T::class.java.newInstance()
    val bundle = Bundle()
    bundle.apply(arguments)
    dialog.arguments = bundle
    dialog.show(childFragmentManager, T::class.simpleName)
}

fun Fragment.showDialogFragment(dialogFragment: DialogFragment) {
    dialogFragment.show(childFragmentManager, dialogFragment::class.simpleName)
}