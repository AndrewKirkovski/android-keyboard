package org.futo.inputmethod.latin.uix.settings.pages

import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.SettingsExporter
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem

/**
 * Backing settings up and restoring them.
 *
 * The screen was called "Miscellaneous" and contained exactly one section, headed
 * "Settings Backup", holding exactly two rows. Two of those three labels said nothing:
 * the title named no subject, and the header repeated the screen it was the whole of.
 * The screen is named for what is on it and the header is gone.
 */
val MiscMenu = UserSettingsMenu(
    title = R.string.backup_settings_title,
    navPath = "misc", registerNavPath = true,
    settings = listOf(
        userSettingNavigationItem(
            title = (R.string.settings_export_configuration),
            subtitle = (R.string.settings_export_configuration_subtitle),
            style = NavigationItemStyle.Misc,
            navigateTo = "exportingcfg"
        ).copy(searchTags = R.string.settings_import_export_tags),
        userSettingNavigationItem(
            title = (R.string.settings_import_configuration),
            subtitle = (R.string.settings_import_configuration_subtitle),
            style = NavigationItemStyle.Misc,
            navigate = { nav ->
                SettingsExporter.triggerImportSettings(nav.context)
            }
        ).copy(searchTags = R.string.settings_import_export_tags),
    )
)