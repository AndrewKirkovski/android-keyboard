package org.futo.inputmethod.latin.uix.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.LocalKeyboardScheme
import org.futo.inputmethod.latin.uix.LocalNavController
import org.futo.inputmethod.latin.uix.SettingsKey
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.theme.app.Spacing
import kotlin.math.pow

/**
 * What a [ScrollableList] knows about the screen title scrolling inside it, so it can
 * put a compact bar in its place once it leaves. Both positions are in root
 * coordinates, which -- unlike bounds -- are not clipped to the visible region, so the
 * comparison stays meaningful after the title has scrolled fully out.
 */
internal class ScreenTitleAnchor {
    var title by mutableStateOf<String?>(null)
    var onBack by mutableStateOf<(() -> Unit)?>(null)
    var viewportTop by mutableFloatStateOf(0f)
    var titleBottom by mutableFloatStateOf(Float.MAX_VALUE)

    val scrolledAway: Boolean
        get() = !title.isNullOrEmpty() && titleBottom <= viewportTop
}

internal val LocalScreenTitleAnchor = compositionLocalOf<ScreenTitleAnchor?> { null }

/**
 * The label above a group of rows.
 *
 * Accent-coloured and sentence case. It used to be uppercase behind a 3dp accent rail,
 * which is two devices doing one job, and caps are worse than useless in the many
 * locales among the app's 91 whose scripts have no case at all -- there the rail was
 * carrying the whole burden and the tracking just spaced the glyphs out.
 *
 * The header sits *outside* the card its rows live in, which is what makes it read as a
 * heading rather than as the first row.
 */
@Composable
fun SettingSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Spacing.xl,
                end = Spacing.rowInset,
                top = Spacing.xl,
                bottom = Spacing.s
            )
    )
}

/**
 * The hairline between two rows inside a card.
 *
 * The design has one between every pair and none at the card's edges, which is what
 * makes a card of six rows read as six things rather than one block of text. Full
 * width, not inset: almost no row here carries a leading icon -- the language IME rows
 * and a developer screen are the exceptions -- so insetting every divider to clear a
 * gutter that is usually empty would let the few dictate the many.
 */
@Composable
fun SettingsRowDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/**
 * One row's slice of a card, for a list that must stay lazy.
 *
 * [SettingsCard] composes every row it is given, which is right for a menu of eight and
 * wrong for the language picker's hundred-odd. This gives a lazy item the same fill,
 * inset and divider, rounding only the first and last so the run still reads as one
 * card.
 */
@Composable
fun SettingsCardItem(
    isFirst: Boolean,
    isLast: Boolean,
    content: @Composable () -> Unit
) {
    val r = 22.dp
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.cardInset),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = if (isFirst) r else 0.dp,
            topEnd = if (isFirst) r else 0.dp,
            bottomStart = if (isLast) r else 0.dp,
            bottomEnd = if (isLast) r else 0.dp
        )
    ) {
        Column {
            if (isFirst) Spacer(Modifier.height(Spacing.s))
            if (!isFirst) SettingsRowDivider()
            content()
            if (isLast) Spacer(Modifier.height(Spacing.s))
        }
    }
}

/**
 * A card built from a known list of rows, with a divider between each pair.
 *
 * The lambda overload cannot do this -- a `ColumnScope.() -> Unit` gives no way to
 * count or interleave its children -- so a caller that has its rows as a list uses
 * this one, and bespoke content uses the lambda and places its own dividers.
 */
@Composable
fun SettingsCard(rows: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    SettingsCard(modifier) {
        rows.forEachIndexed { i, row ->
            if (i > 0) SettingsRowDivider()
            row()
        }
    }
}

/**
 * The grouped card a run of rows sits in, on the tinted ground the screen paints.
 *
 * One UI's structure: related settings share a rounded container, and the gap between
 * two containers is what says "different subject". Before this the app drew rows
 * directly on the background with nothing between groups but a header, so a screen was
 * an undifferentiated column and grouping was invisible.
 */
