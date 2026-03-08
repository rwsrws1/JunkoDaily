package com.example.learncompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learncompose.R
import com.example.learncompose.ui.theme.LearnComposeTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Greeting() {
    FlowRow(
        Modifier.fillMaxSize().padding(10.dp),
    ) {
        Column(
            Modifier.width(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.search_24px),
                contentDescription = "card",
                Modifier.size(80.dp, 60.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.8f,
                colorFilter = ColorFilter.tint(Color.DarkGray)
            )
            Text(
                text = "sdakljkljvxzlknvlkzxjfoijqofw",
                Modifier.padding(top = 5.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
        Icon(
            painter = painterResource(R.drawable.add_24px),
            contentDescription = "add",
            Modifier.size(80.dp),
            tint = Color.Red
        )
    }
}

@Composable
fun HomeScreen() {
    Scaffold {paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Greeting()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LearnComposeTheme {
        Greeting()
    }
}