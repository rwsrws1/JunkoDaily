package com.example.learncompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.learncompose.R
import com.example.learncompose.ui.theme.LearnComposeTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    FlowRow(
        Modifier.fillMaxSize().padding(10.dp).verticalScroll(rememberScrollState()),
    ) {
        var isShowDialog by remember { mutableStateOf(false) }
        Column(
            Modifier.width(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.search_24px),
                contentDescription = "card",
                Modifier.size(80.dp, 60.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.8f,
                colorFilter = ColorFilter.tint(Color.DarkGray)
            )
            Text(
                text = "sdakljkljvxzlknvlkzxjfoijqofw",
                Modifier.padding(top = 5.dp),
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Blue,
//                textAlign = TextAlign.Center,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//                style = TextStyle(
//                    lineHeight = 20.sp,
//                    letterSpacing = 0.5.sp
//                )
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painter = painterResource(R.drawable.check_circle_24px),
                contentDescription = "add",
//                Modifier.size(80.dp),
//                tint = Color.Black
            )
            Button(
                onClick = {
                    isShowDialog = true
                },
//                enabled = true,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Green,
//                    contentColor = Color.White
//                ),
//                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 50.dp
                ),
            ) {
                Icon(Icons.Default.Home, null)
                Spacer(Modifier.width(10.dp))
                Text("Home")
            }
        }
        var text by remember { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("用户名") },
            placeholder = { Text("请输入用户名" ) },
            leadingIcon = { Icon(painter = painterResource(R.drawable.favorite_24px), null) },
            trailingIcon = { Icon(painter = painterResource(R.drawable.menu_24px), null) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (isShowDialog) {
            Dialog(
                onDismissRequest = { isShowDialog = false }
            ) {
                Card {
                    Text("确定删除吗?")
                }
            }
        }
        Checkbox(
            checked = true,
            onCheckedChange = {}
        )
        RadioButton(
            selected = true,
            onClick = {}
        )
        Switch(
            checked = true,
            onCheckedChange = {}
        )
        Slider(
            state = SliderState(0.5f)
        )
        val itemList = (1..100).toList()
        LazyColumn(
            modifier = Modifier.size(100.dp, 200.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = rememberLazyListState()
        ) {
            items(itemList) { item ->
                Text(
                    text = "第${item}个"
                )
            }
        }
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