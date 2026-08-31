package org.futo.inputmethod.latin.uix.settings.pages

import android.content.Intent
import androidx.compose.runtime.Composable
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.ANIMATE_BUBBLE
import org.futo.inputmethod.latin.uix.AUDIO_FOCUS
import org.futo.inputmethod.latin.uix.CAN_EXPAND_SPACE
import org.futo.inputmethod.latin.uix.DISALLOW_SYMBOLS
import org.futo.inputmethod.latin.uix.ENABLE_SOUND
import org.futo.inputmethod.latin.uix.PREFER_BLUETOOTH
import org.futo.inputmethod.latin.uix.USE_PERSONAL_DICT
import org.futo.inputmethod.latin.uix.USE_SYSTEM_VOICE_INPUT
import org.futo.inputmethod.latin.uix.USE_VAD_AUTOSTOP
import org.futo.inputmethod.latin.uix.VERBOSE_PROGRESS
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.useDataStoreValue
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingSection
import org.futo.inputmethod.latin.uix.settings.userSettingToggleDataStore

internal val visibilityCheckNotSystemVoiceInput = @Composable {
    useDataStoreValue(USE_SYSTEM_VOICE_INPUT) == false
}

val VoiceInputMenu = UserSettingsMenu(
    title = R.string.settings_title_voice,
    navPath = "voiceInput", registerNavPath = true,
    settings = listOf(
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_disable_builtin_voice_input,
            subtitle = R.string.voice_input_settings_disable_builtin_voice_input_subtitle,
            setting = USE_SYSTEM_VOICE_INPUT
        ),

        //if(!systemVoiceInput.value) {

        /*
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_verbose_progress,
            subtitle = R.string.voice_input_settings_verbose_progress_subtitle,
            setting = VERBOSE_PROGRESS
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),
         */

        userSettingSection(R.string.voice_settings_recording_section),
        userSettingToggleDataStore(
            title = R.string.settings_row_voice_personal_dict,
            subtitle = R.string.settings_sub_voice_personal_dict,
            setting = USE_PERSONAL_DICT
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingToggleDataStore(
            title = R.string.settings_row_bluetooth_mic,
            subtitle = R.string.settings_sub_bluetooth_mic,
            setting = PREFER_BLUETOOTH
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingToggleDataStore(
            title = R.string.settings_row_audio_focus,
            subtitle = R.string.voice_input_settings_audio_focus_subtitle,
            setting = AUDIO_FOCUS
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_suppress_symbols,
            setting = DISALLOW_SYMBOLS
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingSection(R.string.voice_settings_behavior_section),
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_long_form,
            subtitle = R.string.settings_sub_longform,
            setting = CAN_EXPAND_SPACE
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_autostop_vad,
            subtitle = R.string.settings_sub_autostop_vad,
            setting = USE_VAD_AUTOSTOP
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_animate_bubble,
            subtitle = R.string.voice_input_settings_animate_bubble_subtitle,
            setting = ANIMATE_BUBBLE
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),

        userSettingSection(R.string.voice_settings_models_section),
        userSettingNavigationItem(
            title = R.string.voice_input_settings_change_models,
            subtitle = R.string.voice_input_settings_change_models_subtitle,
            style = NavigationItemStyle.Misc,
            navigateTo = "languages"
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),
        //}
    )
)