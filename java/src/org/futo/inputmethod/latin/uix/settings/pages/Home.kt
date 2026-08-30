package org.futo.inputmethod.latin.uix.settings.pages

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.BuildConfig
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.LocalNavController
import org.futo.inputmethod.latin.uix.TextEditPopupActivity
import org.futo.inputmethod.latin.uix.USE_SYSTEM_VOICE_INPUT
import org.futo.inputmethod.latin.uix.settings.NavigationItem
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.UserSetting
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.render
import org.futo.inputmethod.latin.uix.settings.useDataStoreValue
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingSection
import org.futo.inputmethod.updates.ConditionalMigrateUpdateNotice
import org.futo.inputmethod.updates.openManualUpdateCheck
import org.futo.inputmethod.latin.Subtypes
import org.futo.inputmethod.latin.SubtypesSetting
import org.futo.inputmethod.latin.settings.Settings
import org.futo.inputmethod.latin.uix.settings.useSharedPrefsBool
import androidx.compose.runtime.remember

val HomeScreenLite = UserSettingsMenu(
    title = R.string.settings_home_title,
    navPath = "home", registerNavPath = false,
    settings = listOf(
        userSettingSection(R.string.home_section_typing),

        // The design puts live state in these subtitles, so a home row says what is
        // behind it without being opened: which languages, whether swipe is on, which
        // theme. A destination name alone repeats the title.
        UserSetting(
            name = R.string.language_settings_title
        ) {
            val navController = LocalNavController.current
            val subtypes = useDataStoreValue(SubtypesSetting)
            val names = remember(subtypes) {
                subtypes.mapNotNull { Subtypes.convertToSubtype(it) }
                    .map { Subtypes.getName(it) }
                    .distinct()
            }
            NavigationItem(
                title = stringResource(R.string.language_settings_title),
                subtitle = names.takeIf { it.isNotEmpty() }?.joinToString(", "),
                style = NavigationItemStyle.HomePrimary,
                navigate = { navController!!.navigate("languages") }
            )
        },

        userSettingNavigationItem(
            title = R.string.prediction_settings_title,
            subtitle = R.string.home_subtitle_text,
            style = NavigationItemStyle.HomeTertiary,
            navigateTo = PredictiveTextMenu.navPath
        ),



        UserSetting(
            name = R.string.swipe_settings_title
        ) {
            val navController = LocalNavController.current
            val on = useSharedPrefsBool(Settings.PREF_GESTURE_INPUT, true)
            NavigationItem(
                title = stringResource(SwipeMenu.title),
                subtitle = stringResource(
                    if (on.value) R.string.home_state_on else R.string.home_state_off
                ),
                style = NavigationItemStyle.HomePrimary,
                navigate = { navController!!.navigate(SwipeMenu.navPath) }
            )
        },

        UserSetting(
            name = R.string.voice_input_settings_title
        ) {
            val navController = LocalNavController.current
            NavigationItem(
                title = stringResource(R.string.voice_input_settings_title),
                style = NavigationItemStyle.HomePrimary,
                subtitle = if(useDataStoreValue(USE_SYSTEM_VOICE_INPUT)) {
                    stringResource(R.string.voice_input_settings_builtin_disabled_notice)
                } else { null },
                navigate = { navController!!.navigate(VoiceInputMenu.navPath) }
            )
        },

        userSettingSection(R.string.home_section_keyboard),

        userSettingNavigationItem(
            title = R.string.keys_layout_settings_title,
            subtitle = R.string.home_subtitle_keys,
            style = NavigationItemStyle.HomeSecondary,
            navigateTo = KeyboardSettingsMenu.navPath
        ),

        userSettingNavigationItem(
            title = AppearanceMenu.title,
            style = NavigationItemStyle.HomeSecondary,
            navigateTo = AppearanceMenu.navPath
        ),

        userSettingNavigationItem(
            title = FeedbackMenu.title,
            subtitle = R.string.feedback_settings_subtitle,
            style = NavigationItemStyle.HomeSecondary,
            navigateTo = FeedbackMenu.navPath
        ),

        userSettingNavigationItem(
            title = R.string.action_settings_title,
            subtitle = R.string.home_subtitle_actions,
            style = NavigationItemStyle.HomeSecondary,
            navigateTo = "actions"
        ),

        userSettingSection(R.string.home_section_app),

        userSettingNavigationItem(
            title = R.string.payment_screen_short_title,
            style = NavigationItemStyle.HomePrimary,
            navigateTo = "payment"
        ).copy(visibilityCheck = {
            useDataStoreValue(IS_ALREADY_PAID) == false
        }, appearInSearchIfVisibilityCheckFailed = false),
        //}

        userSettingNavigationItem(
            title = R.string.help_menu_title,
            subtitle = R.string.home_subtitle_help,
            style = NavigationItemStyle.HomeSecondary,
            navigateTo = "help"
        ),

        //if(isDeveloper || LocalInspectionMode.current) {

        userSettingNavigationItem(
            title = MiscMenu.title,
            style = NavigationItemStyle.MiscNoArrow,
            navigateTo = "misc"
        ),

        userSettingNavigationItem(
            title = R.string.credits_menu_title,
            style = NavigationItemStyle.MiscNoArrow,
            navigateTo = "credits"
        ),

        userSettingNavigationItem(
            title = R.string.settings_check_for_updates_manually,
            style = NavigationItemStyle.Misc,
            navigate = { nav -> nav.context.openManualUpdateCheck() }
        ).copy(
            visibilityCheck = { BuildConfig.UPDATE_CHECKING },
            appearInSearchIfVisibilityCheckFailed = false
        ),

        userSettingNavigationItem(
            title = R.string.dev_settings_title,
            style = NavigationItemStyle.HomeTertiary,
            navigateTo = "developer"
        ).copy(visibilityCheck = {
            useDataStoreValue(IS_DEVELOPER) == true || LocalInspectionMode.current
        }),
        //}

        // MiscNoArrow draws no circle behind the icon, so these two keep the
        // quieter weight the style gives them while their titles start on the
        // same left edge as every row above.
    )
)

@Preview(showBackground = true)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isDeveloper = useDataStoreValue(IS_DEVELOPER)
    val isPaid = useDataStoreValue(IS_ALREADY_PAID)

    Column {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.english_ime_settings), style = MaterialTheme.typography.headlineMedium, modifier = Modifier
                    .align(CenterVertically)
                    .weight(1.0f))

                Spacer(Modifier.width(4.dp))

                IconButton(onClick = {
                    navController.navigate("search")
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(
                        R.string.settings_search_menu_title
                    ))
                }
            }

            ConditionalMigrateUpdateNotice()
            ConditionalUnpaidNoticeWithNav(navController)

            HomeScreenLite.render(showTitle = false)


            Spacer(modifier = Modifier.height(16.dp))

            if(isPaid || LocalInspectionMode.current) {
                Text(
                    stringResource(R.string.payment_paid_version_indicator),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        TextButton(onClick = {
            val intent = Intent()
            intent.setClass(context, TextEditPopupActivity::class.java)
            intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_try_typing_here), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth())
        }
    }
}