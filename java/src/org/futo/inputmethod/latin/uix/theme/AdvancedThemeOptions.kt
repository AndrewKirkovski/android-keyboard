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
 * A drop shadow for a key that draws a background, on a theme with key borders on.
 * Opt-in: a theme that leaves [AdvancedThemeOptions.keyShadow] null renders exactly
 * as it did before.
 *
 * Which keys that covers is BasicThemeProvider's to decide, and it is narrower than
 * "the filled ones". Four styles are handed the shadow -- an ordinary key, a
 * functional key, a sticky-off modifier and the spacebar -- each only on the branch
 * key borders on selects. A latched modifier and the enter key are filled and are
 * handed it in no configuration. A pressed key drops the shadow it had, because its
 * pressed drawable is built without one.
 *
 * Key borders off nulls the shadow before any style is built, so none of the four
 * casts. That is not the same as losing the fill, and the two do not line up: the
 * keys that go transparent lose both, while the spacebar keeps a fill and simply
 * stops casting.
 *
 * The shadow is drawn into the gap that already exists between keys rather than
 * widening it, because that gap also feeds hit testing, KeyDetector and gesture
 * typing. [radius] is what spreads it sideways, [radius] plus [offsetY] what spreads
 * it down, so the two axes have separate budgets -- the gap on that axis. Past it the
 * shadow lands on the next key instead of between them, and hit targets are what pay.
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

    /**
     * How far the background image is blurred behind the keys.
     *
     * A photograph at full sharpness competes with the key labels sitting on it. Opacity
     * already had an answer -- both theme formats carry it and apply it as a surface-coloured
     * overlay through `keyboardBackgroundGradient` -- but sharpness did not.
     *
     * Applied in Compose at [org.futo.inputmethod.latin.uix.KeyboardBackground], so it costs a
     * RenderEffect on one node rather than any work per key, and it is a no-op below API 31.
     *
     * The default is what the constructions in ColorScheme.kt rely on. A theme file that
     * predates this is covered separately: ktoml decodes SerializedTomlFile, whose own
     * `blur` field defaults to 0, and Toml.kt passes that through explicitly.
     */
    val backgroundImageBlur: Dp = 0.dp,

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
