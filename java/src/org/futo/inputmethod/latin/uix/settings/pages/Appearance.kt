package org.futo.inputmethod.latin.uix.settings.pages

import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.KeyHintsSetting
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingSection
import org.futo.inputmethod.latin.uix.settings.userSettingToggleDataStore
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import org.futo.inputmethod.latin.uix.LocalNavController
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.theme.ThemeOptions
import org.futo.inputmethod.latin.uix.settings.NavigationItem
import org.futo.inputmethod.latin.uix.settings.UserSetting
import org.futo.inputmethod.latin.uix.settings.useDataStoreValue

/**
 * How the keyboard looks.
 *
 * There was no home for this. Theme was a top-level row of its own, and the two settings
 * that change the keyboard's appearance were filed under behaviour: the suggestion-bar
 * switch sat on Keyboard among the layout settings, and the key-hint switch was the
 * first row of Long-Press Keys, where it has nothing to do with long-press duration or
 * with how the letters in a popup are ordered.
 *
 * Inline autofill joins the suggestion-bar switch because it governs what appears in
 * that same bar. It already sat directly beneath that switch; what changed is that both
 * moved off a screen about layout and onto one about appearance.
 */
val AppearanceMenu = UserSettingsMenu(
    title = R.string.appearance_settings_title,
    navPath = "appearance", registerNavPath = true,
    showPreview = true,
    settings = listOf(
        // The screen said nothing about which theme was active, which is the one thing
        // a reader wants from this row.
        UserSetting(
            name = R.string.theme_settings_title
        ) {
            val navController = LocalNavController.current
            val key = useDataStoreValue(THEME_KEY)
            val option = remember(key) { ThemeOptions[key] }
            NavigationItem(
                title = stringResource(R.string.theme_settings_title),
                subtitle = option?.name?.let { stringResource(it) },
                style = NavigationItemStyle.Misc,
                navigate = { navController!!.navigate("themes") }
            )
        },
        userSettingSection(R.string.appearance_settings_suggestion_bar_section),
        userSettingToggleDataStore(
            title = R.string.keyboard_settings_show_suggestion_row,
            subtitle = R.string.keyboard_settings_show_suggestion_row_subtitle,
            setting = ActionBarDisplayedSetting
        ),
        userSettingToggleDataStore(
            title = R.string.keyboard_settings_inline_autofill,
            subtitle = R.string.keyboard_settings_inline_autofill_subtitle,
            setting = InlineAutofillSetting
        ),
        userSettingSection(R.string.appearance_settings_keys_section),
        userSettingToggleDataStore(
            title = R.string.morekey_settings_show_hints,
            subtitle = R.string.morekey_settings_show_hints_subtitle,
            setting = KeyHintsSetting
        ).copy(searchTags = R.string.morekey_settings_show_hints_tags)
    )
)
