package com.example.learncompose.data.room

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learncompose.data.contract.FeatureScreen

@Composable
fun UserScreen() {
//    val factory = UserViewModelFactory(AppContainer.userRepository)
//    // 3. 通过 factory 获取 ViewModel
//    val viewModel: UserViewModel = viewModel(factory = factory)

    val viewModel: UserViewModel = hiltViewModel()

    // 自动随生命周期收集 Flow 状态
    val userList by viewModel.users.collectAsStateWithLifecycle()

    var nameInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("姓名") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = ageInput,
            onValueChange = { ageInput = it },
            label = { Text("年龄") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.addUser(nameInput, ageInput.toIntOrNull() ?: 0)
                nameInput = ""
                ageInput = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("添加用户")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 用户列表
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(userList, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${user.fullName} (${user.age} 岁)")
                        TextButton(onClick = { viewModel.deleteUser(user) }) {
                            Text("删除")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    UserScreen()
}