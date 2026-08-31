package org.futo.inputmethod.latin.uix.theme.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.KeyBordersSetting
import org.futo.inputmethod.latin.uix.KeyboardBackground
import org.futo.inputmethod.latin.uix.KeyboardColorScheme
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.actions.BugInfo
import org.futo.inputmethod.latin.uix.actions.BugViewerState
import org.futo.inputmethod.latin.uix.setSetting
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.SettingToggleDataStore
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.theme.ZipThemes
import org.futo.inputmethod.latin.uix.theme.ThemeOption
import org.futo.inputmethod.latin.uix.theme.ThemeOptionKeys
import org.futo.inputmethod.latin.uix.theme.Typography
import org.futo.inputmethod.latin.uix.theme.UixThemeWrapper
import org.futo.inputmethod.latin.uix.theme.defaultThemeOption
import org.futo.inputmethod.latin.uix.theme.getThemeOption
import org.futo.inputmethod.latin.uix.theme.presets.AMOLEDDarkPurple
import org.futo.inputmethod.latin.uix.theme.presets.ClassicMaterialDark
import org.futo.inputmethod.latin.uix.theme.presets.DefaultLightScheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicDarkTheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicLightTheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicSystemTheme
import org.futo.inputmethod.latin.uix.theme.presets.VoiceInputTheme
import org.futo.inputmethod.updates.openURI

