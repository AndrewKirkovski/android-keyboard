package org.futo.inputmethod.latin.uix.settings.pages

import android.icu.text.Transliterator
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.LocalNavController
import org.futo.inputmethod.latin.uix.SettingsTextEdit
import org.futo.inputmethod.latin.uix.settings.BottomSpacer
import org.futo.inputmethod.latin.uix.settings.NavigationItem
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.SettingSectionHeader
import org.futo.inputmethod.latin.uix.settings.SettingsCard
import org.futo.inputmethod.latin.uix.settings.SettingsEmptyState
import org.futo.inputmethod.latin.uix.settings.SettingsMenus
import org.futo.inputmethod.latin.uix.settings.UserSetting
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.userSettingDecorationOnly

private val LATIN_ASCII = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    Transliterator.getInstance("Latin-ASCII")
} else {
    null
}

private fun normalizeString(s: String): String {
    return (LATIN_ASCII?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            it.transliterate(s)
        } else {
            null
        }
    } ?: s).lowercase()
}

@Composable private fun searchResults(query: String): List<Pair<UserSettingsMenu, List<UserSetting>>> {
    val resources = LocalResources.current

    val searchTagsByMenu = remember {
        SettingsMenus
            .flatMap { it.settings }
            .filter { it.name != 0 }
            .associate {
                it to run {
                    normalizeString(resources.getString(it.name)) + "\n" +
                            (it.searchTagList?.joinToString("\n") { normalizeString(resources.getString(it)) }
                                ?: it.searchTags?.let { normalizeString(resources.getString(it)) }
                                ?: "") + "\n" +
                            (it.subtitle?.let { normalizeString(resources.getString(it)) } ?: "")
                }
            }
    }

    return remember(query) {
        SettingsMenus.map { menu ->
            menu to menu.settings
                .filter { it.name != 0 && it.appearsInSearch }
                .filter { searchTagsByMenu[it]!!.contains(query) }
        }
    }.filter {
        it.first.visibilityCheck?.invoke() != false
    }.map { v ->
        v.first to v.second.mapNotNull {
            if(it.visibilityCheck?.invoke() == false) {
                if(it.appearInSearchIfVisibilityCheckFailed) {
                    userSettingDecorationOnly {
                        val nav = LocalNavController.current
                        NavigationItem(
                            title = stringResource(it.name),
                            style = NavigationItemStyle.MiscNoArrow,
                            subtitle = stringResource(
                                R.string.settings_search_option_exists_but_disabled,
                                stringResource(v.first.title)
                            ),
                            navigate = {
                                nav!!.navigate(v.first.navPath)
                            }
                        )
                    }
                } else {
                    null
                }
            } else {
                it
            }
        }
    }.filter {
        it.second.isNotEmpty()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun SearchScreen(navController: NavHostController = rememberNavController()) {
    val textFieldValue = remember { mutableStateOf("") }

    val query = normalizeString(textFieldValue.value)
    val results = searchResults(query)

    LazyColumn {
        item {
            ScreenTitle(
                stringResource(R.string.settings_search_menu_title),
                showBack = true,
                navController = navController
            )
        }
        item {
            Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SettingsTextEdit(textFieldValue, icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.settings_search_menu_title)
                    )
                }, autofocus = true)
            }
        }

        // Both of these were italic titleMedium, and italic is not a weight the
        // type scale has. Not the last of it either: the clipboard panel's
        // no-results state was still italic after this, and the suggestion strip
        // italicises a verbatim suggestion to this day.
        if(query.isBlank()) {
            item {
                SettingsEmptyState(stringResource(R.string.settings_search_enter_your_search))
            }
        } else if(results.isEmpty()) {
            item {
                SettingsEmptyState(stringResource(R.string.settings_search_no_options_found))
            }
        } else {
            results.forEach {
                val menu = it.first
                val settings = it.second
                // The screen a result belongs to is a section header, as it is on
                // every other screen that groups rows. It used to be a bespoke
                // clickable row with a forward arrow, and the rows beneath it were
                // drawn straight onto the background with a divider between groups
                // -- the shape the rest of the app was moved off.
                item { SettingSectionHeader(stringResource(menu.title)) }
                item { SettingsCard(rows = settings.map { setting -> setting.component }) }
            }

            item { BottomSpacer() }
        }
    }
}