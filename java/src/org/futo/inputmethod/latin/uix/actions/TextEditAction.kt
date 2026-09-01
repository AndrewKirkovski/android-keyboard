package org.futo.inputmethod.latin.uix.actions

import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.common.Constants
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.LocalKeyboardScheme
import org.futo.inputmethod.latin.uix.LocalThemeProvider

@Composable
fun IconWithColor(@DrawableRes iconId: Int, iconColor: Color, modifier: Modifier = Modifier) {
    val icon = painterResource(id = iconId)

    Canvas(modifier = modifier) {
        translate(
            left = this.size.width / 2.0f - icon.intrinsicSize.width / 2.0f,
            top = this.size.height / 2.0f - icon.intrinsicSize.height / 2.0f
        ) {
            with(icon) {
                draw(
                    icon.intrinsicSize,
                    colorFilter = ColorFilter.tint(
                        iconColor
                    )
                )
            }
        }
    }
}

@Composable
fun TogglableKey(
    onToggle: (Boolean) -> Unit,
    toggled: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable (color: Color) -> Unit
) {
    val keys = panelKeyColors()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if(isPressed) {
            onToggle(!toggled)
        }
    }

    Surface(
        modifier = modifier
            .padding(4.dp)
            .keyShadow(if (toggled) 0.dp else keys.elevation, keys.shadowColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { }
            ),
        shape = RoundedCornerShape(8.dp),
        color = if(toggled) { keys.accent } else { keys.functionalKey }
    ) {
        contents(if(toggled) { keys.onAccent } else { keys.onKey })
    }

}

@Composable
fun Modifier.repeatablyClickableAction(repeatable: Boolean = true, onTrigger: (Boolean) -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnTrigger by rememberUpdatedState(onTrigger)

    LaunchedEffect(interactionSource, repeatable) {
        var repeatJob: Job? = null

        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    currentOnTrigger(false)

                    if (repeatable) {
                        repeatJob?.cancel()
                        repeatJob = launch {
                            delay(670L)
                            while (isActive) {
                                currentOnTrigger(true)
                                delay(50L)
                            }
                        }
                    }
                }
                is PressInteraction.Release,
                is PressInteraction.Cancel -> {
                    repeatJob?.cancel()
                    repeatJob = null
                }
            }
        }
    }

    return this.clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        onClick = { }
    )
}

/**
 * The three key treatments the keyboard itself draws, so a panel built out of
 * keys matches the keys under it. [BasicThemeProvider] builds the keyboard's own
 * from exactly these roles: an ordinary key, a functional key, and the pair it
 * puts on a latched one -- KeyVisualStyle.StickyOn, which is secondary rather
 * than the enter key's primary. It also drops both fills to transparent on a theme with
 * key borders off, which is why this reads the provider rather than the colour
 * scheme alone -- a panel that ignored it would draw filled boxes over a
 * keyboard that draws none.
 */
private class PanelKeyColors(
    val key: Color,
    val functionalKey: Color,
    val onKey: Color,
    val accent: Color,
    val onAccent: Color,
    val elevation: Dp,
    val shadowColor: Color
)

@Composable
private fun panelKeyColors(): PanelKeyColors {
    val scheme = LocalKeyboardScheme.current
    // A preview has no theme provider, and reading it there throws.
    val borders = LocalInspectionMode.current || LocalThemeProvider.current.keyBorders

    return PanelKeyColors(
        key = if (borders) scheme.keyboardContainer else Color.Transparent,
        functionalKey = if (borders) scheme.keyboardContainerVariant else Color.Transparent,
        onKey = if (borders) scheme.onKeyboardContainer else scheme.onBackground,
        accent = scheme.secondary,
        onAccent = scheme.onSecondary,
        // A theme that gives its keys a shadow has to give the panel's the same
        // one, or a panel of keys sits on the keyboard looking like a different
        // material. BasicThemeProvider bakes that shadow into the key's own
        // drawable and this does not reuse it, so the blur radius the theme asks
        // for becomes an elevation and the colour is carried across separately --
        // Compose would otherwise substitute its own, and the two Samsung themes
        // differ only in shadow colour, sharing a radius.
        //
        // Suppressed with borders off to match BasicThemeProvider, which drops
        // the shadow with them: "only a key that draws a background can cast a
        // shadow".
        elevation = if (borders) {
            scheme.advancedThemeOptions.keyShadow?.radius ?: 0.dp
        } else {
            0.dp
        },
        shadowColor = Color(scheme.advancedThemeOptions.keyShadow?.color ?: 0)
    )
}