@Composable
fun SettingsCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.cardInset, vertical = Spacing.xs),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {
        // The card breathes at its ends. A row centres itself in 56dp so a single line
        // already sits clear of the edge, but a two-line row or one carrying a control
        // fills its height, and without this the first and last of those touch the
        // rounded corner they sit against.
        Column(Modifier.padding(vertical = Spacing.s), content = content)
    }
}

/**
 * @param actionLabel a single action for this screen, shown at the end of the app bar.
 *   "Add", "Import", "Done" -- the one thing a screen is for, where the alternative is a
 *   full-width row that costs 56dp and reads as another setting. The bar had no slot for
 *   one, which is why Languages opened with an "Add language" row and Transformer models
 *   ended with an "Import from file" row.
 */
@Composable
fun ScreenTitle(
    title: String,
    showBack: Boolean = false,
    navController: NavHostController? = LocalNavController.current ?: rememberNavController(),
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    // Only the arrow and the title navigate back. Making the whole row clickable would
    // put the action inside the back target.
    val backModifier = if(showBack) {
        Modifier.clickable(onClickLabel = "Navigate back") {
            navController!!.navigateUp()
        }
    } else {
        Modifier
    }
    // showBack splits this in two: with a back arrow it is the screen's title, and
    // without one it is an in-page section header, which is now its own composable.
    if (!showBack) {
        SettingSectionHeader(title)
        return
    }

    // Hand the enclosing ScrollableList this title and where its underside currently is,
    // so that once it scrolls off the top the screen still says what screen it is.
    val anchor = LocalScreenTitleAnchor.current
    val reportModifier = if (anchor == null) Modifier else Modifier.onGloballyPositioned {
        anchor.titleBottom = it.positionInRoot().y + it.size.height
    }
    if (anchor != null) {
        val nav = navController
        LaunchedEffect(title, nav) {
            anchor.title = title
            anchor.onBack = nav?.let { { it.navigateUp(); Unit } }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().then(reportModifier),
        verticalAlignment = CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.0f).then(backModifier),
            verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(0.dp, 16.dp)
            )
        }

        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, modifier = Modifier.padding(end = Spacing.s)) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * The screen title, compacted to a bar, shown only once the real one has scrolled away.
 *
 * The 28sp title is worth its space at the top of a screen and not worth it for the rest
 * of the scroll, which is why it is not simply pinned: it hands over to this at the point
 * where the alternative is no context at all.
 */
@Composable
private fun CollapsedScreenTitle(title: String, onBack: (() -> Unit)?) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (onBack != null) it.clickable(onClickLabel = "Navigate back", onClick = onBack) else it }
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(18.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        // Content passes underneath, so the bar needs an edge or the line half-hidden
        // behind it reads as a clipping bug rather than as scrolled-away content.
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
fun ScreenTitleWithIcon(title: String, painter: Painter) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))

        Icon(painter, contentDescription = null, modifier = Modifier.align(CenterVertically))
        Spacer(modifier = Modifier.width(18.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier
            .align(CenterVertically)
            .padding(0.dp, 16.dp))
    }
}

/**
 * A note on the ground between cards.
 *
 * The slot overload exists so a tip carrying an inline icon or several lines still
 * looks like a tip. Resize used to build its own out of PaymentSurface -- the payment
 * screen's promotional panel -- so it alone had a heading, a border and a grey fill
 * where every other tip is a plain primaryContainer note.
 */
@Composable
fun Tip(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.cardInset, vertical = Spacing.xs),
        shape = MaterialTheme.shapes.medium
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(Modifier.padding(horizontal = Spacing.l, vertical = Spacing.m)) {
                content()
            }
        }
    }
}

