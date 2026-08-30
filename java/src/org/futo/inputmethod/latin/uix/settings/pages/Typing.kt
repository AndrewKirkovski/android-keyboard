package org.futo.inputmethod.latin.uix.settings.pages

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import org.futo.inputmethod.accessibility.AccessibilityUtils
import org.futo.inputmethod.engine.IMESettingsMenu
import org.futo.inputmethod.latin.HideKeyboardWhenHardKeyboardConnected
import org.futo.inputmethod.latin.uix.theme.app.Spacing
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.settings.LongPressKey
import org.futo.inputmethod.latin.settings.LongPressKeyLayoutSetting
import org.futo.inputmethod.latin.settings.Settings
import org.futo.inputmethod.latin.settings.Settings.PREF_KEYPRESS_SOUND_VOLUME
import org.futo.inputmethod.latin.settings.Settings.PREF_VIBRATION_DURATION_SETTINGS
import org.futo.inputmethod.latin.settings.description
import org.futo.inputmethod.latin.settings.name
import org.futo.inputmethod.latin.settings.toEncodedString
import org.futo.inputmethod.latin.settings.toLongPressKeyLayoutItems
import org.futo.inputmethod.latin.uix.AndroidTextInput
import org.futo.inputmethod.latin.uix.BasicThemeProvider
import org.futo.inputmethod.latin.uix.KeyHintsSetting
import org.futo.inputmethod.latin.uix.theme.currentKeyboardScheme
import org.futo.inputmethod.latin.uix.SHOW_EMOJI_SUGGESTIONS
import org.futo.inputmethod.latin.uix.SettingsKey
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.setSettingBlocking
import org.futo.inputmethod.latin.uix.settings.BottomSpacer
import org.futo.inputmethod.latin.uix.settings.DataStoreItem
import org.futo.inputmethod.latin.uix.settings.DropDownPickerSettingItem
import org.futo.inputmethod.latin.uix.settings.GlyphIcon
import org.futo.inputmethod.latin.uix.settings.LocalSharedPrefsCache
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.PrimarySettingToggleDataStoreItem
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.settings.SettingItem
import org.futo.inputmethod.latin.uix.settings.SettingRadio
import org.futo.inputmethod.latin.uix.settings.SettingSlider
import org.futo.inputmethod.latin.uix.settings.SettingSliderSharedPrefsInt
import org.futo.inputmethod.latin.uix.settings.SettingToggleRaw
import org.futo.inputmethod.latin.uix.settings.SettingsCard
import org.futo.inputmethod.latin.uix.settings.SettingsRowDivider
import org.futo.inputmethod.latin.uix.settings.SyncDataStoreToPreferencesFloat
import org.futo.inputmethod.latin.uix.settings.SyncDataStoreToPreferencesInt
import org.futo.inputmethod.latin.uix.settings.Tip
import org.futo.inputmethod.latin.uix.settings.UserSetting
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.render
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.settings.useSharedPrefsBool
import org.futo.inputmethod.latin.uix.settings.useSharedPrefsInt
import org.futo.inputmethod.latin.uix.settings.userSettingDecorationOnly
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingSection
import org.futo.inputmethod.latin.uix.settings.userSettingToggleDataStore
import org.futo.inputmethod.latin.uix.settings.userSettingToggleSharedPrefs
import org.futo.inputmethod.v2keyboard.KeyboardSettings
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign

val vibrationDurationSetting = SettingsKey(
    intPreferencesKey("vibration_duration"),
    -1
)

val keySoundVolumeSetting = SettingsKey(
    floatPreferencesKey("key_sound_volume"),
    0.0f
)

val ActionBarDisplayedSetting = SettingsKey(
    booleanPreferencesKey("enable_action_bar"),
    true
)

val InlineAutofillSetting = SettingsKey(
    booleanPreferencesKey("inline_autofill"),
    true
)

// The exit button sits on the inner edge of the one-handed gutter, low, which is
// the part of the screen a thumb sweeps through constantly while typing
// one-handed. Hiding it does not strand anyone: the "Keyboard modes" Standard
// tile leaves one-handed mode independently, and long-pressing the switch-hands
// chevron does too, so the gesture survives the button.
val HideOneHandedExitButtonSetting = SettingsKey(
    booleanPreferencesKey("hide_one_handed_exit_button"),
    false
)

