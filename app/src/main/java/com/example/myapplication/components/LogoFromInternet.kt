package com.example.myapplication.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding

@Composable
fun LogoFromInternet() {
    AsyncImage(
        model = "https://upload.wikimedia.org/wikipedia/commons/a/ab/Logo_TV_2015.png",
        contentDescription = "Logo da Internet",
        modifier = Modifier
            .size(60.dp)
            .padding(20.dp)
    )
}