package org.futo.inputmethod.latin.uix.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionBarHeight
import org.futo.inputmethod.latin.uix.ActionSep
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.CloseResult
import org.futo.inputmethod.latin.uix.TutorialMode
import org.futo.inputmethod.v2keyboard.KeyboardMode
import org.futo.inputmethod.v2keyboard.KeyboardSizingCalculator
import org.futo.inputmethod.latin.uix.theme.Typography

/**
 * One mode in the row.
 *
 * The current mode used to be marked by swapping its glyph for a filled variant with a
 * check badge and colouring it `tertiary`. That is a treatment nothing else in the app
 * uses, and it cannot be shared: it needs a second hand-drawn icon per item, which the
 * emoji categories and the theme thumbnails could never have. It also cost the most
 * where it was used -- these four glyphs are told apart by their silhouette, and filling
 * one in flattens the detail on the one tile the user is looking for.
 *
 * The marker is instead the filled `secondary` / `onSecondary` pair the emoji category
 * row uses, which is also what the keyboard itself puts on a latched key
 * (KeyVisualStyle.StickyOn). A first pass used `outline` at 10% and measured between
 * 1.08 and 1.22 to 1 against the panel on all nineteen presets -- a container nobody
 * can see, leaving the content alpha as the only signal. Horizontally inset so it does
 * not run flush to the screen edge; the vertical inset stays small because the whole
 * panel is 54dp.
 */
@Composable
internal fun RowScope.KeyboardMode(iconRes: Int, name: String, sizingCalculator: KeyboardSizingCalculator, mode: KeyboardMode, isChecked: Boolean) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .weight(1.0f)
            .height(54.dp)
            // Four choices, one of them current. A screen reader was told which mode
            // each tile was and never which one it was on.
            .semantics {
                role = Role.RadioButton
                selected = isChecked
            },
        onClick = {
            sizingCalculator.editSavedSettings { settings ->
                settings.copy(
                    currentMode = mode
                ).let {
                    // Set prefersSplit
                    when (mode) {
                        KeyboardMode.Split -> it.copy(prefersSplit = true)
                        KeyboardMode.Regular -> it.copy(prefersSplit = false)
                        else -> it
                    }
                }
            }
        },
        // Full strength either way. Dimming the unselected tiles to 60% put the
        // label under 4.5:1 on seven of the light presets -- it is small text at
        // any size the auto-sizer picks, so that threshold applies throughout --
        // and it was saying what the filled pill already says.
        contentColor = if(isChecked) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            MaterialTheme.colorScheme.onBackground
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // The full width of the tile, behind the content rather than around
            // it. Both halves of that matter. In one-handed mode the tile is 87dp,
            // and insetting the box the label sits in cost it 12 of those, leaving
            // 75; "One-handed" is 77dp at the default font scale and 84 at the 1.1
            // this phone is set to, so it clipped. Insetting only the marker
            // instead leaves the label hanging
            // off both ends of it, in a colour chosen to sit on the marker. The row
            // carries the margin.
            if(isChecked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(vertical = 3.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(12.dp)
                        )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painterResource(iconRes),
                    contentDescription = null
                )
                // Shrinks to fit rather than wrapping. The tile is 87dp wide in
                // one-handed mode at 411dp, and the longest of these four labels
                // -- Lithuanian "Plūduriuojanti" -- is 95dp at the phone's own
                // font scale of 1.1. A second line does not fit in a 54dp tile,
                // so wrapping means a cut label; this way it is whole at 14sp
                // wherever it fits and down to 10sp where it does not. Below that
                // floor -- a narrow phone one-handed at the largest system font
                // -- it ellipsises, which at least says that it did.
                BasicText(
                    name,
                    style = Typography.SmallMl.copy(color = LocalContentColor.current),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 10.sp,
                        maxFontSize = 14.sp,
                        stepSize = 0.5.sp
                    )
                )
            }
        }
    }
}

