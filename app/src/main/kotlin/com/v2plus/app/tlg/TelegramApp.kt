package com.v2plus.app.tlg

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class TelegramApp(
    val appName: String,
    val appIcon: Drawable,
    val componentName: ComponentName
)
