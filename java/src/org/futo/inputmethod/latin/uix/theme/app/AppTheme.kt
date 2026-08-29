package org.futo.inputmethod.latin.uix.theme.app

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.uix.actions.compatEmojiTypeface
import org.futo.inputmethod.latin.uix.theme.LocalCompatEmojiFamily
import org.futo.inputmethod.latin.uix.theme.LocalCompatEmojiTypeface

/**
 * The settings app's own theme.
 *
 * The app used to render in whichever *keyboard* theme the user had picked, which is
 * why it never looked designed: a palette tuned for a 360dp strip sitting over someone
 * else's app, often with a photograph behind it, was being asked to carry a full-screen
 * scrolling text surface as well. Dark mode was not a mode, it was eighteen arbitrary
 * palettes recoloured, and contrast was unknowable in advance because the palette was
 * user-supplied.
 *
 * So the app follows the system light/dark setting like any other Android app, and the
 * keyboard theme stays where it belongs: on the keyboard, and on the previews of it that
 * settings screens show. Those previews wrap themselves in the keyboard scheme
 * explicitly, which is why a preview can look nothing like the page around it -- that is
 * the point, not a bug.
 *
 * The palette is the product's own, taken from polish-typographic.com rather than
 * invented here: warm neutrals and a crimson accent, so the app reads as part of the
 * same thing the keyboard belongs to.
 */

// --------------------------------------------------------------------------------- //
// colour
// --------------------------------------------------------------------------------- //

private val Crimson = Color(0xFFE94560)
private val CrimsonLight = Color(0xFFEE6680)

private val AppLightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFBE3E7),
    onPrimaryContainer = Color(0xFF5C1220),

    secondary = Color(0xFF6B6862),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDEAE3),
    onSecondaryContainer = Color(0xFF2A2A28),

    tertiary = Color(0xFFB25E00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFDF0DC),
    onTertiaryContainer = Color(0xFF4A2800),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    background = Color(0xFFFAF9F6),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFAF9F6),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0EEE9),
    onSurfaceVariant = Color(0xFF6B6862),

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF9F6),
    surfaceContainer = Color(0xFFF5F3EE),
    surfaceContainerHigh = Color(0xFFF0EEE9),
    surfaceContainerHighest = Color(0xFFE8E6E1),

    outline = Color(0xFFC5C0B6),
    outlineVariant = Color(0xFFD8D4CC),
    scrim = Color(0xFF000000),
)

private val AppDarkColors = darkColorScheme(
    primary = CrimsonLight,
    onPrimary = Color(0xFF3A0A14),
    primaryContainer = Color(0xFF3A1F26),
    onPrimaryContainer = Color(0xFFFBE3E7),

    secondary = Color(0xFF9B9B9B),
    onSecondary = Color(0xFF242422),
    secondaryContainer = Color(0xFF2A2A2A),
    onSecondaryContainer = Color(0xFFE8E6E1),

    tertiary = Color(0xFFE8A33D),
    onTertiary = Color(0xFF2E1D00),
    tertiaryContainer = Color(0xFF2E2517),
    onTertiaryContainer = Color(0xFFFDF0DC),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    background = Color(0xFF111111),
    onBackground = Color(0xFFF0EEE9),
    surface = Color(0xFF111111),
    onSurface = Color(0xFFF0EEE9),
    surfaceVariant = Color(0xFF242422),
    onSurfaceVariant = Color(0xFF9B9B9B),

    surfaceContainerLowest = Color(0xFF0B0B0B),
    surfaceContainerLow = Color(0xFF161615),
    surfaceContainer = Color(0xFF1A1A1A),
    surfaceContainerHigh = Color(0xFF242422),
    surfaceContainerHighest = Color(0xFF2E2E2C),

    outline = Color(0xFF6E6D69),
    outlineVariant = Color(0xFF3A3A36),
    scrim = Color(0xFF000000),
)

// --------------------------------------------------------------------------------- //
// type
// --------------------------------------------------------------------------------- //

/**
 * Weight and opacity carry the hierarchy here, not size.
 *
 * The old rows were 16sp/400 title over 14sp/400 subtitle -- two points of size and
 * nothing else -- so a three-line subtitle visually outweighed the row it belonged to.
 * A row title now differs from its subtitle on three axes at once: 16/500/100% against
 * 13/400/70%.
 *
 * The family stays the platform sans deliberately. There are 541 UIX strings across 91
 * locales, including Devanagari, Arabic, Thai, Khmer, CJK and Cyrillic; a display face
 * without full coverage falls back per glyph and looks broken in exactly the locales
 * nobody here would test.
 *
 * Every line height sits inside the 120-145% band.
 */
val AppTypography = Typography(
    // screen title
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 28.sp, lineHeight = 34.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = (-0.02).em,
    ),
    // section header: uppercase and letterspaced, at one short line
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 13.sp, lineHeight = 16.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 0.08.em,
    ),
    // row title
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp, lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
    ),
    // row subtitle
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 13.sp, lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    // control value
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp, lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
    ),
    // caption
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 12.sp, lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
)

// --------------------------------------------------------------------------------- //
// shape
// --------------------------------------------------------------------------------- //

/**
 * Three steps, replacing the sixteen ad-hoc RoundedCornerShape values under settings/.
 * MaterialTheme.shapes was previously supplied to nothing and used zero times.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// --------------------------------------------------------------------------------- //
// wrapper
// --------------------------------------------------------------------------------- //

/**
 * Wraps a full-screen activity: the settings, the mic permission prompt and the payment
 * completion screen. NOT the IME -- that keeps [org.futo.inputmethod.latin.uix.theme.UixThemeAuto].
 *
 * [dynamicColor] is off by default. Android 12+ can hand an app the wallpaper palette,
 * and a stock settings app usually takes it -- but here it replaces the product's own
 * colours with whatever is behind the launcher, which is the one thing this theme exists
 * to stop happening. The palette is the point, so it wins; the parameter stays for
 * anyone who wants the platform behaviour.
 */
@Composable
fun AppTheme(
    dark: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        dark -> AppDarkColors
        else -> AppLightColors
    }

    // The IME wrapper provides these, and settings screens render emoji too (the action
    // list, the emoji picker's own settings), so provide them here as well rather than
    // leaving them null off the keyboard.
    val emojiTypeface = remember { context.compatEmojiTypeface }
    val emojiFamily = remember(emojiTypeface) { emojiTypeface?.let { FontFamily(it) } }

    CompositionLocalProvider(
        LocalCompatEmojiFamily provides emojiFamily,
        LocalCompatEmojiTypeface provides emojiTypeface,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