@Composable
@Preview
fun Tip(text: String = "This is an example tip") {
    Tip {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * What a list shows when it has nothing in it yet.
 *
 * Not a [Tip]. A tip is accent-filled, which is the app's way of saying "read this";
 * an empty list is the normal state of a list nobody has added to, and colouring it
 * like a notice gives a blank screen the loudest element on it.
 */
@Composable
@Preview
fun SettingsEmptyState(text: String = "Nothing here yet") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun WarningTip(text: String = "This is an example tip") {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.cardInset, vertical = Spacing.xs),
        shape = MaterialTheme.shapes.medium
    ) {

        Text(
            buildAnnotatedString {
                appendInlineContent("icon")
                append(' ')
                append(text)
            },
            modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.m),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            inlineContent = mapOf(
                "icon" to InlineTextContent(
                    Placeholder(
                        width = with(LocalDensity.current) { 24.dp.toPx().toSp() },
                        height = with(LocalDensity.current) { 24.dp.toPx().toSp() },
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ){
                    Icon(Icons.Default.Warning, contentDescription = null)
                }
            ))
    }
}

@Composable
fun SpacedColumn(gap: Dp, modifier: Modifier = Modifier, horizontalAlignment: Alignment.Horizontal = Alignment.Start, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = horizontalAlignment) {
        content()
    }
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    disabled: Boolean = false,
    subcontent: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
    /** Accent, for a row that adds or removes rather than navigates. */
    accentTitle: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    // Two heights, and only two. This was 68dp, or 48dp when compact, against
    // LanguageConfigurable's 50dp and LayoutConfigurable's 44dp -- four heights for
    // one idea. A row is 56dp, or 72dp when it carries a second line.
    val minHeight = if (subtitle != null || subcontent != null) {
        Spacing.rowHeightTwoLine
    } else {
        Spacing.rowHeight
    }
    val textColor = when(LocalContentColor.current) {
        MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.onTertiary -> LocalContentColor.current

        else -> MaterialTheme.colorScheme.onSurface
    }

    val subTextColor = when(textColor) {
        MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.onTertiary -> textColor.copy(alpha = 0.6f)

        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(0.dp, minHeight)
            .let {
                if(onClick != null && onSubmenuNavigate == null) {
                    it.clickable(enabled = !disabled, onClick = {
                        if (!disabled) {
                            onClick()
                        }
                    })
                } else if(onSubmenuNavigate != null) {
                    it.clickable(enabled = !disabled, onClick = {
                        if (!disabled) {
                            onSubmenuNavigate()
                        }
                    })
                } else {
                    it
                }
            }
            .height(intrinsicSize = IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1.0f).fillMaxHeight().padding(0.dp, Spacing.xs)) {
            Spacer(Modifier.width(Spacing.rowInset))
            if (icon != null) {
                // The slot is the icon, 24dp, not a 48dp column holding a 40dp circle
                // holding the icon. The column reserved its width whether or not a row
                // had an icon, so titles started at 20dp on some rows and 80dp on
                // others -- most of the 14 different left edges came from here.
                //
                // On a row with a subtitle the slot pins to the top, beside the title
                // it labels, rather than floating against the middle of the text block.
                Box(
                    modifier = Modifier
                        .size(Spacing.iconSlot)
                        .then(
                            if (subtitle != null || subcontent != null) Modifier
                            else Modifier.align(Alignment.CenterVertically)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(Modifier.width(Spacing.iconGap))
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .alpha(
                        if (disabled) {
                            0.5f
                        } else {
                            1.0f
                        }
                    )
            ) {
                SpacedColumn(4.dp) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (accentTitle) MaterialTheme.colorScheme.primary else textColor,
                        modifier = Modifier.heightIn(min = 24.dp)
                    )

                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor,
                            // F2: an unbounded subtitle made row height vary about
                            // threefold down a list, which destroys the vertical rhythm.
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else if (subcontent != null) {
                        subcontent()
                    }
                }
            }
            if(onSubmenuNavigate != null) { Spacer(Modifier.width(Spacing.s)) }
        }

        Spacer(Modifier.width(Spacing.l))

        Row(Modifier.let {
            if(onSubmenuNavigate != null && onClick != null) {
                it.clickable(enabled = !disabled, onClick = {
                    if(!disabled) {
                        onClick()
                    }
                })
            } else {
                it
            }
        }.fillMaxHeight()) {
            if(onSubmenuNavigate != null) { Spacer(Modifier.width(Spacing.s)) }
            Box(modifier = Modifier.align(Alignment.CenterVertically), contentAlignment = Alignment.Center) {
                content()
            }

            // Matches the leading inset. It was 16dp against a 20dp left edge, so every
            // switch sat 4dp closer to the screen edge than the title it belonged to.
            Spacer(Modifier.width(Spacing.rowInset))
        }
    }
}