val ResizeMenuLite = UserSettingsMenu(
    title = R.string.settings_title_resize,
    navPath = "resize", registerNavPath = false,
    settings = listOf(
        userSettingNavigationItem(
            title = R.string.size_settings_reset,
            subtitle = R.string.size_settings_reset_subtitle,
            style = NavigationItemStyle.Misc,
            navigate = { nav ->
                KeyboardSettings.values.forEach {
                    nav.context.setSettingBlocking(it.key, it.default)
                }
            }
        ),
        userSettingToggleDataStore(
            title = R.string.size_settings_hide_one_handed_exit,
            subtitle = R.string.size_settings_hide_one_handed_exit_subtitle,
            setting = HideOneHandedExitButtonSetting
        )
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun ResizeScreen(navController: NavHostController = rememberNavController()) {
    val textInputService = LocalTextInputService.current
    val session = remember { mutableStateOf<TextInputSession?>(null) }

    DisposableEffect(Unit) {
        session.value = textInputService?.startInput(
            TextFieldValue(""),
            imeOptions = ImeOptions.Default.copy(
                platformImeOptions = PlatformImeOptions(
                    privateImeOptions = "org.futo.inputmethod.latin.ResizeMode=1"
                )
            ),
            onEditCommand = { },
            onImeActionPerformed = { }
        )

        onDispose {
            textInputService?.stopInput(session.value ?: return@onDispose)
        }
    }

    Box {
        ScrollableList {
            ScreenTitle(stringResource(R.string.settings_title_resize), showBack = true, navController)

            Tip {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.size_settings_keyboard_modes_tip))
                        append(" ")
                        appendInlineContent("icon")
                        appendLine()
                        append(stringResource(R.string.size_settings_keyboard_modes_portrait_landscape_tip))
                        appendLine()
                        append(stringResource(R.string.size_settings_resize_tip))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current,
                    inlineContent = mapOf(
                        "icon" to InlineTextContent(
                            Placeholder(
                                width = with(LocalDensity.current) { 24.dp.toPx().toSp() },
                                height = with(LocalDensity.current) { 24.dp.toPx().toSp() },
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                            )
                        ){
                            Icon(painterResource(R.drawable.keyboard_gear), contentDescription = null)
                        }
                    ))
            }

            Spacer(Modifier.height(8.dp))
            ResizeMenuLite.render(showTitle = false)

            AndroidTextInput(allowPredictions = false, customOptions = setOf("org.futo.inputmethod.latin.ResizeMode"), autoshow = false)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DraggableSettingItem(idx: Int, item: LongPressKey, moveItem: (LongPressKey, Int) -> Unit, disable: (LongPressKey) -> Unit, dragIcon: @Composable () -> Unit, limits: IntRange) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val talkBackOn = remember {
        AccessibilityUtils.init(context)
        AccessibilityUtils.getInstance().isAccessibilityEnabled
    }

    val customActions = remember(idx, limits, item, resources) {
        buildList {
            if (idx > limits.first) {
                add(
                    CustomAccessibilityAction(
                        resources.getString(R.string.morekey_settings_move_kind_up)
                    ) {
                        moveItem(item, -1)
                        true
                    }
                )

                add(
                    CustomAccessibilityAction(
                        resources.getString(R.string.morekey_settings_move_kind_up_to_top)
                    ) {
                        moveItem(item, -100)
                        true
                    }
                )
            }
            if (idx < limits.last) {
                add(
                    CustomAccessibilityAction(
                        resources.getString(R.string.morekey_settings_move_kind_down)
                    ) {
                        moveItem(item, 1)
                        true
                    }
                )
                add(
                    CustomAccessibilityAction(
                        resources.getString(R.string.morekey_settings_move_kind_down_to_bottom)
                    ) {
                        moveItem(item, 100)
                        true
                    }
                )
            }
            add(
                CustomAccessibilityAction(
                    resources.getString(R.string.morekey_settings_disable)
                ) {
                    disable(item)
                    true
                }
            )
        }
    }

    val semantics = Modifier.clearAndSetSemantics {
        contentDescription = item.name(resources)
        stateDescription = resources.getString(
            R.string.morekey_settings_kind_position,
            (idx + 1).toString(),
            (limits.last + 1).toString()
        )

        if (talkBackOn) {
            this.customActions = customActions
        }
    }

    val dragging = remember { mutableStateOf(false) }
    val offset = remember { mutableFloatStateOf(0.0f) }
    val height = remember { mutableIntStateOf(1) }

    val pendingOffsetDiff = remember { mutableFloatStateOf(0.0f) }
    LaunchedEffect(idx, pendingOffsetDiff.floatValue) {
        if(pendingOffsetDiff.floatValue != 0.0f) {
            offset.floatValue += pendingOffsetDiff.floatValue
            pendingOffsetDiff.floatValue = 0.0f
        }
    }

    val shouldClampLower = (idx - 1) < limits.first
    val shouldClampUpper = (idx + 1) > limits.last

    SettingItem(
        title = "${idx+1}. " + item.name(resources),
        subtitle = item.description(resources),
        modifier = semantics
            .onSizeChanged { size -> height.intValue = size.height }
            .let { modifier ->
                if (!dragging.value) {
                    // No zebra striping. It alternated two alphas of surfaceTint, which
                    // in this palette is the crimson accent, so a list of five items got
                    // pink bands across it -- and the bands said nothing the numbers in
                    // the titles do not already say.
                    modifier
                } else {
                    modifier
                        .zIndex(10.0f)
                        .graphicsLayer {
                            clip = false
                            translationX = 0.0f
                            translationY = offset.floatValue.let {
                                if (shouldClampLower && it < 0.0f) 0.0f
                                else if (shouldClampUpper && it > 0.0f) 0.0f
                                else it
                            }
                        }
                        .background(
                            MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)
                                .compositeOver(MaterialTheme.colorScheme.background)
                        )
                }
            }
    ) {
        IconButton(onClick = { disable(item) }) {
            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.morekey_settings_disable))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LongPressKeyLayoutEditor(context: Context, setting: DataStoreItem<String>) {
    val resources = LocalResources.current
    // A section header and a text button, not a 20sp title and a filled pill. The
    // title read as a second screen heading inside a card, and a filled Button is the
    // weight this app reserves for a primary action -- reset is the opposite of one.
    Row(
        Modifier.padding(start = Spacing.rowInset, end = Spacing.s),
        verticalAlignment = CenterVertically
    ) {
        Text(
            stringResource(R.string.morekey_settings_layout),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1.0f)
        )
        TextButton(onClick = {
            setting.setValue(LongPressKeyLayoutSetting.default)
        }) {
            Text(stringResource(R.string.morekey_settings_reset))
        }
    }


    val dragIcon: @Composable () -> Unit = {
        Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
    }

    val items = setting.value.toLongPressKeyLayoutItems()

    val moveItem: (item: LongPressKey, direction: Int) -> Unit = { item, direction ->
        val oldItems = context.getSettingBlocking(LongPressKeyLayoutSetting).toLongPressKeyLayoutItems()
        val oldIdx = oldItems.indexOf(item)

        val insertIdx = (oldIdx + direction).coerceAtLeast(0).coerceAtMost(oldItems.size - 1)

        val newItems = oldItems.filter { it != item }.toMutableList().apply {
            add(insertIdx, item)
        }.toEncodedString()

        setting.setValue(newItems)
    }

    val disable: (item : LongPressKey) -> Unit = { item ->
        val oldItems = context.getSettingBlocking(LongPressKeyLayoutSetting).toLongPressKeyLayoutItems()

        val newItems = oldItems.filter { it != item }.toEncodedString()

        setting.setValue(newItems)

    }

    val enable: (item : LongPressKey) -> Unit = { item ->
        val oldItems = context.getSettingBlocking(LongPressKeyLayoutSetting).toLongPressKeyLayoutItems()

        val newItems = oldItems.filter { it != item }.toMutableList().apply {
            add(item)
        }.toEncodedString()

        setting.setValue(newItems)
    }

    if(items.isNotEmpty()) {
        Text(
            stringResource(R.string.morekey_settings_active),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.rowInset, top = Spacing.s, bottom = Spacing.xs)
        )
        SettingsCard {
            Column(Modifier.semantics {
                collectionInfo = CollectionInfo(
                    rowCount = items.size,
                    columnCount = 1
                )
                contentDescription = resources.getString(R.string.morekey_settings_active)
            }) {
                items.forEachIndexed { i, v ->
                    key(v.ordinal) {
                        if (i > 0) SettingsRowDivider()
                        DraggableSettingItem(
                            idx = i,
                            item = v,
                            moveItem = moveItem,
                            disable = disable,
                            dragIcon = dragIcon,
                            limits = items.indices
                        )
                    }
                }
            }
        }
    }

    val inactiveEntries = LongPressKey.entries.filter { !items.contains(it) }
    if(inactiveEntries.isNotEmpty()) {
        Text(
            stringResource(R.string.morekey_settings_inactive),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.rowInset, top = Spacing.s, bottom = Spacing.xs)
        )
        SettingsCard {
            Column(Modifier.semantics {
                collectionInfo = CollectionInfo(
                    rowCount = inactiveEntries.size,
                    columnCount = 1
                )
                contentDescription = resources.getString(R.string.morekey_settings_inactive)
            }) {
                inactiveEntries.forEachIndexed { i, entry ->
                    if (i > 0) SettingsRowDivider()
                    SettingItem(
                        title = entry.name(resources),
                        subtitle = entry.description(resources),
                        modifier = Modifier.clearAndSetSemantics {
                            contentDescription = entry.name(resources)

                            onClick(label = resources.getString(R.string.morekey_settings_reactivate)) {
                                enable(entry)
                                true
                            }
                        }
                    ) {
                        IconButton(onClick = { enable(entry) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.morekey_settings_reactivate)
                            )
                        }
                    }
                }
            }
        }
    }
}

