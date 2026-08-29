package org.futo.inputmethod.latin.uix.theme.presets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.extendedDarkColorScheme
import org.futo.inputmethod.latin.uix.extendedLightColorScheme
import org.futo.inputmethod.latin.uix.theme.AdvancedThemeOptions
import org.futo.inputmethod.latin.uix.theme.KeyShadow
import org.futo.inputmethod.latin.uix.theme.ThemeOption

// Sampled from a Samsung keyboard screenshot, 1080px across a 10 column row:
// backdrop #E4E4E6, letter key #FCFCFE, functional key #C9C9CB, label #202020.
//
// The letter/functional split is the part that matters. Stock light is #FFFFFF against
// #F7F7F7, which is barely a split at all; here the functional keys are visibly greyer,
// which is what makes shift, delete and the symbols key read as a separate class.

private val lightShadow = KeyShadow(radius = 3.dp, offsetY = 1.dp, color = 0x33000000)

// A black shadow is close to invisible on a dark backdrop, so the dark variant leans on a
// denser one rather than a larger one, which would not fit the key gap.
private val darkShadow = KeyShadow(radius = 3.dp, offsetY = 1.dp, color = 0x66000000)

private val lightScheme = extendedLightColorScheme(
    primary = Color(0xFF2A6DF4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF0B2A66),
    secondary = Color(0xFF5A6070),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDDE1E8),
    onSecondaryContainer = Color(0xFF2A2E36),
    tertiary = Color(0xFF3F6070),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E5EE),
    onTertiaryContainer = Color(0xFF16323D),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF8A8D94),
    outlineVariant = Color(0xFFD4D6DA),
    surface = Color(0xFFE4E4E6),
    onSurface = Color(0xFF202020),
    onSurfaceVariant = Color(0xFF55585F),
    surfaceContainerHighest = Color(0xFFD8D9DC),
    keyboardSurface = Color(0xFFE4E4E6),
    keyboardSurfaceDim = Color(0xFFD9D9DC),
    keyboardContainer = Color(0xFFFCFCFE),
    keyboardContainerVariant = Color(0xFFC9C9CB),
    onKeyboardContainer = Color(0xFF202020),
    keyboardPress = Color(0xFFBFC0C4),
    primaryTransparent = Color(0xFF2A6DF4).copy(alpha = 0.3f),
    onSurfaceTransparent = Color(0xFF202020).copy(alpha = 0.1f),
    // No textWeight. An earlier version set 500 on the assumption that Samsung's
    // keycaps are heavier than stock's; they are not, they are regular weight,
    // and 500 read as bold against the reference. Leaving it null keeps the
    // default, which is what every other preset does -- this was the only preset
    // in the app setting the field at all.
    advanced = AdvancedThemeOptions(keyShadow = lightShadow)
)

private val darkScheme = extendedDarkColorScheme(
    primary = Color(0xFF8FB4FF),
    onPrimary = Color(0xFF10254F),
    primaryContainer = Color(0xFF1B345E),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFFB6BCC9),
    onSecondary = Color(0xFF272A31),
    secondaryContainer = Color(0xFF2E3138),
    onSecondaryContainer = Color(0xFFDDE1E8),
    tertiary = Color(0xFFA6C9DA),
    onTertiary = Color(0xFF10323F),
    tertiaryContainer = Color(0xFF2A4854),
    onTertiaryContainer = Color(0xFFD3E5EE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF6E7178),
    outlineVariant = Color(0xFF3A3D43),
    surface = Color(0xFF1B1C1E),
    onSurface = Color(0xFFECECEE),
    onSurfaceVariant = Color(0xFFB6B8BC),
    surfaceContainerHighest = Color(0xFF34363A),
    keyboardSurface = Color(0xFF1B1C1E),
    keyboardSurfaceDim = Color(0xFF141517),
    keyboardContainer = Color(0xFF35373C),
    keyboardContainerVariant = Color(0xFF232427),
    onKeyboardContainer = Color(0xFFECECEE),
    keyboardPress = Color(0xFF4A4D53),
    primaryTransparent = Color(0xFF8FB4FF).copy(alpha = 0.3f),
    onSurfaceTransparent = Color(0xFFECECEE).copy(alpha = 0.1f),
    advanced = AdvancedThemeOptions(keyShadow = darkShadow)
)

val SamsungLightScheme = ThemeOption(
    dynamic = false,
    key = "SamsungLightScheme",
    name = R.string.theme_samsung_light,
    available = { true }
) {
    lightScheme
}

val SamsungDarkScheme = ThemeOption(
    dynamic = false,
    key = "SamsungDarkScheme",
    name = R.string.theme_samsung_dark,
    available = { true }
) {
    darkScheme
}