@Composable
fun SettingToggleRaw(
    title: String,
    enabled: Boolean,
    setValue: (Boolean) -> Unit,
    subtitle: String? = null,
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
) {
    SettingItem(
        title = title,
        subtitle = subtitle,
        onClick = {
            if (!disabled) {
                setValue(!enabled)
            }
        },
        icon = icon,
        modifier = Modifier.let {
            if(onSubmenuNavigate == null) {
                it.clearAndSetSemantics {
                    this.text = AnnotatedString("$title. ${subtitle ?: ""}")
                    this.role = Role.Switch
                    this.toggleableState = ToggleableState(enabled)
                }
            } else {
                it
            }
        },
        onSubmenuNavigate = onSubmenuNavigate
    ) {
        Switch(checked = enabled, onCheckedChange = {
            if (!disabled) {
                setValue(!enabled)
            }
        }, enabled = !disabled)
    }
}

@Composable
fun SettingToggleDataStoreItem(
    title: String,
    dataStoreItem: DataStoreItem<Boolean>,
    subtitle: String? = null,
    disabledSubtitle: String? = null,
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
) {
    val (enabled, setValue) = dataStoreItem

    val subtitleValue = if (!enabled && disabledSubtitle != null) {
        disabledSubtitle
    } else {
        subtitle
    }

    SettingToggleRaw(title, enabled, { setValue(it) }, subtitleValue, disabled, icon, onSubmenuNavigate)
}

@Composable
fun SettingToggleDataStore(
    title: String,
    setting: SettingsKey<Boolean>,
    subtitle: String? = null,
    disabledSubtitle: String? = null,
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
) {
    key(setting) {
        SettingToggleDataStoreItem(
            title,
            useDataStore(setting.key, setting.default),
            subtitle,
            disabledSubtitle,
            disabled,
            icon,
            onSubmenuNavigate
        )
    }
}

@Composable
fun SettingToggleSharedPrefs(
    title: String,
    key: String,
    default: Boolean,
    subtitle: String? = null,
    disabledSubtitle: String? = null,
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
) {
    key(key) {
        SettingToggleDataStoreItem(
            title, useSharedPrefsBool(key, default), subtitle, disabledSubtitle, disabled, icon, onSubmenuNavigate
        )
    }
}

