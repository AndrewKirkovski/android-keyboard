package org.futo.inputmethod.latin.uix.actions

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Indication
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import org.futo.inputmethod.v2keyboard.KeyVisualStyle
import kotlin.math.roundToInt

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
    val key = panelKey(if (toggled) KeyVisualStyle.StickyOn else KeyVisualStyle.Functional)

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
            .keyBackground(if (isPressed) key.backgroundPressed else key.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            ),
        shape = RoundedCornerShape(8.dp),
        color = key.fill
    ) {
        contents(key.content)
    }

}

@Composable
fun Modifier.repeatablyClickableAction(
    repeatable: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    onTrigger: (Boolean) -> Unit
): Modifier {
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
        indication = indication,
        onClick = { }
    )
}

/**
 * The keyboard's own drawing of a key, for a panel that is built out of keys.
 *
 * [BasicThemeProvider] gives each key style a drawable rather than a colour, because a
 * theme can put more on a key than a fill: a corner radius, and a shadow drawn through
 * Paint.setShadowLayer. The panel used to rebuild that out of colour-scheme roles and
 * reproduce the rest, and the shadow is what showed the cost of reproducing it --
 * Compose's elevation scales the shadow colour it is handed by the theme's own shadow
 * alpha, and ignores it entirely below API 28, so a panel matched against one theme
 * came out wrong on the next. Reading the style hands over fill, radius, shadow and
 * pressed state together, and they are the key's rather than a copy of the key's.
 *
 * The three styles the panel uses are the three it is made of: an ordinary key, a
 * functional key, and the one the keyboard puts on a latched modifier --
 * KeyVisualStyle.StickyOn, which is secondary rather than the enter key's primary.
 * Key borders need no handling here, whatever the provider does with them: an
 * ordinary or functional key goes transparent and loses its shadow, a latched one
 * keeps its fill, and the panel matches the keyboard in both cases because it is
 * asking the same question.
 *
 * What it does not ask about is a per-key background image, which an imported theme
 * can set and the keyboard resolves ahead of the style. A panel key takes the style
 * either way, so on such a theme it follows the keyboard's colours and not its
 * bitmaps.
 */
private class PanelKey(
    val background: Drawable?,
    val backgroundPressed: Drawable?,
    /** Stands in for [background] where there is none: a preview, or a style with none. */
    val fill: Color,
    val content: Color
)

@Composable
private fun panelKey(style: KeyVisualStyle): PanelKey {
    // A preview has no theme provider, and reading it there throws. It gets flat colour
    // from the scheme instead: a preview shows the arrangement, not the material.
    if (LocalInspectionMode.current) {
        val scheme = LocalKeyboardScheme.current
        val latched = style == KeyVisualStyle.StickyOn
        return PanelKey(
            background = null,
            backgroundPressed = null,
            fill = when {
                latched -> scheme.secondary
                style == KeyVisualStyle.Functional -> scheme.keyboardContainerVariant
                else -> scheme.keyboardContainer
            },
            content = if (latched) scheme.onSecondary else scheme.onKeyboardContainer
        )
    }

    val provider = LocalThemeProvider.current
    val key = provider.getKeyStyleDescriptor(style)
    return PanelKey(
        background = key.backgroundDrawable,
        backgroundPressed = key.backgroundDrawablePressed,
        fill = Color.Transparent,
        // The background is the key's; the icon colour is not. A style's
        // foregroundColor is emptied by touch typing mode, which hides the letters
        // so their positions have to be learned -- a rule about labels, and the
        // provider does not apply it to the action bar's icons. The panel is icons
        // too, so it takes the same colour they do. A latched key keeps the style's,
        // which is onSecondary and is not something that mode touches.
        content = if (style == KeyVisualStyle.StickyOn) {
            Color(key.foregroundColor)
        } else {
            Color(provider.onKeyColor)
        }
    )
}