val KeyboardModeAction = Action(
    icon = R.drawable.keyboard_gear,
    name = R.string.settings_action_keyboard_modes,
    simplePressImpl = null,
    windowImpl = { manager, _ ->
        val sizeCalculator = manager.getSizingCalculator()
        object : ActionWindow() {
            override val showCloseButton: Boolean
                get() = false

            override val onlyShowAboveKeyboard: Boolean
                get() = true

            override val fixedWindowHeight: Dp?
                get() = 54.dp + ActionBarHeight

            @Composable
            override fun windowName(): String =
                stringResource(R.string.settings_action_keyboard_modes)

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                val currMode = sizeCalculator.getSavedSettings().currentMode
                // The one panel that still builds its own header rather than
                // setting showTitleBarAboveKeyboard. The shared bar always draws a
                // back arrow, and the resize tutorial needs that one arrow gone so
                // the only way on is the button beside it. Everything else here
                // matches the shared bar exactly -- same glyph, same title style --
                // so the two read as one thing.
                Column {
                    // ActionWindowBar is a column of a hairline over a weighted
                    // row, so a bare 40dp row here was a hairline taller and the
                    // only panel header without the rule above it. One difference
                    // remains: the shared bar draws its rule outside the window's
                    // side padding and this one inside it, so in one-handed mode
                    // this rule stops at the keyboard rather than crossing the
                    // empty strip. That is the better of the two.
                    Column(Modifier.height(ActionBarHeight)) {
                        ActionSep()
                        Row(Modifier.weight(1.0f)) {
                            if(manager.getTutorialMode() != TutorialMode.ResizerTutorial) {
                                IconButton(onClick = {
                                    manager.closeActionWindow()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_left_26),
                                        contentDescription = stringResource(R.string.action_keyboard_modes_go_back)
                                    )
                                }
                            }
                            Text(
                                windowName(),
                                style = Typography.Body.MediumMl,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(Modifier.weight(1.0f))
                            TextButton(onClick = {
                                manager.showResizer()

                                if(manager.getTutorialMode() == TutorialMode.ResizerTutorial) {
                                    manager.markTutorialCompleted()
                                }
                            }, Modifier.onGloballyPositioned {
                                if(manager.getTutorialMode() == TutorialMode.ResizerTutorial) {
                                    manager.setTutorialArrowPosition(it)
                                }
                            }) {
                                Text(stringResource(R.string.action_keyboard_modes_resize_keyboard), style = Typography.Body.MediumMl)
                            }
                        }
                    }
                    // The margin the marker used to carry. Here it costs each tile
                    // 3dp rather than 12, and keeps the outer two off the screen
                    // edges they would otherwise run flush to.
                    Row(Modifier.padding(horizontal = 6.dp)) {
                        KeyboardMode(
                            R.drawable.keyboard_regular,
                            stringResource(R.string.action_keyboard_modes_standard),
                            sizeCalculator, KeyboardMode.Regular,
                            currMode == KeyboardMode.Regular
                        )

                        KeyboardMode(
                            R.drawable.keyboard_left_handed,
                            stringResource(R.string.action_keyboard_modes_one_handed),
                            sizeCalculator, KeyboardMode.OneHanded,
                            currMode == KeyboardMode.OneHanded
                        )

                        if(sizeCalculator.doesCurrentLayoutSupportSplit()) {
                            KeyboardMode(
                                R.drawable.keyboard_split,
                                stringResource(R.string.action_keyboard_modes_split),
                                sizeCalculator, KeyboardMode.Split,
                                currMode == KeyboardMode.Split
                            )
                        }

                        KeyboardMode(
                            R.drawable.keyboard_float,
                            stringResource(R.string.action_keyboard_modes_floating),
                            sizeCalculator, KeyboardMode.Floating,
                            currMode == KeyboardMode.Floating
                        )
                    }
                }
            }

            override fun close(): CloseResult {
                if(manager.getTutorialMode() == TutorialMode.ResizerTutorial) {
                    manager.markTutorialCompleted()
                }
                return CloseResult.Default
            }
        }
    },
)