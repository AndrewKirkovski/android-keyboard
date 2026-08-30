package org.futo.inputmethod.latin.uix.settings.pages

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.settings.Settings
import org.futo.inputmethod.latin.uix.ENABLE_SOUND
import org.futo.inputmethod.latin.uix.settings.LocalSharedPrefsCache
import org.futo.inputmethod.latin.uix.settings.SettingSlider
import org.futo.inputmethod.latin.uix.settings.SyncDataStoreToPreferencesFloat
import org.futo.inputmethod.latin.uix.settings.SyncDataStoreToPreferencesInt
import org.futo.inputmethod.latin.uix.settings.Tip
import org.futo.inputmethod.latin.uix.settings.UserSetting
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.userSettingSection
import org.futo.inputmethod.latin.uix.settings.userSettingToggleDataStore
import org.futo.inputmethod.latin.uix.settings.userSettingToggleSharedPrefs
import kotlin.math.roundToInt

/**
 * What the keyboard does when a key is pressed: the popup, the vibration, the sound.
 *
 * One subject that was spread across two screens and a buried section header. Popup,
 * vibration and sound sat among the text-behaviour settings on Typing preferences, and
 * voice input's start and cancel sound sat on Voice Input. Somebody asking "how does
 * this thing feel to type on" had to visit both screens and know the answer was split.
 *
 * Each slider sits directly under the switch that enables it and appears only when that
 * switch is on, so the screen never offers a strength control for something that is off.
 */
val FeedbackMenu = UserSettingsMenu(
    title = R.string.feedback_settings_title,
    navPath = "feedback", registerNavPath = true,
    settings = listOf(
        userSettingSection(R.string.feedback_settings_touch_section),
        userSettingToggleSharedPrefs(
            title = R.string.popup_on_keypress,
            key = Settings.PREF_POPUP_ON,
            default = {booleanResource(R.bool.config_default_key_preview_popup)}
        ),
        userSettingToggleSharedPrefs(
            title = R.string.vibrate_on_keypress,
            key = Settings.PREF_VIBRATE_ON,
            default = {booleanResource(R.bool.config_default_vibration_enabled)}
        ),
        UserSetting(
            name = R.string.typing_settings_vibration_strength,
            visibilityCheck = {
                LocalSharedPrefsCache.current!!.currSharedPrefs.getBoolean(
                    Settings.PREF_VIBRATE_ON,
                    booleanResource(R.bool.config_default_vibration_enabled)
                )
            },
            component = {
                val context = LocalContext.current
                val resources = LocalResources.current
                SyncDataStoreToPreferencesInt(vibrationDurationSetting, Settings.PREF_VIBRATION_DURATION_SETTINGS)

                SettingSlider(
                    title = stringResource(R.string.typing_settings_vibration_strength),
                    setting = vibrationDurationSetting,
                    range = -1.0f .. 100.0f,
                    hardRange = -1.0f .. 2000.0f,
                    transform = { it.roundToInt() },
                    indicator = {
                        if(it == -1) {
                            resources.getString(R.string.typing_settings_vibration_strength_default)
                        } else {
                            resources.getString(R.string.abbreviation_unit_milliseconds, "$it")
                        }
                    }
                )
            }
        ),
        userSettingToggleSharedPrefs(
            title = R.string.sound_on_keypress,
            key = Settings.PREF_SOUND_ON,
            default = {booleanResource(R.bool.config_default_sound_enabled)}
        ),
        UserSetting(
            name = R.string.typing_settings_keypress_sound_volume,
            visibilityCheck = {
                LocalSharedPrefsCache.current!!.currSharedPrefs.getBoolean(
                    Settings.PREF_SOUND_ON,
                    booleanResource(R.bool.config_default_sound_enabled)
                )
            },
            component = {
                val context = LocalContext.current
                val resources = LocalResources.current
                SyncDataStoreToPreferencesFloat(keySoundVolumeSetting, Settings.PREF_KEYPRESS_SOUND_VOLUME)

                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val value = remember { mutableFloatStateOf(0.0f) }
                val ringerMode = remember { mutableStateOf(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) }
                val firstPlayback = remember { mutableStateOf(false) }

                LaunchedEffect(value.floatValue) {
                    delay(100L) // debounce
                    if(firstPlayback.value == false) {
                        firstPlayback.value = true
                        return@LaunchedEffect
                    }
                    val volume = value.floatValue.let {
                        if(it == -1.0f) {
                            Settings.readDefaultKeypressSoundVolume(resources)
                        } else {
                            it
                        }
                    }

                    val shouldPlay = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                    ringerMode.value = shouldPlay

                    if(shouldPlay) {
                        audioManager.playSoundEffect(
                            AudioManager.FX_KEYPRESS_STANDARD,
                            volume
                        )
                    }
                }

                if(!ringerMode.value) {
                    Tip(stringResource(R.string.typing_settings_keypress_sound_volume_ringer_mode_warning))
                }

                Tip(stringResource(R.string.typing_settings_keypress_sound_volume_vendor_warning))

                SettingSlider(
                    title = stringResource(R.string.typing_settings_keypress_sound_volume),
                    setting = keySoundVolumeSetting,
                    range = 0.0f .. 1.0f,
                    hardRange = 0.0f .. 1.0f,
                    transform = {
                        value.floatValue = it
                        if(it == 0.0f) {
                            -1.0f
                        } else {
                            it
                        }
                    },
                    indicator = {
                        if(it <= 0.0f) {
                            resources.getString(R.string.typing_settings_keypress_sound_volume_default)
                        } else {
                            "${(it * 100.0f).roundToInt()}%"
                        }
                    }
                )
            }
        ),
        userSettingSection(R.string.feedback_settings_voice_section),
        // Still gated on the built-in engine: a sound the keyboard does not play is
        // not a setting.
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_indication_sounds,
            subtitle = R.string.voice_input_settings_indication_sounds_subtitle,
            setting = ENABLE_SOUND
        ).copy(visibilityCheck = visibilityCheckNotSystemVoiceInput),
    )
)