val LongPressMenu = UserSettingsMenu(
    title = R.string.settings_row_longpress,
    navPath = "longPress", registerNavPath = true,
    settings = listOf(
        // The Home row that opens this screen promises "configure long-press
        // duration", so the duration is the first thing on it rather than the
        // last. The first card carries no header, as on every other screen.
        UserSetting(
            name = R.string.settings_row_longpress_duration,
            subtitle = R.string.settings_sub_longpress_duration,
        ) {
            val resources = LocalResources.current
            SettingSliderSharedPrefsInt(
                title = stringResource(R.string.settings_row_longpress_duration),
                subtitle = stringResource(R.string.settings_sub_longpress_duration),
                key = Settings.PREF_KEY_LONGPRESS_TIMEOUT,
                default = 300,
                range = 100.0f..700.0f,
                hardRange = 25.0f..1200.0f,
                transform = { it.roundToInt() },
                indicator = { resources.getString(R.string.abbreviation_unit_milliseconds, "$it") },
                steps = 23
            )
        },

        userSettingSection(R.string.morekey_settings_backspace_title),

        UserSetting(name = R.string.morekey_settings_backspace_hold_delete_words) {
            val oldSetting = useSharedPrefsInt(
                key = Settings.PREF_BACKSPACE_MODE,
                default = Settings.BACKSPACE_MODE_CHARACTERS
            )

            val setting = useSharedPrefsInt(
                key = Settings.PREF_BACKSPACE_MODE_HOLD,
                default = oldSetting.value
            )

            SettingToggleRaw(
                title = stringResource(R.string.morekey_settings_backspace_hold_delete_words),
                enabled = setting.value == Settings.BACKSPACE_MODE_WORDS,
                setValue = { to ->
                    setting.setValue(if(to) Settings.BACKSPACE_MODE_WORDS else Settings.BACKSPACE_MODE_CHARACTERS)
                }
            )
        },

        UserSetting(name = R.string.morekey_settings_backspace_swipe_to_delete) {
            val setting = useSharedPrefsInt(
                key = Settings.PREF_BACKSPACE_MODE,
                default = Settings.BACKSPACE_MODE_CHARACTERS
            )

            val deleteModes = mapOf(
                Settings.BACKSPACE_MODE_OFF to stringResource(R.string.morekey_settings_backspace_swipe_to_delete_off),
                Settings.BACKSPACE_MODE_CHARACTERS to stringResource(R.string.morekey_settings_backspace_swipe_to_delete_characters),
                Settings.BACKSPACE_MODE_WORDS to stringResource(R.string.morekey_settings_backspace_swipe_to_delete_words)
            )

            DropDownPickerSettingItem(
                label = stringResource(R.string.morekey_settings_backspace_swipe_to_delete),
                options = deleteModes.keys.toList(),
                selection = setting.value,
                onSet = { setting.setValue(it) },
                getDisplayName = { deleteModes[it] ?: "?" }
            )
        },


        userSettingSection(R.string.morekey_settings_spacebar_title),

        UserSetting(name = R.string.morekey_settings_spacebar_swipe_shortcut) {
            val setting = useSharedPrefsInt(
                key = Settings.PREF_SPACEBAR_SWIPE_MODE,
                default = remember { Settings.getInstance().current.mSpacebarSwipeMode }
            )

            val modes = mapOf(
                Settings.SPACEBAR_MODE_OFF to stringResource(R.string.morekey_settings_spacebar_swipe_shortcut_off),
                Settings.SPACEBAR_MODE_CURSOR to stringResource(R.string.morekey_settings_spacebar_swipe_shortcut_cursor),
                Settings.SPACEBAR_MODE_LANGUAGE to stringResource(R.string.morekey_settings_spacebar_swipe_shortcut_language)
            )

            DropDownPickerSettingItem(
                label = stringResource(R.string.morekey_settings_spacebar_swipe_shortcut),
                options = modes.keys.toList(),
                selection = setting.value,
                onSet = { setting.setValue(it) },
                getDisplayName = { modes[it] ?: "?" }
            )
        },

        UserSetting(name = R.string.morekey_settings_spacebar_hold_shortcut) {
            val setting = useSharedPrefsInt(
                key = Settings.PREF_SPACEBAR_HOLD_MODE,
                default = remember { Settings.getInstance().current.mSpacebarHoldMode }
            )

            val modes = mapOf(
                Settings.SPACEBAR_MODE_CURSOR to stringResource(R.string.morekey_settings_spacebar_hold_shortcut_cursor),
                Settings.SPACEBAR_MODE_LANGUAGE to stringResource(R.string.morekey_settings_spacebar_hold_shortcut_language)
            )

            DropDownPickerSettingItem(
                label = stringResource(R.string.morekey_settings_spacebar_hold_shortcut),
                options = modes.keys.toList(),
                selection = setting.value,
                onSet = { setting.setValue(it) },
                getDisplayName = { modes[it] ?: "?" }
            )
        },

        // TODO: Might not work well for showing up in search
        // This one draws its own headers and cards, so it must not be folded into
        // the card above it. Without the break, its "Layout of long-press keys"
        // header and its Reset action rendered inside the Spacebar card, which
        // read as though Reset would undo the spacebar settings.
        UserSetting(name = R.string.morekey_settings_layout, breaksCardGroup = true) {
            val context = LocalContext.current
            val setting = useDataStore(LongPressKeyLayoutSetting)
            LongPressKeyLayoutEditor(
                context = context,
                setting = setting
            )
        }
    )
)

