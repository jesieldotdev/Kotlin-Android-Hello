package com.example.myapplication.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun ModernTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF6B7280), fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color(0xFF9CA3AF)
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea)
        )
    )
}