package org.futo.inputmethod.latin.uix.actions

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.isDirectBootUnlocked
import org.futo.inputmethod.latin.uix.settings.SettingsActivity
import org.futo.inputmethod.latin.uix.theme.selector.ThemePicker

val ThemeAction = Action(
    icon = R.drawable.themes,
    name = R.string.action_theme_switcher_title,
    simplePressImpl = null,
    canShowKeyboard = true,
    windowImpl = { manager, _ ->
        object : ActionWindow() {
            override val onlyShowAboveKeyboard: Boolean = true

            // Keeping the keyboard up meant the shared title bar was skipped, so this
            // was one of two panels with neither its name nor a back arrow -- Debug info
            // was the other -- and all this one had was the round close button the
            // suggestion strip lends a docked window, sitting over the top-left key. The
            // bar is drawn outside the panel's content box, so the window grows upward
            // to fit it rather than taking the room the thumbnails need.
            override val showTitleBarAboveKeyboard: Boolean = true

            // The bar's back arrow is now what closes the panel, and it is where every
            // other panel puts that.
            override val showCloseButton: Boolean = false

            @Composable
            override fun windowName(): String {
                return stringResource(R.string.action_theme_switcher_title)
            }

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                val context = LocalContext.current
                val resources = LocalResources.current
                val openSettingsLambda = {
                    if(context.isDirectBootUnlocked && !manager.isDeviceLocked()) {
                        SettingsActivity.openToNavDest(context, "themes")
                    } else {
                        val toast = Toast.makeText(
                            context,
                            resources.getString(R.string.action_clipboard_manager_error_device_locked_title),
                            Toast.LENGTH_SHORT
                        )

                        toast.show()

                    }
                }

                ThemePicker({ openSettingsLambda() }, openSettingsLambda, inKeyboard = true)
            }
        }
    }
)