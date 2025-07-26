import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemeDataStore(private val context: Context) {

    companion object {
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false // Padrão para modo claro se não definido
        }

    suspend fun toggleDarkMode(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDark
        }
    }
}
