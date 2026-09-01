package org.futo.inputmethod.latin.uix.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Superseded by [org.futo.inputmethod.latin.uix.theme.app.AppTypography], which is now
 * supplied to MaterialTheme on both sides of the app. Nothing under settings/ reads this
 * any more. What remains is keyboard-side -- its panels, its window bar, the floating
 * pre-edit, the quick-clip pill and two dialogs -- plus ThemePreview, which is shared
 * with the settings Themes screen. All out of scope for the settings redesign, and
 * migrated with the keyboard work rather than ahead of it.
 *
 * Do not add uses. Five of the ten styles below set lineHeight equal to fontSize, which
 * crushes any string that wraps -- the reason it is being retired.
 */
data object Typography {
    data object Heading {
        val MediumMl = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 30.sp
        )

        val Medium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            //lineHeight = 13.6.sp
            lineHeight = 20.sp,
        )

        val RegularMl = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 30.sp
        )

        val Regular = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            //lineHeight = 13.6.sp
            lineHeight = 20.sp,
        )
    }

    data object Body {
        val MediumMl = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )

        val Medium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            //lineHeight = 10.56.sp
            lineHeight = 16.sp,
        )

        val RegularMl = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )

        val Regular = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            //lineHeight = 10.56.sp
            lineHeight = 16.sp,
        )
    }

    val SmallMl = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    val Small = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        //lineHeight = 9.52.sp
        lineHeight = 14.sp
    )
}