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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
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

    // Sincronizar com dados salvos
    LaunchedEffect(savedTodos) {
        if (savedTodos.isNotEmpty()) {
            todos = savedTodos
            nextId = (savedTodos.maxOfOrNull { it.id } ?: -1) + 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundStart, BackgroundEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "TO-DO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = getDate(),
                fontSize = 16.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(TodoCardBackground, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Nova tarefa...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = CursorColor
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
                            brush = Brush.horizontalGradient(
                                listOf(PurpleBlue, LightPurple)
                            ), shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White
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
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        color = if (todo.done) TodoCardDoneBackground else TodoCardBackground,
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
                    tint = if (todo.done) IconDoneColor else IconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = todo.text,
                fontSize = 18.sp,
                color = if (todo.done) TodoDoneText else TodoText,
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
                        tint = DeleteIconColor,
                    )
                }
            }
        }
    }
}

// Função separada para adicionar todo (não é @Composable)
fun addTodo(
    text: String,
    currentTodos: List<TodoItem>,
    nextIdToUse: Int,
    onTodosChange: (List<TodoItem>) -> Unit,
    onInputChange: (TextFieldValue) -> Unit,
    onNextIdChange: (Int) -> Unit,
    dataStoreManager: DataStoreManager,
    scope: CoroutineScope
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