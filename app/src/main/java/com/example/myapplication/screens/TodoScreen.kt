package com.example.myapplication.screens

import DataStoreManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Mantém
import androidx.compose.material.icons.outlined.BrightnessMedium // Ícone para modo escuro/claro
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Remova os imports de cores específicas de com.example.myapplication.ui.theme se você as usava diretamente.
// Deixe o MaterialTheme cuidar disso.
import kotlinx.serialization.Serializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Serializable
data class TodoItem(val id: Int, val text: String, val done: Boolean = false)

@Composable
fun TodoScreen(dataStoreManager: DataStoreManager) {
    var todos by remember { mutableStateOf(listOf<TodoItem>()) }
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var nextId by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val savedTodos by dataStoreManager.todosFlow.collectAsState(initial = emptyList())
    val isDarkMode by dataStoreManager.isDarkModeFlow.collectAsState(initial = false) // Observe o tema

    // Sincronizar com dados salvos
    LaunchedEffect(savedTodos) {
        if (savedTodos.isNotEmpty()) {
            todos = savedTodos
            nextId = (savedTodos.maxOfOrNull { it.id } ?: -1) + 1
        }
    }

    // Cores baseadas no tema
    val backgroundColorStart = if (isDarkMode) MaterialTheme.colorScheme.background else Color(0xFFF2F5F8) // Exemplo, ajuste para suas cores de tema
    val backgroundColorEnd = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE6E9ED) // Exemplo
    val textPrimaryColor = MaterialTheme.colorScheme.onBackground
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant // Ou outra cor adequada
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val cardDoneBackgroundColor = if (isDarkMode) Color(0xFF385139) else Color(0xFFE8F5E9)
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    val iconDoneColor = if (isDarkMode) Color(0xFF66BB6A) else Color(0xFF4CAF50)
    val deleteIconColor = if (isDarkMode) Color(0xFFE57373) else Color(0xFFF44336)
    val cursorColor = MaterialTheme.colorScheme.primary
    val todoTextColor = MaterialTheme.colorScheme.onSurface
    val todoDoneTextColor = if (isDarkMode) Color(0xFFA5D6A7) else Color(0xFF2E7D32)
    val addButtonBrush = Brush.horizontalGradient(
        if (isDarkMode) listOf(Color(0xFF7E57C2), Color(0xFF5C6BC0))
        else listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(backgroundColorStart, backgroundColorEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TO-DO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = getDate(),
                        fontSize = 16.sp,
                        color = textSecondaryColor,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                // Botão de alternância de tema
                IconButton(onClick = {
                    scope.launch {
                        dataStoreManager.toggleDarkMode(!isDarkMode)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.BrightnessMedium,
                        contentDescription = "Alternar Tema",
                        tint = textPrimaryColor
                    )
                }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(cardBackgroundColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Nova tarefa...", color = textSecondaryColor) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = cursorColor,
                        focusedTextColor = textPrimaryColor,
                        unfocusedTextColor = textPrimaryColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        addTodo(
                            text = input.text.trim(),
                            currentTodos = todos,
                            nextIdToUse = nextId,
                            onTodosChange = { todos = it },
                            onInputChange = { input = it },
                            onNextIdChange = { nextId = it },
                            dataStoreManager = dataStoreManager,
                            scope = scope
                        )
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 4.dp)
                        .background(
                            brush = addButtonBrush,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White // Ícone do botão de adicionar geralmente é branco
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(todos, key = { it.id }) { todo ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TodoCard(
                            todo = todo,
                            isDarkMode = isDarkMode, // Passe o estado do tema
                            onToggle = {
                                val updatedTodos = todos.map {
                                    if (it.id == todo.id) it.copy(done = !it.done) else it
                                }
                                todos = updatedTodos
                                scope.launch {
                                    dataStoreManager.saveTodos(updatedTodos)
                                }
                            },
                            onDelete = {
                                val updatedTodos = todos.filter { it.id != todo.id }
                                todos = updatedTodos
                                scope.launch {
                                    dataStoreManager.saveTodos(updatedTodos)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TodoCard(
    todo: TodoItem,
    isDarkMode: Boolean, // Recebe o estado do tema
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    // Cores do card baseadas no tema
    val cardBackgroundColor = if (todo.done) {
        if (isDarkMode) Color(0xFF385139) else Color(0xFFE8F5E9)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val iconColor = if (todo.done) {
        if (isDarkMode) Color(0xFF66BB6A) else Color(0xFF4CAF50)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val textColor = if (todo.done) {
        if (isDarkMode) Color(0xFFA5D6A7) else Color(0xFF2E7D32)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val deleteIconColor = if (isDarkMode) Color(0xFFE57373) else Color(0xFFF44336)


    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        color = cardBackgroundColor, // Use a cor dinâmica
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (todo.done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (todo.done) "Desmarcar" else "Marcar como feito",
                    tint = iconColor, // Use a cor dinâmica
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = todo.text,
                fontSize = 18.sp,
                color = textColor, // Use a cor dinâmica
                fontWeight = if (todo.done) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            AnimatedVisibility(visible = todo.done) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = deleteIconColor, // Use a cor dinâmica
                    )
                }
            }
        }
    }
}

// Função addTodo (não precisa de muitas alterações visuais, mas usa o scope do DataStoreManager)
fun addTodo(
    text: String,
    currentTodos: List<TodoItem>,
    nextIdToUse: Int,
    onTodosChange: (List<TodoItem>) -> Unit,
    onInputChange: (TextFieldValue) -> Unit,
    onNextIdChange: (Int) -> Unit,
    dataStoreManager: DataStoreManager, // Já recebe
    scope: CoroutineScope // Já recebe
) {
    if (text.isNotEmpty()) {
        val newTodo = TodoItem(nextIdToUse, text)
        val updatedTodos = currentTodos + newTodo
        onTodosChange(updatedTodos)
        onInputChange(TextFieldValue(""))
        onNextIdChange(nextIdToUse + 1)

        scope.launch {
            dataStoreManager.saveTodos(updatedTodos)
        }
    }
}

fun getDate(): String {
    val today = LocalDate.now()
    val day = today.dayOfMonth
    val month = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "$day de $month"
}
