package org.futo.inputmethod.latin.uix.settings.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.settings.SettingSlider
import org.futo.inputmethod.latin.uix.settings.SettingsCard
import org.futo.inputmethod.latin.uix.settings.Tip
import org.futo.inputmethod.latin.xlm.AutocorrectThresholdSetting
import org.futo.inputmethod.latin.xlm.BinaryDictTransformerWeightSetting

@Preview
@Composable
fun AdvancedParametersScreen(navController: NavHostController = rememberNavController()) {
    val resources = LocalResources.current
    ScrollableList {
        ScreenTitle(stringResource(R.string.prediction_settings_transformer_advanced_params), showBack = true, navController)

        Tip(stringResource(R.string.prediction_settings_transformer_advanced_params_experimental_notice))

        // Both sliders share one card. Drawn straight onto the background they
        // had no surface at all in dark, and the screen had three left edges:
        // the tip, the titles, and the slider tracks.
        SettingsCard(rows = listOf({
        SettingSlider(
            title = stringResource(R.string.prediction_settings_transformer_advanced_params_transformer_lm_strength),
            subtitle = stringResource(R.string.settings_sub_lm_strength),
            setting = BinaryDictTransformerWeightSetting,
            range = 0.0f .. 100.0f,
            transform = {
                if(it > 99.9f) {
                    Float.POSITIVE_INFINITY
                } else if(it < 0.0001f) {
                    Float.NEGATIVE_INFINITY
                } else {
                    it
                }
            },
            indicator = {
                when {
                    it == Float.POSITIVE_INFINITY -> {
                        resources.getString(R.string.prediction_settings_transformer_advanced_params_transformer_lm_strength_always_transformer)
                    }
                    it == Float.NEGATIVE_INFINITY -> {
                        resources.getString(R.string.prediction_settings_transformer_advanced_params_transformer_lm_strength_always_binarydictionary)
                    }
                    (it > 0.1f) -> {
                        "a = ${String.format("%.1f", it)}"
                    }
                    else -> {
                        "a = $it"
                    }
                }
            },
            power = 2.5f
        )
        }, {
        SettingSlider(
            title = stringResource(R.string.prediction_settings_transformer_advanced_params_autocorrect_threshold),
            subtitle = stringResource(R.string.settings_sub_autocorrect_threshold),
            setting = AutocorrectThresholdSetting,
            range = 0.0f .. 25.0f,
            hardRange = 0.0f .. 25.0f,
            transform = {
                it
            },
            indicator = {
                "T = ${String.format("%.1f", it)}"
            },
            power = 2.5f
        )
        }))
    }
}