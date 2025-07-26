import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.screens.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    companion object {
        val TODOS_KEY = stringPreferencesKey("todos_list")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    val todosFlow: Flow<List<TodoItem>> = context.dataStore.data
        .map { preferences ->
            val todosString = preferences[TODOS_KEY]
            if (todosString != null) {
                try {
                    Json.decodeFromString(ListSerializer(TodoItem.serializer()), todosString)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    suspend fun saveTodos(todos: List<TodoItem>) {
        context.dataStore.edit { settings ->
            val todosString = Json.encodeToString(ListSerializer(TodoItem.serializer()), todos)
            settings[TODOS_KEY] = todosString
        }
    }

    val usernameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { settings ->
            settings[USERNAME_KEY] = username
        }
    }
}