/**
 * The key shadow the theme asks for, in the theme's own colour.
 *
 * A latched key passes 0: BasicThemeProvider builds KeyVisualStyle.StickyOn with no
 * shadow, so a shift-locked key on the keyboard casts none and one in a panel must
 * not either.
 */
private fun Modifier.keyShadow(elevation: Dp, color: Color): Modifier =
    if (elevation <= 0.dp) this else this.shadow(
        elevation = elevation,
        shape = RoundedCornerShape(8.dp),
        clip = false,
        ambientColor = color,
        spotColor = color
    )

@Composable
fun ActionKey(
    onTrigger: () -> Unit,
    modifier: Modifier = Modifier,
    repeatable: Boolean = true,
    color: Color = panelKeyColors().key,
    contents: @Composable () -> Unit
) {
    val keys = panelKeyColors()
    Surface(
        modifier = modifier
            .padding(4.dp)
            .keyShadow(keys.elevation, keys.shadowColor)
            .repeatablyClickableAction(
                repeatable = repeatable,
                onTrigger = { onTrigger() }
            ),
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        contents()
    }
}

@Composable
fun ArrowKeys(
    modifier: Modifier,
    moveCursor: (direction: Direction) -> Unit
) {
    val keys = panelKeyColors()

    Row(modifier = modifier) {
        ActionKey(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight(),
            onTrigger = { moveCursor(Direction.Left) }
        ) {
            IconWithColor(
                iconId = R.drawable.arrow_left,
                iconColor = keys.onKey
            )
        }

        Column(modifier = Modifier
            .weight(1.0f)
            .fillMaxHeight()) {
            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                onTrigger = { moveCursor(Direction.Up) }
            ) {
                IconWithColor(
                    iconId = R.drawable.arrow_up,
                    iconColor = keys.onKey
                )
            }


            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                onTrigger = { moveCursor(Direction.Down) }
            ) {
                IconWithColor(
                    iconId = R.drawable.arrow_down,
                    iconColor = keys.onKey
                )
            }
        }

        ActionKey(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight(),
            onTrigger = { moveCursor(Direction.Right) }
        ) {
            IconWithColor(
                iconId = R.drawable.arrow_right,
                iconColor = keys.onKey
            )
        }
    }
}

@Composable
fun CtrlShiftMetaKeys(modifier: Modifier, ctrlState: MutableState<Boolean>, shiftState: MutableState<Boolean>) {
    Row(modifier = modifier) {
        TogglableKey(
            onToggle = { ctrlState.value = it },
            toggled = ctrlState.value,
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
        ) {
            IconWithColor(
                iconId = R.drawable.ctrl,
                iconColor = it
            )
        }
        TogglableKey(
            onToggle = { shiftState.value = it },
            toggled = shiftState.value,
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
        ) {
            IconWithColor(
                iconId = R.drawable.shift,
                iconColor = it
            )
        }
    }
}

@Composable
fun SideKeys(modifier: Modifier, onEvent: (Int, Int) -> Unit, onCodePoint: (Int) -> Unit, keyboardShown: Boolean) {
    val keys = panelKeyColors()

    // The column beside this one splits 3:1 -- arrows over modifiers -- so its one
    // horizontal line sits at three quarters. Four side keys land on that line by
    // accident of being equal. Three do not: dropping delete when the keyboard is
    // up would put the undo row's top edge at two thirds and leave the panel with
    // no line running across it. The clipboard keys take up the slack instead, so
    // the bottom band is one band in both states.
    val clipboardKeyWeight = if (keyboardShown) 1.5f else 1.0f

    Column(modifier = modifier) {
        ActionKey(
            modifier = Modifier
                .weight(clipboardKeyWeight)
                .fillMaxWidth(),
            repeatable = false,
            color = keys.functionalKey,
            onTrigger = { onEvent(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON) }
        ) {
            IconWithColor(
                iconId = R.drawable.copy,
                iconColor = keys.onKey
            )
        }

        ActionKey(
            modifier = Modifier
                .weight(clipboardKeyWeight)
                .fillMaxWidth(),
            repeatable = false,
            color = keys.functionalKey,
            onTrigger = { onEvent(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON) }
        ) {
            IconWithColor(
                iconId = R.drawable.clipboard,
                iconColor = keys.onKey
            )
        }

        if(!keyboardShown) {
            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                repeatable = true,
                color = keys.functionalKey,
                onTrigger = { onCodePoint(Constants.CODE_DELETE) }
            ) {
                IconWithColor(
                    iconId = R.drawable.delete,
                    iconColor = keys.onKey
                )
            }
        }


        Row(modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth()) {
            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight(),
                repeatable = false,
                color = keys.functionalKey,
                onTrigger = { onEvent(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) }
            ) {
                IconWithColor(
                    iconId = R.drawable.undo,
                    iconColor = keys.onKey
                )
            }

            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight(),
                repeatable = false,
                color = keys.functionalKey,
                onTrigger = { onEvent(KeyEvent.KEYCODE_Y, KeyEvent.META_CTRL_ON) }
            ) {
                IconWithColor(
                    iconId = R.drawable.redo,
                    iconColor = keys.onKey
                )
            }
        }
    }
}

