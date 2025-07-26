import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.screens.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

val Context.todoDataStore: DataStore<Preferences> by preferencesDataStore(name = "todo_prefs")




class DataStoreManager(private val context: Context) {

    companion object {
        private val TODOS_KEY = stringPreferencesKey("todos_list")
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }

    val todosFlow: Flow<List<TodoItem>> = context.todoDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[TODOS_KEY] ?: "[]"
            Json.decodeFromString<List<TodoItem>>(jsonString)
        }

    suspend fun saveTodos(todos: List<TodoItem>) {
        context.todoDataStore.edit { preferences ->
            preferences[TODOS_KEY] = Json.encodeToString(todos)
        }
    }

    val isDarkModeFlow: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false
        }

    suspend fun toggleDarkMode(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDark
        }
    }

    suspend fun isDarkModeInitialValue(): Boolean {
        return context.themeDataStore.data.first()[IS_DARK_MODE_KEY] ?: false
    }
}
