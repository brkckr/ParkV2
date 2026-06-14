package com.brkckr.parkv2.presentation.common

import android.content.Context
import com.brkckr.parkv2.domain.util.UiText

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> context.getString(resId, *args)
    }
}
