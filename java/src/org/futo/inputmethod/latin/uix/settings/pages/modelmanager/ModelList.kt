package org.futo.inputmethod.latin.uix.settings.pages.modelmanager

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.settings.NavigationItem
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.settings.Tip
import org.futo.inputmethod.latin.xlm.ModelInfo
import org.futo.inputmethod.latin.xlm.ModelPaths
import org.futo.inputmethod.updates.openURI
import java.net.URLEncoder
import java.util.Locale
import org.futo.inputmethod.latin.uix.settings.SettingsCard

@Composable
fun ModelNavigationItem(navController: NavHostController, name: String, isPrimary: Boolean, path: String) {
    val style = if (isPrimary) {
        NavigationItemStyle.HomePrimary
    } else {
        NavigationItemStyle.MiscNoArrow
    }

    NavigationItem(
        title = name,
        style = style,
        navigate = {
            navController.navigate("model/${URLEncoder.encode(path, "utf-8")}")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ModelListScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val models = if(LocalInspectionMode.current) { PreviewModels } else {
        remember {
            ModelPaths.getModels(context).mapNotNull {
                it.loadDetails()
            }
        }
    }

    val modelChoices = remember { runBlocking { ModelPaths.getModelOptions(context) } }

    val modelsByLanguage: MutableMap<String, MutableList<ModelInfo>> = mutableMapOf()
    models.forEach { model ->
        modelsByLanguage.getOrPut(model.languages.joinToString(" ")) { mutableListOf() }.add(model)
    }

    ScrollableList {
        ScreenTitle(stringResource(R.string.prediction_settings_transformer_models), showBack = true, navController)

        Tip(stringResource(R.string.prediction_settings_transformer_english_notice))

        modelsByLanguage.forEach { item ->
            Spacer(modifier = Modifier.height(32.dp))
            // The key is `model.languages.joinToString(" ")`, so a model covering more
            // than one language arrives here as "en pl". Locale("en pl") is not a
            // locale, and its displayLanguage is the raw string, which then appears as
            // the section heading. Resolve each language separately.
            ScreenTitle(
                item.key.split(" ")
                    .filter { it.isNotEmpty() }
                    .joinToString(" + ") { Locale(it).displayLanguage }
            )

            SettingsCard(item.value.map { model ->
                {
                    val name = if (model.finetune_count > 0) {
                        model.name.trim() + " (local finetune)"
                    } else {
                        model.name.trim()
                    }

                    ModelNavigationItem(
                        name = name,
                        isPrimary = model.path == modelChoices[item.key]?.path?.absolutePath,
                        path = model.path,
                        navController = navController
                    )
                }
            })
        }

        Spacer(modifier = Modifier.height(32.dp))
        ScreenTitle("Actions")
        SettingsCard(listOf {
            NavigationItem(
                title = "Import from file",
                style = NavigationItemStyle.Misc,
                navigate = {
                    openModelImporter(context)
                }
            )
        })
    }
}