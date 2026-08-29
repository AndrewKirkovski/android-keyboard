package org.futo.inputmethod.latin.uix.theme

import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class KeyBackground(
    val foregroundColor: Int?,
    val outlineColor: Int?,
    val outlineWidth: Dp = 3.dp,
    val padding: Rect = Rect(0,0,0,0),
    val gap: RectF = RectF(1.0f,1.0f,1.0f,1.0f),
    val background: Drawable
)

data class KeyIcon(
    val drawable: Drawable
)

/**
 * A drop shadow cast by every key. Opt-in: a theme that leaves
 * [AdvancedThemeOptions.keyShadow] null renders exactly as it did before.
 *
 * The shadow is drawn into the gap that already exists between keys rather than widening it,
 * because that gap (KeyboardLayoutSet: 4dp horizontal, 8dp vertical) also feeds hit testing,
 * KeyDetector and gesture typing. Keeping radius + offsetY within roughly 3dp stays inside
 * that budget and leaves hit targets untouched.
 */
data class KeyShadow(
    val radius: Dp = 3.dp,
    val offsetY: Dp = 1.dp,
    val color: Int = 0x33000000
)

data class AdvancedThemeOptions(
    val backgroundShader: String? = null,
    val backgroundImage: ImageBitmap? = null,
    val backgroundImageVisibleArea: Rect? = null,
    val thumbnailImage: ImageBitmap? = null,
    val thumbnailScale: Float = 1.0f,
    val keyRoundness: Float = 1.0f,
    val actionBarOpacity: Float = 0.5f,
    val keyBorders: Boolean? = null,
    val keyBackgrounds: KeyedBitmaps<KeyBackground>? = null,
    val keyIcons: KeyedBitmaps<KeyIcon>? = null,
    val font: Typeface? = null,
    val themeName: String? = null,
    val themeAuthor: String? = null,

    val textSizeMultiplier: Float = 1.0f,
    val hintSizeMultiplier: Float = 1.0f,
    val textWeight: Float? = null,
    val hintWeight: Float? = null,

    val centerHints: Boolean = false,

    val keyShadow: KeyShadow? = null,
)