/**
 * Draws a key background behind the content, at the size the key is.
 *
 * A drawable that casts a shadow insets its fill to leave the shadow room, and reports
 * that inset through getPadding -- so growing the bounds by it lands the fill exactly on
 * the box being modified and puts the shadow in the gap around it, which is where the
 * keyboard's own shadows fall. A drawable with no shadow reports nothing and lands
 * unchanged.
 *
 * The result is kept as an image rather than redrawn each frame. One drawable is shared
 * by every key of a style, and the shadowed one caches a bitmap against the last bounds
 * it drew at, so keys of two sizes taking turns would each throw away the other's.
 */
private fun Modifier.keyBackground(drawable: Drawable?): Modifier =
    if (drawable == null) this else drawWithCache {
        val spill = Rect()
        drawable.getPadding(spill)
        val width = size.width.roundToInt() + spill.left + spill.right
        val height = size.height.roundToInt() + spill.top + spill.bottom

        // The key's own size, not the grown one: a shadow's spill is non-zero, so a
        // node measured to nothing still gives a positive width here and would paint
        // a smudge of shadow with no key inside it.
        val image = if (size.width >= 1f && size.height >= 1f) {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                drawable.setBounds(0, 0, width, height)
                drawable.draw(android.graphics.Canvas(it))
            }.asImageBitmap()
        } else {
            null
        }

        val origin = Offset(-spill.left.toFloat(), -spill.top.toFloat())
        onDrawBehind {
            image?.let { drawImage(it, topLeft = origin) }
        }
    }

@Composable
fun ActionKey(
    onTrigger: () -> Unit,
    modifier: Modifier = Modifier,
    repeatable: Boolean = true,
    style: KeyVisualStyle = KeyVisualStyle.Normal,
    contents: @Composable () -> Unit
) {
    val key = panelKey(style)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .padding(4.dp)
            .keyBackground(if (pressed) key.backgroundPressed else key.background)
            .repeatablyClickableAction(
                repeatable = repeatable,
                interactionSource = interactionSource,
                // The pressed drawable is the keyboard's own press feedback, and a
                // ripple over it would be a second one.
                indication = null,
                onTrigger = { onTrigger() }
            ),
        shape = RoundedCornerShape(8.dp),
        color = key.fill
    ) {
        contents()
    }
}

@Composable
fun ArrowKeys(
    modifier: Modifier,
    moveCursor: (direction: Direction) -> Unit
) {
    val key = panelKey(KeyVisualStyle.Normal)

    Row(modifier = modifier) {
        ActionKey(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight(),
            onTrigger = { moveCursor(Direction.Left) }
        ) {
            IconWithColor(
                iconId = R.drawable.arrow_left,
                iconColor = key.content
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
                    iconColor = key.content
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
                    iconColor = key.content
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
                iconColor = key.content
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
    val key = panelKey(KeyVisualStyle.Functional)

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
            style = KeyVisualStyle.Functional,
            onTrigger = { onEvent(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON) }
        ) {
            IconWithColor(
                iconId = R.drawable.copy,
                iconColor = key.content
            )
        }

        ActionKey(
            modifier = Modifier
                .weight(clipboardKeyWeight)
                .fillMaxWidth(),
            repeatable = false,
            style = KeyVisualStyle.Functional,
            onTrigger = { onEvent(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON) }
        ) {
            IconWithColor(
                iconId = R.drawable.clipboard,
                iconColor = key.content
            )
        }

        if(!keyboardShown) {
            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                repeatable = true,
                style = KeyVisualStyle.Functional,
                onTrigger = { onCodePoint(Constants.CODE_DELETE) }
            ) {
                IconWithColor(
                    iconId = R.drawable.delete,
                    iconColor = key.content
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
                style = KeyVisualStyle.Functional,
                onTrigger = { onEvent(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) }
            ) {
                IconWithColor(
                    iconId = R.drawable.undo,
                    iconColor = key.content
                )
            }

            ActionKey(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight(),
                repeatable = false,
                style = KeyVisualStyle.Functional,
                onTrigger = { onEvent(KeyEvent.KEYCODE_Y, KeyEvent.META_CTRL_ON) }
            ) {
                IconWithColor(
                    iconId = R.drawable.redo,
                    iconColor = key.content
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