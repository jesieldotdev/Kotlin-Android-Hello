package com.example.myapplication.screens
import java.time.LocalTime
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color // Mantenha esta importação
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.AppNav
// Importe suas cores personalizadas
import com.example.myapplication.ui.theme.* // Ajuste o pacote se necessário
import java.time.LocalDate

data class TodoItem(val id: Int, val text: String, val done: Boolean = false)



@Composable
fun TodoScreen() {
    var todos by remember { mutableStateOf(listOf<TodoItem>()) }
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var nextId by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundStart, BackgroundEnd) // Usando variáveis
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
                color = TextPrimary, // Usando variável
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = getDate(),
                fontSize = 16.sp,
                color = TextSecondary, // Usando variável
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo de adicionar tarefa
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(TodoCardBackground, RoundedCornerShape(16.dp)) // Usando variável
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
                        cursorColor = CursorColor // Usando variável
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        val text = input.text.trim()
                        if (text.isNotEmpty()) {
                            todos = todos + TodoItem(nextId, text)
                            nextId++
                            input = TextFieldValue("")
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(PurpleBlue, LightPurple) // Usando variáveis
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White // Poderia ser uma variável TextOnPrimaryButton por exemplo
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de tarefas
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
                                todos = todos.map {
                                    if (it.id == todo.id) it.copy(done = !it.done) else it
                                }
                            },
                            onDelete = {
                                todos = todos.filter { it.id != todo.id }
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
        color = if (todo.done) TodoCardDoneBackground else TodoCardBackground, // Usando variáveis
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (todo.done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (todo.done) "Desmarcar" else "Marcar como feito",
                    tint = if (todo.done) IconDoneColor else IconColor, // Usando variáveis
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = todo.text,
                fontSize = 18.sp,
                color = if (todo.done) TodoDoneText else TodoText, // Usando variáveis
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
                        tint = DeleteIconColor, // Usando variável
                    )
                }
            }
        }
    }
}


fun getDate():String{
    val today = LocalDate.now()
    val day = today.dayOfMonth
    val month = today.month

    return "$day $month"
}

@Preview
@Composable
fun AppPreview() {
    MaterialTheme {
        TodoScreen()
    }
}