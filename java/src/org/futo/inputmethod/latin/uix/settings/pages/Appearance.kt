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
 * that same bar. It used to sit two rows from the switch that shows the bar at all,
 * which is close enough to look deliberate and far enough to be missed.
 */
val AppearanceMenu = UserSettingsMenu(
    title = R.string.appearance_settings_title,
    navPath = "appearance", registerNavPath = true,
    showPreview = true,
    settings = listOf(
        userSettingNavigationItem(
            title = R.string.theme_settings_title,
            style = NavigationItemStyle.Misc,
            navigateTo = "themes",
            icon = R.drawable.eye
        ),
        userSettingSection(R.string.appearance_settings_suggestion_bar_section),
        userSettingToggleDataStore(
            title = R.string.keyboard_settings_show_suggestion_row,
            subtitle = R.string.keyboard_settings_show_suggestion_row_subtitle,
            setting = ActionBarDisplayedSetting,
            icon = {
                Icon(painterResource(id = R.drawable.more_horizontal), contentDescription = null)
            }
        ),
        userSettingToggleDataStore(
            title = R.string.keyboard_settings_inline_autofill,
            subtitle = R.string.keyboard_settings_inline_autofill_subtitle,
            setting = InlineAutofillSetting,
            icon = {
                Icon(painterResource(id = R.drawable.edit_text), contentDescription = null)
            }
        ),
        userSettingSection(R.string.appearance_settings_keys_section),
        userSettingToggleDataStore(
            title = R.string.morekey_settings_show_hints,
            subtitle = R.string.morekey_settings_show_hints_subtitle,
            setting = KeyHintsSetting,
            icon = {
                Icon(painterResource(id = R.drawable.type), contentDescription = null)
            }
        ).copy(searchTags = R.string.morekey_settings_show_hints_tags),
    )
)