@Composable
fun<T> SettingRadio(
    title: String,
    options: List<T>,
    optionNames: List<String>,
    setting: DataStoreItem<T>,
    modifier: Modifier = Modifier,
    hints: List<@Composable () -> Unit>? = null,
    subcontent: List<@Composable () -> Unit>? = null,
) {
    // The group's label is a label, not a section header. It used to emit
    // ScreenTitle(showBack = false), so a radio group inside a card announced itself as
    // a new section of the screen -- the same mistake the slider made.
    Column {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.rowInset, end = Spacing.rowInset, top = Spacing.m)
        )
        options.zip(optionNames).forEachIndexed { i, it ->
            SettingItem(title = it.second, onClick = { setting.setValue(it.first) }, icon = {
                RadioButton(selected = setting.value == it.first, onClick = null)
            }, modifier = modifier.clearAndSetSemantics {
                this.text = AnnotatedString(it.second)
                this.role = Role.RadioButton
                this.selected = setting.value == it.first
            }, subcontent = subcontent?.getOrNull(i)) {
                hints?.getOrNull(i)?.invoke()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T: Number> SettingSliderForDataStoreItem(
    title: String,
    item: DataStoreItem<T>,
    default: T,
    range: ClosedFloatingPointRange<Float>,
    transform: (Float) -> T,
    indicator: (T) -> String = { it.toString() },
    hardRange: ClosedFloatingPointRange<Float> = range,
    power: Float = 1.0f,
    subtitle: String? = null,
    steps: Int = 0,
) {
    val context = LocalContext.current

    val (value, setValue) = item
    var virtualValue by remember { mutableFloatStateOf(value.toFloat().let {
        if(it == Float.POSITIVE_INFINITY || it == Float.NEGATIVE_INFINITY) {
            it
        } else {
            it.pow(1.0f / power)
        }
    }) }
    var isTextFieldVisible by remember { mutableStateOf(false) }
    var hasTextFieldFocusedYet by remember { mutableStateOf(false) }
    var textFieldValue by remember(value) {
        val s = value.toString()
        mutableStateOf(TextFieldValue(
            s,
            selection = TextRange(0, s.length)
        ))
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isTextFieldVisible) {
        if(isTextFieldVisible) focusRequester.requestFocus()
    }

    val apply = {
        if(isTextFieldVisible) {
            val number = textFieldValue.text.trim().toFloatOrNull()
            val newValue = if (number != null) {
                transform(number.coerceIn(hardRange))
            } else {
                default
            }

            setValue(newValue)
            virtualValue = newValue.toFloat().pow(1.0f / power)

            isTextFieldVisible = false
            textFieldValue = TextFieldValue()
        }
    }

    // Title left, reading right, track beneath at full width -- the same shape as a
    // row, so a slider sits in a list without breaking its rhythm.
    //
    // It used to emit its own title through ScreenTitle(showBack = false), which is the
    // in-page *section header*: every slider announced itself as a new section of the
    // screen, in caps, with an accent rail. The reading was then pinned to weight(0.33f)
    // of the row whatever it said, which both starved "Default" and left two thirds of
    // the row empty for "18"; AdvancedParameters feeds whole sentences into it. And the
    // whole control had no vertical padding at all, so it collided with the rows above
    // and below it.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.rowInset, vertical = Spacing.m)
    ) {
        Row(verticalAlignment = CenterVertically) {
            Column(Modifier.weight(1.0f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(Spacing.l))

            // Tabular figures: the reading changes continuously while a thumb is on the
            // track, and proportional digits make it jitter sideways as it counts.
            val readingStyle = MaterialTheme.typography.labelLarge.copy(
                fontFeatureSettings = "tnum"
            )

            if (isTextFieldVisible) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .widthIn(min = 56.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) hasTextFieldFocusedYet = true
                            else if (!it.isFocused && hasTextFieldFocusedYet) apply()
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { apply() }),
                    singleLine = true,
                    textStyle = readingStyle.copy(color = MaterialTheme.colorScheme.primary),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            } else {
                Text(
                    text = indicator(value),
                    modifier = Modifier.clickable {
                        hasTextFieldFocusedYet = false
                        isTextFieldVisible = true
                    },
                    style = readingStyle,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.height(Spacing.s))

        // Material 3.1.3's default slider draws a bar-shaped thumb, a gap on each side
        // of it, and a stop dot at the far end. Three marks where the control has one
        // thing to say, and at settings density the gap reads as the track being broken
        // rather than as a thumb. A round thumb on a continuous track, as designed.
        val sliderInteraction = remember { MutableInteractionSource() }
        Slider(
            value = virtualValue,
            onValueChange = {
                virtualValue = it
                setValue(transform(it.pow(power))) },
            valueRange = range.start.pow(1.0f / power) .. range.endInclusive.pow(1.0f / power),
            enabled = !isTextFieldVisible,
            modifier = Modifier.fillMaxWidth(),
            steps = steps,
            interactionSource = sliderInteraction,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = sliderInteraction,
                    enabled = !isTextFieldVisible,
                    thumbSize = DpSize(20.dp, 20.dp)
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    enabled = !isTextFieldVisible,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    modifier = Modifier.height(4.dp)
                )
            }
        )
    }
}



