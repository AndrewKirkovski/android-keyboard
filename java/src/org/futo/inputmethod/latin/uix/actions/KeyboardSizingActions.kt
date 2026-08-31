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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionBarHeight
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
        contentColor = if(isChecked) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Behind the content rather than around it. Insetting the box the label
            // sits in costs the label 12dp, and in one-handed mode the whole tile is
            // 76dp -- which is where "One-handed" started coming out as "One-hande".
            if(isChecked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(horizontal = 6.dp, vertical = 3.dp)
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
                Text(name, style = Typography.SmallMl)
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
                Column {
                    Row(Modifier.height(ActionBarHeight)) {
                        // Hide the back button in the resize tutorial
                        if(manager.getTutorialMode() != TutorialMode.ResizerTutorial) {
                            IconButton(onClick = {
                                manager.closeActionWindow()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_keyboard_modes_go_back))
                            }
                        }
                        // This panel keeps the keyboard visible, so the shared
                        // ActionWindowBar -- which is what draws every other
                        // panel's title -- is not rendered for it (UixManager.kt:842).
                        // Without this the screen was the only one with no title at
                        // all, and the "Resize keyboard" button on the right read as
                        // one.
                        Text(
                            windowName(),
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
                    Row {
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