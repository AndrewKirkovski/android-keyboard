package org.futo.inputmethod.latin.uix.actions

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.LocalKeyboardScheme
import org.futo.inputmethod.latin.uix.LocalManager
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.settings.SettingSectionHeader
import org.futo.inputmethod.latin.uix.settings.SettingsEmptyState
import org.futo.inputmethod.latin.uix.settings.WarningTip
import org.futo.inputmethod.latin.uix.settings.useDataStoreValue
import org.futo.inputmethod.latin.uix.theme.Typography
import org.futo.inputmethod.latin.uix.theme.app.Spacing
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState


@Composable
fun ActionItem(action: Action, modifier: Modifier = Modifier, dragIcon: Boolean = false, dragIconModifier: Modifier = Modifier) {
    Surface(color = LocalKeyboardScheme.current.keyboardContainer,
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides LocalKeyboardScheme.current.onKeyboardContainer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Tight, because the label needs every dp of the tile: at four
                    // columns the longest names wrap, and each dp of inset costs a
                    // character on the line.
                    .padding(4.dp)
            ) {

                if (dragIcon) {
                    Icon(
                        painterResource(id = R.drawable.move),
                        contentDescription = null,
                        modifier = dragIconModifier
                            .size(16.dp)
                            .align(Alignment.TopEnd),
                        tint = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Center)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1.0f))
                    Icon(
                        painterResource(id = action.icon),
                        contentDescription = null,
                        modifier = Modifier.align(
                            CenterHorizontally
                        )
                    )

                    Spacer(modifier = Modifier.weight(1.0f))

                    Text(
                        stringResource(id = action.name),
                        // Laid out at the tile's width and centred by textAlign,
                        // rather than shrink-wrapped by align(CenterHorizontally).
                        // Wrapped inside a column narrower than the tile, a label
                        // broke inside a word that would have fitted the tile.
                        modifier = Modifier.fillMaxWidth(),
                        // 12sp on 14sp, which sets two lines close enough to
                        // touch -- and stays, because the tile cannot afford the
                        // alternative. The slot left by a 24dp icon in an 84dp
                        // tile is 52dp; three lines at the 1.25 the kit asks for
                        // need 52.5, so raising the leading clips a three-line
                        // label at every font scale, and vertical overflow cuts
                        // rather than ellipsising. It needs shorter labels or a
                        // taller tile first.
                        style = Typography.Small.copy(lineHeight = 12.sp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun MoreActionsView() {
    val manager = if(LocalInspectionMode.current) { null } else { LocalManager.current }
    val context = LocalContext.current

    val actionList = if(LocalInspectionMode.current) {
        ActionsSettings.default
    } else {
        useDataStoreValue(ActionsSettings)
    }

    val map = remember(actionList) {
        actionList.toActionEditorItems().ensureWellFormed().toActionMap()
    }

    val actions = remember(actionList) {
        (map[ActionCategory.Favorites] ?: listOf()) +
                (map[ActionCategory.ActionKey] ?: listOf()) +
                (map[ActionCategory.PinnedKey] ?: listOf()) +
                (map[ActionCategory.More]      ?: listOf())
    }

    if(actions.isEmpty()) {
        // Not ScreenTitle: with no back arrow that is the accent section header,
        // so a grid with nothing in it was announced by what looks like the label
        // of a group that follows. This is what a list with nothing in it looks
        // like everywhere else in the app -- and centred in the panel, which is
        // where the clipboard and the emoji page put theirs, because a panel that
        // ends where the keyboard begins leaves a visibly blank half under a lone
        // element pinned to its top.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SettingsEmptyState(stringResource(R.string.action_editor_warning_no_actions))
        }
        return
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            // 8dp all round, equal to the gutter. The kit asks for 16 at the
            // sides so the margin beats the gap between cards, and 16 costs each
            // tile 4dp of width: at that width "Paste from clipboard" and "Voice
            // input" take a third line in one-handed mode, which the tile is not
            // tall enough to show.
            .padding(8.dp),
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(actions, key = { it.name }) {
            ActionItem(it, Modifier.clickable {
                manager!!.activateAction(it)
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionsEditor(header: @Composable () -> Unit) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val view = LocalView.current

    val initialList: List<ActionEditorItem> = if(!LocalInspectionMode.current) {
        remember {
            context.getSettingBlocking(ActionsSettings).toActionEditorItems().ensureWellFormed().filter {
                it !is ActionEditorItem.Item || it.action.shownInEditor
            }
        }
    } else {
        DefaultActionSettings.flattenToActionEditorItems()
    }

    val list = remember { initialList.toMutableStateList() }
    val lazyListState = rememberLazyGridState()
    val reorderableLazyListState = rememberReorderableLazyGridState(lazyListState) { from, to ->
        val itemToAdd = list.removeAt(from.index)
        list.add(to.index, itemToAdd)

        view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
    }

    if(!LocalInspectionMode.current) {
        DisposableEffect(Unit) {
            onDispose {
                val map = list.toActionMap()
                context.updateSettingsWithNewActions(map)
            }
        }
    }

    val actionMap = list.toActionMap()

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 0.dp),
        state = lazyListState,
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(list, key = { it.toKey() }, span = {
            when(it) {
                is ActionEditorItem.Item -> GridItemSpan(1)
                is ActionEditorItem.Separator -> GridItemSpan(maxLineSpan)
            }
        }) {
            when(it) {
                is ActionEditorItem.Item -> {
                    ReorderableItem(reorderableLazyListState, key = it.toKey()) { isDragging ->
                        ActionItem(it.action, Modifier.longPressDraggableHandle(
                            onDragStarted = {
                                view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                            },
                            onDragStopped = {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                            },
                        ), dragIcon = true, dragIconModifier = Modifier.draggableHandle())
                    }
                }
                is ActionEditorItem.Separator -> {
                    ReorderableItem(reorderableLazyListState, modifier = Modifier.fillMaxWidth(), key = it.toKey(), enabled = it.category != ActionCategory.entries[0]) { _ ->
                        Column {
                            if (it.category == ActionCategory.entries[0]) {
                                header()
                                
                                if (actionMap[ActionCategory.ActionKey]?.let { it.size > 1 } == true) {
                                    WarningTip(stringResource(R.string.action_editor_error_more_than_one_action_key))
                                } else if (actionMap[ActionCategory.PinnedKey]?.let { it.size >= 3 } == true) {
                                    WarningTip(stringResource(R.string.action_editor_warning_too_many_pinned))
                                }
                            }
                            // The app's section header, not a heading of this
                            // screen's own. It was the last place still labelling a
                            // group with a large dimmed heading -- which reads as a
                            // title for the screen rather than for the group under
                            // it, and is a third treatment beside the one every
                            // settings page and the theme panel use.
                            SettingSectionHeader(it.category.name(resources))

                            if(actionMap[it.category]?.isEmpty() == true && it.category != ActionCategory.entries.last()) {
                                TextButton(onClick = {
                                    val selfIdx = list.indexOf(it)
                                    val itemToMove = list.subList(
                                        selfIdx,
                                        list.size
                                    ).firstOrNull { v -> v is ActionEditorItem.Item }

                                    if (itemToMove != null) {
                                        val idx = list.indexOf(itemToMove)

                                        list.add(selfIdx + 1, list.removeAt(idx))
                                    }
                                }) {
                                    Text(stringResource(R.string.action_editor_add_next_action))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionEditor() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f),
        color = LocalKeyboardScheme.current.keyboardSurface,
        contentColor = LocalKeyboardScheme.current.onSurface,
        shape = RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp)
    ) {
        ActionsEditor {
            // Not ScreenTitle: with no back arrow that resolves to the section
            // header, which is what the groups under it now use, so the screen
            // and its first group read as the same thing. This is the treatment
            // every other panel's title has, from ActionWindowBar.
            Text(
                stringResource(R.string.action_editor_edit_actions),
                style = Typography.Body.MediumMl,
                modifier = Modifier.padding(
                    start = Spacing.xl,
                    end = Spacing.rowInset,
                    top = Spacing.xl,
                    bottom = Spacing.s
                )
            )
        }
    }
}


val MoreActionsAction = Action(
    icon = R.drawable.more_horizontal,
    name = R.string.settings_action_all_actions,
    simplePressImpl = null,
    shownInEditor = false,
    windowImpl = { manager, _ ->
        object : ActionWindow() {
            @Composable
            override fun windowName(): String = stringResource(id = R.string.settings_action_all_actions)

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                MoreActionsView()
            }

            @Composable
            override fun WindowTitleBar(rowScope: RowScope) {
                super.WindowTitleBar(rowScope)

                TextButton(onClick = { manager.showActionEditor() }, modifier = Modifier.padding(8.dp, 0.dp)) {
                    Text(stringResource(R.string.action_editor_edit_actions), color = LocalKeyboardScheme.current.onSurface)
                }
            }
        }
    },
)