@Composable
fun<T: Number> SettingSlider(
    title: String,
    setting: SettingsKey<T>,
    range: ClosedFloatingPointRange<Float>,
    transform: (Float) -> T,
    indicator: (T) -> String = { it.toString() },
    hardRange: ClosedFloatingPointRange<Float> = range,
    power: Float = 1.0f,
    subtitle: String? = null,
    steps: Int = 0
) {
    SettingSliderForDataStoreItem(
        title = title,
        item = useDataStore(setting, blocking = true),
        default = setting.default,
        range = range,
        transform = transform,
        indicator = indicator,
        hardRange = hardRange,
        power = power,
        subtitle = subtitle,
        steps = steps
    )
}

@Composable
fun SettingSliderSharedPrefsInt(
    title: String,
    key: String,
    default: Int,
    range: ClosedFloatingPointRange<Float>,
    transform: (Float) -> Int,
    indicator: (Int) -> String = { it.toString() },
    hardRange: ClosedFloatingPointRange<Float> = range,
    power: Float = 1.0f,
    subtitle: String? = null,
    steps: Int = 0
) {
    SettingSliderForDataStoreItem(
        title = title,
        item = useSharedPrefsInt(key, default),
        default = default,
        range = range,
        transform = transform,
        indicator = indicator,
        hardRange = hardRange,
        power = power,
        subtitle = subtitle,
        steps = steps
    )
}

@Composable
fun ScrollableList(modifier: Modifier = Modifier, spacing: Dp = 0.dp, horizontalAlignment: Alignment.Horizontal = Alignment.Start, content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()
    val anchor = remember { ScreenTitleAnchor() }

    // The viewport is measured on the Box, not on the scrolling Column. A modifier after
    // verticalScroll observes the *content* node, which moves as you scroll, so measuring
    // there compares two positions that travel together and the bar never appears.
    Box(
        modifier
            .fillMaxSize()
            .onGloballyPositioned { anchor.viewportTop = it.positionInRoot().y }
    ) {
        CompositionLocalProvider(LocalScreenTitleAnchor provides anchor) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = horizontalAlignment
            ) {
                content()
            }
        }

        // Only a screen title registers itself, and only screens have one -- the panels
        // that render inside the keyboard call SettingSectionHeader directly, which does
        // not. So this appears on settings screens and nowhere else.
        AnimatedVisibility(
            visible = anchor.scrolledAway,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CollapsedScreenTitle(anchor.title ?: "", anchor.onBack)
        }
    }
}

@Composable
fun SettingListLazy(content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        content()
    }
}


enum class NavigationItemStyle {
    HomePrimary,
    HomeSecondary,
    HomeTertiary,
    MiscNoArrow,
    Misc,
    ExternalLink,
    Mail
}

