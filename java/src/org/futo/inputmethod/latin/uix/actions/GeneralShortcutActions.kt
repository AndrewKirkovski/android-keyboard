package org.futo.inputmethod.latin.uix.actions

import android.view.KeyEvent
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

val SelectAllAction = Action(
    icon = R.drawable.maximize,
    name = R.string.settings_action_select_all,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
    },
    windowImpl = null,
)

val CutAction = Action(
    icon = R.drawable.scissors,
    name = R.string.settings_action_cut,
    simplePressImpl = { manager, _ ->
        manager.copyToClipboard(cut = true)
    },
    windowImpl = null,
)

val CopyAction = Action(
    icon = R.drawable.copy,
    name = R.string.settings_action_copy,
    simplePressImpl = { manager, _ ->
        manager.copyToClipboard(cut = false)
    },
    windowImpl = null,
)

val PasteAction = Action(
    icon = R.drawable.clipboard,
    name = R.string.settings_action_paste,
    simplePressImpl = { manager, _ ->
        manager.pasteFromClipboard()
    },
    windowImpl = null,
)