@Composable
internal fun AutoSpacesSetting() {
    val altSpacesMode = useSharedPrefsInt(Settings.PREF_ALT_SPACES_MODE, Settings.DEFAULT_ALT_SPACES_MODE)
    val autoSpaceModes = mapOf(
        Settings.SPACES_MODE_ALL to stringResource(R.string.typing_settings_auto_space_mode_auto2),
        Settings.SPACES_MODE_SUGGESTIONS to stringResource(R.string.typing_settings_auto_space_mode_suggestions2),
        Settings.SPACES_MODE_LEGACY to stringResource(R.string.typing_settings_auto_space_mode_legacy2),
        Settings.SPACES_MODE_NONE to stringResource(R.string.typing_settings_auto_space_mode_none2)
    )
    DropDownPickerSettingItem(
        label = stringResource(R.string.typing_settings_auto_space_mode),
        options = autoSpaceModes.keys.toList(),
        selection = altSpacesMode.value,
        onSet = {
            altSpacesMode.setValue(it)
        },
        getDisplayName = {
            autoSpaceModes[it] ?: "?"
        }
    )
}

val KeyboardSettingsMenu = UserSettingsMenu(
    // The same string the Home row that opens this screen uses. They disagreed:
    // the row said "Keys & layout" and the screen it opened said "Keyboard".
    title = R.string.keys_layout_settings_title,
    navPath = "keyboard", registerNavPath = true,
    settings = listOf(
        userSettingNavigationItem(
            title = R.string.settings_title_resize,
            subtitle = R.string.size_settings_subtitle2,
            style = NavigationItemStyle.Misc,
            navigateTo = "resize"
        ),
        userSettingSection(R.string.keyboard_settings_rows_section),
        userSettingToggleSharedPrefs(
            title = R.string.settings_row_number_row,
            subtitle = R.string.keyboard_settings_show_number_row_subtitle,
            key = Settings.PREF_ENABLE_NUMBER_ROW,
            default = {false}
        ),
        // Both were behind a submenu whose only other content was a second copy of the
        // switch above. They already carry the visibility check that hides them when the
        // number row is off, so inline they simply appear under the switch that turns
        // them on -- which is what the submenu was standing in for.
        userSettingToggleSharedPrefs(
            R.string.keyboard_settings_number_row_dont_use_script_digits,
            default = {false},
            key = Settings.PREF_USE_WESTERN_NUMERALS
        ).copy(visibilityCheck = {
            useSharedPrefsBool(Settings.PREF_ENABLE_NUMBER_ROW, false).value
        }),
        UserSetting(name = R.string.keyboard_settings_number_row_style, visibilityCheck = {
            useSharedPrefsBool(Settings.PREF_ENABLE_NUMBER_ROW, false).value
        }) {
            val context = LocalContext.current
            // A picture of a real key, so it uses the keyboard's theme rather than the
            // app's. LocalKeyboardScheme is not provided off the keyboard -- its static
            // default is a light scheme -- so resolve the user's choice directly.
            val scheme = currentKeyboardScheme()
            val provider = remember(scheme) {
                BasicThemeProvider(context, scheme)
            }
            val keySize = with(LocalDensity.current) {
                32.dp.toPx() to 48.dp.toPx()
            }
            val background = remember(provider) {
                provider.keyBackground.toBitmap(
                    width = keySize.first.toInt(),
                    height = keySize.second.toInt()
                ).asImageBitmap()
            }

            val measurer = rememberTextMeasurer()
            val textSizePx = background.height / 2f
            val textSizeSp = with(LocalDensity.current) { textSizePx.toSp() }
            val color = scheme.onKeyboardContainer

            val textLayoutResult = measurer.measure(
                text = "1",
                style = TextStyle(
                    fontSize = textSizeSp,
                    color = color,
                    textAlign = TextAlign.Center
                )
            )

            SettingRadio(
                title = stringResource(R.string.keyboard_settings_number_row_style),
                options = listOf(
                    Settings.NUMBER_ROW_MODE_DEFAULT,
                    Settings.NUMBER_ROW_MODE_CLASSIC
                ),
                optionNames = listOf(
                    stringResource(R.string.keyboard_settings_number_row_style_default),
                    stringResource(R.string.keyboard_settings_number_row_style_classic)
                ),
                setting = useSharedPrefsInt(
                    key = Settings.PREF_NUMBER_ROW_MODE,
                    default = Settings.NUMBER_ROW_MODE_DEFAULT
                ),
                hints = listOf(
                    {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.size(32.dp, 48.dp)
                        ) {
                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(
                                    x = background.width / 2.0f - textLayoutResult.size.width / 2.0f,
                                    y = background.height / 2.0f - textLayoutResult.size.height / 2.0f
                                )
                            )
                        }
                    },
                    {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.size(32.dp, 48.dp)
                        ) {
                            drawImage(background)
                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(
                                    x = background.width / 2.0f - textLayoutResult.size.width / 2.0f,
                                    y = background.height / 2.0f - textLayoutResult.size.height / 2.0f
                                )
                            )
                        }
                    }
                )
            )
        },
        userSettingToggleSharedPrefs(
            title = R.string.settings_row_arrow_keys,
            subtitle = R.string.keyboard_settings_show_arrow_row_subtitle,
            key = Settings.PREF_ENABLE_ARROW_ROW,
            default = {false}
        ),
        userSettingSection(R.string.keyboard_settings_keys_section),
        userSettingNavigationItem(
            title = R.string.morekey_settings_title,
            subtitle = R.string.morekey_settings_subtitle,
            style = NavigationItemStyle.Misc,
            navigateTo = "longPress"
        ),
        userSettingToggleSharedPrefs(
            title = R.string.keyboard_settings_period_key,
            subtitle = R.string.keyboard_settings_period_key_subtitle2,
            key = Settings.PREF_ENABLE_ALT_PERIOD_KEY,
            default = {false}
        ),
        userSettingToggleDataStore(
            title = R.string.keyboard_settings_hide_when_hardware_keyboard_is_connected,
            setting = HideKeyboardWhenHardKeyboardConnected
        )
    )
)