@Composable
fun ThemePreview(theme: ThemeOption, isSelected: Boolean = false, overrideName: String? = null, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    if(theme == DynamicSystemTheme) return DynamicThemePreview(isSelected, onClick)

    val context = LocalContext.current
    val colors = remember(theme) { theme.obtainColors(context) }

    return ThemePreview(
        colors = colors,
        name = overrideName ?: stringResource(theme.name),
        loading = false,
        isSelected = isSelected,
        modifier = modifier,
        onClick = onClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemePreview(colors: KeyboardColorScheme, name: String, loading: Boolean, isSelected: Boolean = false, modifier: Modifier = Modifier, onLongClick: (() -> Unit)? = null, onClick: () -> Unit = { }) {
    val currColors = MaterialTheme.colorScheme

    val borderWidth = if (isSelected) {
        4.dp
    } else {
        Dp.Hairline
    }

    val borderColor = if (isSelected) {
        currColors.inversePrimary
    } else {
        currColors.outline
    }

    val textColor = colors.onBackground

    val spacebarColor = colors.keyboardContainer
    val actionColor = colors.primary

    val keyboardShape = RoundedCornerShape(8.dp)

    val previewModifier = if(LocalInspectionMode.current) {
        modifier.width(172.dp)
    } else {
        modifier
    }

    Box(
        modifier = previewModifier
            .padding(12.dp)
            .height(128.dp)
            .border(borderWidth, borderColor, keyboardShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .clip(keyboardShape),
    ) {
        KeyboardBackground(colors, useThumbnail = true)
        Box(modifier = Modifier.fillMaxSize()) {
            // Theme name and action bar
            Text(
                text = name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(colors.keyboardSurfaceDim.copy(
                        alpha = if(colors.extended.advancedThemeOptions.thumbnailImage == null) {
                            1.0f
                        } else {
                            0.4f
                        }
                    ))
                    .fillMaxWidth()
                    .padding(4.dp),
                color = textColor,
                style = Typography.SmallMl
            )

            // Keyboard contents
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Spacebar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(18.dp)
                        .align(Alignment.BottomCenter),
                    color = spacebarColor,
                    shape = RoundedCornerShape(4.dp)
                ) { }

                // Enter key
                Surface(
                    modifier = Modifier
                        .width(24.dp)
                        .height(18.dp)
                        .align(Alignment.BottomEnd)
                        .padding(0.dp, 1.dp),
                    color = actionColor,
                    shape = RoundedCornerShape(4.dp)
                ) { }
            }
        }
    }
}


@Composable
fun ZipThemePreview(name: ZipThemes.ThemeFileName, isSelected: Boolean, modifier: Modifier, onLongClick: (() -> Unit)? = null, onClick: () -> Unit) {
    val context = LocalContext.current

    val loading = remember { mutableStateOf(true) }
    val scheme = remember { mutableStateOf<KeyboardColorScheme>(defaultThemeOption(context).obtainColors(context)) }

    LaunchedEffect(name) {
        loading.value = true
        scheme.value = defaultThemeOption(context).obtainColors(context)
        withContext(Dispatchers.Default) {
            try {
                scheme.value = ZipThemes.loadSchemeThumb(context, name)
            }catch(e: Exception) {
                BugViewerState.pushBug(BugInfo(
                    name = "Unable to load thumbnail for $name",
                    details = e.toString(),
                ))
            }
        }
        loading.value = false
    }

    ThemePreview(
        colors = scheme.value,
        name = scheme.value.extended.advancedThemeOptions.themeName ?: stringResource(R.string.theme_custom_named, name.name),
        loading = loading.value,
        isSelected = isSelected,
        modifier = modifier,
        onLongClick = onLongClick,
        onClick = onClick,
    )
}

// Special case to demonstrate the light and dark mode
@Preview
@Composable
fun DynamicThemePreview(isSelected: Boolean = false, onClick: () -> Unit = { }) {
    Box {
        ThemePreview(
            DynamicLightTheme,
            isSelected = isSelected,
            onClick = onClick,
            overrideName = stringResource(DynamicSystemTheme.name),
            modifier = Modifier.clip(GenericShape { size, _ ->
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width * 0.66f, 0f)
                    lineTo(size.width * 0.33f, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                addPath(path)
            })
        )
        ThemePreview(
            DynamicDarkTheme,
            isSelected = isSelected,
            onClick = onClick,
            overrideName = stringResource(DynamicSystemTheme.name),
            modifier = Modifier.clip(GenericShape { size, _ ->
                val path = Path().apply {
                    moveTo(size.width * 0.66f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(size.width * 0.33f, size.height)
                    close()
                }
                addPath(path)
            })
        )
    }
}

/**
 * An action tile in the theme grid: an icon over the word for what it does.
 *
 * On `surfaceVariant` these were #F0EEE9 on a #F4F3EF ground -- four units apart, so in
 * light they read as empty space rather than as buttons. An icon on its own also left
 * "browse online" as something to guess at, next to eight tiles that all name
 * themselves. The card surface is what the rest of the app puts content on, and it
 * separates from the ground in both themes.
 */
@Composable
private fun ThemeActionTile(
    short: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(12.dp)
            .width(172.dp)
            .height(if (short) 80.dp else 128.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
        ) {
            icon()
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddCustomThemeButton(short: Boolean = false, onClick: () -> Unit = { }) {
    ThemeActionTile(
        short = short,
        label = stringResource(R.string.theme_settings_add_new_theme),
        onClick = onClick
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(if (short) 28.dp else 40.dp)
        )
    }
}

@Composable
fun VisitThemeStoreButton(short: Boolean = false) {
    val context = LocalContext.current
    ThemeActionTile(
        short = short,
        label = stringResource(R.string.theme_settings_visit_theme_store),
        onClick = { context.openURI("https://keyboard.futo.tech/themes", true) }
    ) {
        Icon(
            painterResource(R.drawable.compass),
            contentDescription = null,
            modifier = Modifier.size(if (short) 28.dp else 40.dp)
        )
    }
}

@Composable
fun ThemePicker(
    onDeleteCustomTheme: (String) -> Unit,
    onCustomTheme: () -> Unit,
    /** True when this is the keyboard's own panel rather than the settings screen. */
    inKeyboard: Boolean = false
) {
    val context = LocalContext.current

    val currentTheme = useDataStore(THEME_KEY.key, "").value.trimEnd('_')

    val isInspecting = LocalInspectionMode.current
    val availableThemeOptions = remember {
        ThemeOptionKeys.mapNotNull { key ->
            getThemeOption(context, key)?.let { Pair(key, it) }
        }.filter {
            it.second.available(context)
        }.filter {
            when (isInspecting) {
                true -> !it.second.dynamic
                else -> true
            }
        }
    }

    val originalDirection = LocalLayoutDirection.current

    val customThemes = remember(ZipThemes.updateCount.intValue) {
        ZipThemes.listCustom(context)
    }

    val assetThemes = remember { ZipThemes.listAssets(context) }

    val lifecycle = LocalLifecycleOwner.current
    Column {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val yourThemes: LazyGridScope.() -> Unit = {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    ScreenTitle(stringResource(R.string.theme_settings_custom_themes))
                }

                items(customThemes.size) {
                    val name = customThemes[it]
                    ZipThemePreview(name, isSelected = currentTheme == name.toSetting(), modifier = Modifier, onLongClick = {
                        onDeleteCustomTheme(name.name)
                    }) {
                        lifecycle.lifecycleScope.launch {
                            context.setSetting(THEME_KEY, name.toSetting())
                        }
                    }
                }

                item {
                    AddCustomThemeButton(customThemes.isEmpty()) {
                        onCustomTheme()
                    }
                }

                item {
                    VisitThemeStoreButton(customThemes.isEmpty())
                }
            }

            val builtInThemes: LazyGridScope.() -> Unit = {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    ScreenTitle(stringResource(R.string.theme_settings_default_themes))
                }
                items(assetThemes) { name ->
                    ZipThemePreview(name, isSelected = currentTheme == name.toSetting(), modifier = Modifier, onLongClick = {}) {
                        lifecycle.lifecycleScope.launch {
                            context.setSetting(THEME_KEY, name.toSetting())
                        }
                    }
                }

                items(availableThemeOptions.size) {
                    val themeOption = availableThemeOptions[it].second

                    ThemePreview(themeOption, isSelected = themeOption.key == currentTheme) {
                        lifecycle.lifecycleScope.launch {
                            context.setSetting(THEME_KEY, themeOption.key)
                        }
                    }
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                // The keyboard-preview FAB floats over this grid -- Scaffold's inner
                // padding does not account for it -- so the last row scrolled under it
                // and the button sat on top of a theme tile. There is no Scaffold and
                // no FAB in the keyboard's own panel, where 88dp is a tenth of the
                // space the panel has.
                contentPadding = PaddingValues(bottom = if (inKeyboard) 0.dp else 88.dp),
                columns = GridCells.Adaptive(minSize = 172.dp),
                horizontalArrangement = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                    Arrangement.End
                } else {
                    Arrangement.Start
                }
            ) {
                // On the settings screen your own themes come first. In the keyboard
                // the panel is a strip above the keys, and "Your themes" plus its two
                // buttons plus a heading is taller than the whole strip -- so the
                // built-in thumbnails, which are what the panel is for, never appeared
                // at all without scrolling for them.
                if (inKeyboard) {
                    builtInThemes()
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) { }
                    yourThemes()
                } else {
                    yourThemes()
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) { }
                    builtInThemes()
                }

                item(span = { GridItemSpan(maxCurrentLineSpan) }) { }

                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    if(ZipThemes.ThemeFileName.fromSetting(currentTheme) == null) {
                        CompositionLocalProvider(LocalLayoutDirection provides originalDirection) {
                            SettingToggleDataStore(
                                title = stringResource(R.string.theme_settings_key_borders),
                                setting = KeyBordersSetting
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun ThemePickerPreview() {
    Column {
        UixThemeWrapper(VoiceInputTheme.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
        UixThemeWrapper(ClassicMaterialDark.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
        UixThemeWrapper(AMOLEDDarkPurple.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
    }
}