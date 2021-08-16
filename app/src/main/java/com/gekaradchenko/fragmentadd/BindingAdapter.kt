package com.gekaradchenko.fragmentadd

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("fabGONE")
fun fabGone(view: View, count: Int) {
    view.visibility = if (count > 1) View.VISIBLE else View.GONE
}