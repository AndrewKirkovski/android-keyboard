package org.futo.inputmethod.latin.uix.settings.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.settings.Settings
import org.futo.inputmethod.latin.uix.SUGGESTION_BLACKLIST
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.settings.SettingItem
import org.futo.inputmethod.latin.uix.settings.SettingsEmptyState
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.settings.useSharedPrefsBool
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingToggleSharedPrefs
import org.futo.inputmethod.latin.uix.settings.SettingsCard
import org.futo.inputmethod.latin.uix.theme.app.Spacing

@Composable
fun BlacklistedWord(word: String, remove: () -> Unit) { 
    SettingItem(word) {
        IconButton(onClick = remove) {
            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.prediction_settings_word_blacklist_remove_word_from_blacklist))
        }
    }
}

private val blockOffensiveWordsSetting =
    userSettingToggleSharedPrefs(
        title = R.string.prefs_block_potentially_offensive_title,
        subtitle = R.string.prefs_block_potentially_offensive_summary,
        key = Settings.PREF_BLOCK_POTENTIALLY_OFFENSIVE_2,
        default = {
            useSharedPrefsBool(Settings.PREF_BLOCK_POTENTIALLY_OFFENSIVE_LEGACY, true).value
        }
    ).copy(searchTags = R.string.prefs_block_potentially_offensive_tags)

private val blockSlursSetting =
    userSettingToggleSharedPrefs(
        title = R.string.prefs_block_slurs_title,
        subtitle = R.string.prefs_block_slurs_summary,
        key = Settings.PREF_BLOCK_SLURS,
        default = {
            useSharedPrefsBool(Settings.PREF_BLOCK_POTENTIALLY_OFFENSIVE_LEGACY, true).value
        }
    ).copy(searchTags = R.string.prefs_block_potentially_offensive_tags,
        visibilityCheck = {
            val legacyVal = useSharedPrefsBool(Settings.PREF_BLOCK_POTENTIALLY_OFFENSIVE_LEGACY, true).value
            useSharedPrefsBool(Settings.PREF_BLOCK_POTENTIALLY_OFFENSIVE_2, legacyVal).value == false
        })

val BlacklistScreenLite = UserSettingsMenu(
    title = R.string.settings_title_blacklist,
    navPath = "blacklist", registerNavPath = false,
    settings = listOf(
        blockOffensiveWordsSetting,
        blockSlursSetting,

        userSettingNavigationItem(
            title = R.string.prediction_settings_word_blacklist_edit_blacklisted_words_title,
            subtitle = R.string.prediction_settings_word_blacklist_edit_blacklisted_words_subtitle,
            style = NavigationItemStyle.Misc,
            navigateTo = "blacklist"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BlacklistScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val (blacklistedWords, setBlacklistedWords) = useDataStore(key = SUGGESTION_BLACKLIST.key, default = SUGGESTION_BLACKLIST.default)

    var newWord by remember { mutableStateOf("") }
    ScrollableList {
        ScreenTitle(stringResource(R.string.settings_title_blacklist), showBack = true, navController)

        // The two block-* switches are settings like any other, so they belong in a
        // card. They were rendered straight into the ScrollableList, which is why their
        // titles sat 12dp left of every other screen's.
        SettingsCard(buildList {
            add(blockOffensiveWordsSetting.component)
            if (blockSlursSetting.visibilityCheck?.invoke() != false) {
                add(blockSlursSetting.component)
            }
        })

        // No end padding: the icon button carries 12dp of its own around a 24dp
        // icon, so zero here puts its visual edge on the card inset. With 8dp it
        // sat 20dp in, and the row agreed with neither the card above nor below.
        Row(modifier = Modifier.padding(start = Spacing.cardInset, top = Spacing.l)) {
            TextField(value = newWord, onValueChange = {newWord = it}, modifier = Modifier.weight(1.0f), label = {
                Text(stringResource(R.string.prediction_settings_word_blacklist_add))
            })
            IconButton(onClick = {
                val newSet = blacklistedWords.toMutableSet()
                newSet.add(newWord)
                setBlacklistedWords(newSet)
                newWord = ""
            }, modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.prediction_settings_word_blacklist_add))
            }
        }

        if (blacklistedWords.isEmpty()) {
            // An accent-filled Tip made "you have blocked nothing" the loudest
            // thing on an otherwise blank screen.
            SettingsEmptyState(stringResource(R.string.prediction_settings_word_blacklist_none))
        } else {
            SettingsCard(blacklistedWords.map { word ->
                {
                    BlacklistedWord(word = word) {
                        val newSet = blacklistedWords.toMutableSet()
                        newSet.remove(word)
                        setBlacklistedWords(newSet)
                    }
                }
            })
        }
    }
}


@Preview
@Composable
fun PreviewBlacklist() {
    Column {
        BlacklistedWord(word = "Hello") {
            
        }
        
        BlacklistedWord(word = "Goodbye") {
            
        }
    }
}