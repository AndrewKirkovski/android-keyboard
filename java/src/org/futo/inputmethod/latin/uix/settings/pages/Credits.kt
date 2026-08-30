package org.futo.inputmethod.latin.uix.settings.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.settings.BottomSpacer
import org.futo.inputmethod.latin.uix.settings.NavigationItem
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.Route
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.settings.SpacedColumn
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.pages.credits.ThirdPartyItem
import org.futo.inputmethod.latin.uix.settings.pages.credits.ThirdPartyList
import org.futo.inputmethod.latin.uix.settings.pages.credits.codeContribs
import org.futo.inputmethod.latin.uix.settings.pages.credits.languageContribs
import org.futo.inputmethod.latin.uix.settings.pages.credits.layoutContribs
import org.futo.inputmethod.latin.uix.settings.pages.credits.text
import org.futo.inputmethod.latin.uix.settings.render
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.updates.openURI
import org.futo.inputmethod.latin.uix.settings.SettingSectionHeader
import org.futo.inputmethod.latin.uix.settings.SettingsCard
import org.futo.inputmethod.latin.uix.theme.app.Spacing

@Composable
@Preview(showBackground = true)
fun ProjectInfoView(
    projectIndex: Int = 1,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val info = ThirdPartyList[projectIndex]
    ScrollableList {
        ScreenTitle(
            stringResource(R.string.credits_menu_project_information_title, info.name),
            showBack = true, navController
        )

        Text(
            info.description,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        NavigationItem(
            title = stringResource(R.string.credits_menu_project_url_link),
            subtitle = info.projectUrl,
            style = NavigationItemStyle.ExternalLink,
            navigate = {
                context.openURI(info.projectUrl)
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            info.copyright,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            info.license.text(context),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun <T> VerticalGrid(
    modifier: Modifier = Modifier,
    items: List<T>,
    columns: Int,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable (item: T) -> Unit
) {
    val rows = items.chunked(columns)
    Column(modifier = modifier, verticalArrangement = verticalArrangement) {
        rows.forEachIndexed { rowindex, rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = horizontalArrangement) {
                rowItems.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        content(item)
                    }
                    if (index == rowItems.lastIndex && rowindex == rows.lastIndex) {
                        // Add a placeholder empty view
                        for (i in 0 until (columns - rowItems.size)) {
                            Spacer(
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * One category of contributors.
 *
 * This drew a saturated header bar and a gradient body in a hardcoded colour per
 * category -- #3157C6, #31c663, #c69931, #b231c6 -- none of which is in the app's
 * palette, and none of which said anything the heading above it did not. It was the
 * loudest surface in the app and it was the credits page.
 *
 * It is a section header over a card now, the same as every other group of things in
 * the app.
 */
@Composable
fun CreditCategorySection(
    title: String, names: List<String>, columns: Int = 2,
    thirdPartyInformation: List<ThirdPartyItem>? = null, navController: NavHostController? = null
) {
    SettingSectionHeader(title)
    SettingsCard {
        VerticalGrid(
            items = names.indices.toList(),
            columns = columns,
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            modifier = Modifier.padding(
                horizontal = Spacing.rowInset, vertical = Spacing.m
            )
        ) {
            val name = names[it]
            val thirdPartyInfo = thirdPartyInformation?.get(it)
            if (thirdPartyInfo != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController!!.navigate(Route.ThirdPartyInfo(it))
                        }
                        .padding(vertical = Spacing.xs)
                ) {
                    Text(
                        thirdPartyInfo.description,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        thirdPartyInfo.copyright,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                Text(
                    name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

val CreditsScreenLite = UserSettingsMenu(
    title = R.string.settings_title_credits,
    navPath = "credits", registerNavPath = false,
    settings = listOf(
        userSettingNavigationItem(
            title = (R.string.credits_menu_contribute_translations_button),
            style = NavigationItemStyle.Misc,
            navigate = {
                it.context.openURI("https://i18n-keyboard.futo.org/")
            }),
        userSettingNavigationItem(
            title = (R.string.credits_menu_contribute_keyboard_layouts_button),
            style = NavigationItemStyle.Misc,
            navigate = {
                it.context.openURI("https://github.com/futo-org/futo-keyboard-layouts")
            }),
        userSettingNavigationItem(
            title = (R.string.credits_menu_contribute_code_button),
            style = NavigationItemStyle.Misc,
            navigate = {
                it.context.openURI("https://github.com/futo-org/android-keyboard/")
            })

    )
)

@Preview(showBackground = true, heightDp = 1600)
@Composable
fun CreditsScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    ScrollableList {
        ScreenTitle(stringResource(R.string.settings_title_credits), showBack = true, navController)

        Column(Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.credits_menu_header_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = Spacing.rowInset + Spacing.m, vertical = Spacing.s
                )
            )

            CreditCategorySection(
                title = stringResource(R.string.credits_menu_team_translators_title),
                names = languageContribs
            )

            CreditCategorySection(
                title = stringResource(R.string.credits_menu_team_keyboard_layouts_title),
                names = layoutContribs
            )

            CreditCategorySection(
                title = stringResource(R.string.credits_menu_team_code_title),
                names = codeContribs
            )

            CreditCategorySection(
                title = stringResource(R.string.credits_menu_team_third_party_libraries_title2),
                columns = 1,
                names = ThirdPartyList.map { it.description },
                thirdPartyInformation = ThirdPartyList,
                navController = navController
            )

            ParagraphText(stringResource(R.string.credits_menu_nonaffiliation_notice))
        }

        CreditsScreenLite.render(showTitle = false)
        BottomSpacer()
    }
}