enum class Direction {
    Left,
    Right,
    Up,
    Down
}

@Composable
fun TextEditScreen(
    onCodePoint: (Int) -> Unit,
    onEvent: (Int, Int) -> Unit,
    moveCursor: (direction: Direction, ctrl: Boolean, shift: Boolean) -> Unit,
    keyboardShown: Boolean
) {
    val shiftState = remember { mutableStateOf(false) }
    val ctrlState = remember { mutableStateOf(false) }

    val sendMoveCursor = { direction: Direction -> moveCursor(direction, ctrlState.value, shiftState.value) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(3.0f)) {
            ArrowKeys(
                modifier = Modifier
                    .weight(3.0f)
                    .fillMaxWidth(),
                moveCursor = sendMoveCursor
            )
            CtrlShiftMetaKeys(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                ctrlState = ctrlState,
                shiftState = shiftState
            )
        }
        SideKeys(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.0f),
            onEvent = onEvent,
            onCodePoint = onCodePoint,
            keyboardShown = keyboardShown
        )
    }
}

val TextEditAction = Action(
    icon = R.drawable.edit_text,
    name = R.string.settings_action_text_editor,
    simplePressImpl = null,
    persistentState = null,
    canShowKeyboard = true,
    windowImpl = { manager, persistentState ->
        object : ActionWindow() {
            @Composable
            override fun windowName(): String {
                return stringResource(R.string.settings_action_text_editor)
            }

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                val view = LocalView.current
                TextEditScreen(
                    onCodePoint = { a ->
                        manager.sendCodePointEvent(a)
                        manager.performHapticAndAudioFeedback(a, view)
                    },
                    onEvent = { a, b ->
                        manager.sendKeyEvent(a, b)
                        manager.performHapticAndAudioFeedback(Constants.CODE_TAB, view)
                    },
                    moveCursor = { direction, ctrl, shift ->
                        val keyEventMetaState = 0 or
                                (if(shift) { KeyEvent.META_SHIFT_ON } else { 0 }) or
                                (if(ctrl) { KeyEvent.META_CTRL_ON } else { 0 })

                         when {
                            keyEventMetaState == 0 && direction == Direction.Left ->
                                manager.activateAction(ArrowLeftAction)
                            keyEventMetaState == 0 && direction == Direction.Right ->
                                manager.activateAction(ArrowRightAction)
                            keyEventMetaState == 0 && direction == Direction.Up ->
                                manager.activateAction(ArrowUpAction)
                            keyEventMetaState == 0 && direction == Direction.Down ->
                                manager.activateAction(ArrowDownAction)

                            direction == Direction.Left -> manager.cursorLeft(1, stepOverWords = ctrl, select = shift)
                            direction == Direction.Right -> manager.cursorRight(1, stepOverWords = ctrl, select = shift)
                            direction == Direction.Up -> manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP, keyEventMetaState)
                            direction == Direction.Down -> manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, keyEventMetaState)
                        }

                        manager.performHapticAndAudioFeedback(Constants.CODE_TAB, view)
                    },
                    keyboardShown = keyboardShown
                )
            }
        }
    }
)

@Composable
@Preview(showBackground = true)
fun TextEditScreenPreview() {
    Surface(modifier = Modifier.height(256.dp)) {
        TextEditScreen(onCodePoint = { }, onEvent = { _, _ -> }, moveCursor = { _, _, _ -> }, keyboardShown = false)
    }
}
@Composable
@Preview(showBackground = true)
fun TextEditScreenPreviewWithKb() {
    Surface(modifier = Modifier.height(256.dp)) {
        TextEditScreen(onCodePoint = { }, onEvent = { _, _ -> }, moveCursor = { _, _, _ -> }, keyboardShown = true)
    }
}