@Composable
fun NavigationItem(title: String, style: NavigationItemStyle, navigate: () -> Unit, icon: Painter? = null, subtitle: String? = null) {
    SettingItem(
        title = title,
        subtitle = subtitle,
        onClick = navigate,
        // Null when there is no painter, rather than a lambda that draws nothing.
        // SettingItem reserves the gutter on `icon != null`, and a lambda is not
        // null, so a navigation row without a painter indented its title beside an
        // empty slot -- which is the hole that rule exists to close.
        icon = icon?.let { painter ->
            {
                // A plain icon, at the same 24dp as every other row's. It used to be a
                // 48dp Canvas drawing a 40dp filled circle behind the glyph, which made
                // a navigation row a different shape from a toggle row on the same
                // screen and pushed its title 80dp from the edge.
                //
                // The circle also carried no information. The three Home* styles
                // rotated through primary, secondary and tertiary containers in the
                // order the rows happened to be written -- Languages primary, Keyboard
                // secondary, Swipe primary again -- and whichever role was the palette's
                // neutral read as *disabled* beside the two that were hues. The enum
                // still separates a home destination from the quiet footer entries,
                // which is what it is for.
                val iconColor = when(style) {
                    NavigationItemStyle.HomePrimary,
                    NavigationItemStyle.HomeSecondary,
                    NavigationItemStyle.HomeTertiary -> MaterialTheme.colorScheme.primary

                    NavigationItemStyle.MiscNoArrow,
                    NavigationItemStyle.Mail,
                    NavigationItemStyle.ExternalLink,
                    NavigationItemStyle.Misc -> LocalContentColor.current.copy(alpha = 0.75f)
                }

                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(Spacing.iconSlot),
                    tint = iconColor
                )
            }
        }
    ) {
        when(style) {
            // A chevron, not an arrow. An arrow reads as "do this", a chevron as
            // "there is more in here", which is what a navigation row means. Quiet,
            // because it is an affordance rather than content.
            NavigationItemStyle.Misc -> Icon(
                painterResource(id = R.drawable.chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // The Home* styles get the chevron too. They rendered nothing at all, so
            // on the busiest screen in the app no row said it led anywhere -- the
            // circle behind the icon had been carrying that, and the circle is gone.
            // MiscNoArrow still draws none, which is the point of it.
            NavigationItemStyle.HomePrimary,
            NavigationItemStyle.HomeSecondary,
            NavigationItemStyle.HomeTertiary -> Icon(
                painterResource(id = R.drawable.chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            NavigationItemStyle.Mail -> Icon(Icons.Default.Send, contentDescription = null)
            NavigationItemStyle.ExternalLink -> Icon(painterResource(R.drawable.external_link), contentDescription = null)
            else -> {}
        }
    }
}

@Composable
fun SettingTextField(title: String, placeholder: String, field: SettingsKey<String>) {
    val context = LocalContext.current

    val personalDict = useDataStore(field)
    val textFieldValue = remember { mutableStateOf(context.getSettingBlocking(
        field.key, field.default)) }

    LaunchedEffect(textFieldValue.value) {
        personalDict.setValue(textFieldValue.value)
    }

    ScreenTitle(title)

    TextField(
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
        },
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
    )
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T> DropDownPicker(
    label: String,
    options: List<T>,
    selection: T?,
    onSet: (T) -> Unit,
    getDisplayName: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selection?.let(getDisplayName) ?: "None",
            onValueChange = { },
            label = if (label.isNotBlank()) {
                { Text(label) }
            } else {
                null
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(getDisplayName(selectionOption))
                    },
                    onClick = {
                        onSet(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}*/

@Composable
fun CollapsibleSection(title: String, modifier: Modifier = Modifier, section: @Composable ColumnScope.() -> Unit) {
    val resources = LocalResources.current
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 44.dp).clickable {
                expanded = !expanded
            }.padding(16.dp).semantics {
                stateDescription = resources.getString(
                    if(expanded)
                        R.string.setting_section_expanded
                    else
                        R.string.setting_section_collapsed
                )
                role = Role.DropdownList
            }
        ) {
            RotatingChevronIcon(expanded, tint = LocalContentColor.current)

            Spacer(Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = LocalContentColor.current,
                modifier = Modifier.weight(1.0f)
            )
        }

        AnimatedVisibility(expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column {
                section()
            }
        }
    }
}


private val DropDownShape = RoundedCornerShape(12.dp)
@Composable
fun<T> DropDownPicker(
    options: List<T>,
    selection: T?,
    onSet: (T) -> Unit,
    getDisplayName: (T) -> String,
    scrollableOptions: Boolean = false,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    var expanded by remember { mutableStateOf(false) }

    SpacedColumn(4.dp, modifier = modifier.semantics {
        role = Role.DropdownList
    }) {
        Row(
            Modifier.fillMaxWidth().background(
                MaterialTheme.colorScheme.surfaceContainerHighest, DropDownShape
            ).border(
                if(expanded) { 2.dp } else { 1.dp },
                MaterialTheme.colorScheme.outline,
                DropDownShape
            ).heightIn(min = 44.dp).clip(DropDownShape).clickable {
                expanded = !expanded
            }.padding(16.dp).semantics {
                stateDescription = resources.getString(
                    if(expanded)
                        R.string.setting_section_expanded
                    else
                        R.string.setting_section_collapsed
                )
                role = Role.DropdownList
            }
        ) {
            if(selection != null) {
                Text(
                    text = getDisplayName(selection),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.0f)
                )
            } else {
                Spacer(Modifier.weight(1.0f))
            }

            RotatingChevronIcon(expanded, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        AnimatedVisibility(expanded, enter = fadeIn(), exit = fadeOut()) {
            val scrollState = rememberScrollState()
            Column(Modifier.let {
                if(scrollableOptions) {
                    it.verticalScroll(scrollState)
                } else {
                    it
                }
            }) {
                Spacer(Modifier.height(9.dp))
                Column(
                    Modifier.fillMaxWidth().background(
                        MaterialTheme.colorScheme.surfaceContainerHighest, DropDownShape
                    ).border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        DropDownShape
                    ).clip(DropDownShape)
                ) {
                    options.forEach {
                        Box(
                            Modifier.fillMaxWidth().heightIn(min = 44.dp).background(
                                if(selection == it) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                }
                            ).clickable {
                                onSet(it)
                                expanded = false
                            }.padding(16.dp).semantics {
                                selected = selection == it
                                role = Role.DropdownList
                            }
                        ) {
                            Text(
                                getDisplayName(it),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if(selection == it) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A choice among a fixed list: a row that opens a sheet.
 *
 * It used to render an outlined box inside the row. An outline says "input", and this is
 * not one -- next to the plain rows of a card it read as a foreign object dropped into
 * one, and it wrapped to two lines because "Immediate space after suggestions &
 * punctuation" is 47 characters and no box that width holds it. The control came out
 * near 96dp beside a 72dp row.
 *
 * The current value is the row's subtitle instead, in the accent because it is the part
 * that changes. That makes this the standard two-line row -- no new height and no new
 * left edge -- and the options move into a sheet, where a long one is read in full
 * rather than truncated to fit a control.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T> DropDownPickerSettingItem(
    label: String,
    options: List<T>,
    selection: T?,
    onSet: (T) -> Unit,
    getDisplayName: (T) -> String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    var sheetOpen by remember { mutableStateOf(false) }

    SettingItem(
        title = label,
        icon = icon,
        onClick = { sheetOpen = true },
        subcontent = {
            Text(
                selection?.let(getDisplayName) ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier
    ) {
        Icon(
            painterResource(id = R.drawable.chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (sheetOpen) {
        ModalBottomSheet(onDismissRequest = { sheetOpen = false }) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = Spacing.rowInset, end = Spacing.rowInset, bottom = Spacing.s
                )
            )
            options.forEach { option ->
                val name = getDisplayName(option)
                SettingItem(
                    title = name,
                    onClick = { onSet(option); sheetOpen = false },
                    icon = { RadioButton(selected = option == selection, onClick = null) },
                    modifier = Modifier.clearAndSetSemantics {
                        this.text = AnnotatedString(name)
                        this.role = Role.RadioButton
                        this.selected = option == selection
                    }
                ) { }
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun RotatingChevronIcon(isExpanded: Boolean, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) -180f else 0f
    )

    Icon(
        painter = painterResource(R.drawable.chevron_down),
        contentDescription = null,
        modifier = modifier.rotate(rotation),
        tint = tint
    )
}

@Composable
fun PrimarySettingToggleDataStoreItem(
    title: String,
    dataStoreItem: DataStoreItem<Boolean>,
) {
    val (enabled, setValue) = dataStoreItem

    Box(Modifier.padding(24.dp)) {
        Surface(
            shape = RoundedCornerShape(48.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.clearAndSetSemantics {
                this.text = AnnotatedString(title)
                this.role = Role.Switch
                this.toggleableState = ToggleableState(enabled)
            },
            onClick = {
                setValue(!enabled)
            }
        ) {
            Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.heightIn(min = 24.dp)
                )
                Spacer(Modifier.weight(1.0f))

                Switch(checked = enabled, onCheckedChange = null)
            }
        }
    }
}

@Preview
@Composable
fun PreviewPrimarySetting() {
    PrimarySettingToggleDataStoreItem(
        "Enable",
        dataStoreItem = DataStoreItem(false, { error("") })
    )
}