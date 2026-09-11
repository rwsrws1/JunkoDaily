package com.example.learncompose.feature.experiment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import com.example.learncompose.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = TextFieldState(),
    onSearch: (String) -> Unit = {},
    searchResults: List<String> = listOf(),
    focusRequester: FocusRequester = FocusRequester(),
    trailingIconId: Int = R.drawable.placehodler_filled,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .semantics { isTraversalGroup = true }
            .fillMaxWidth()
    ) {
        DockedSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
//                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = {
//                        expanded = it
                                       },
                    placeholder = { Text("搜索") },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable(
                                onClick = {
                                    onSearch(textFieldState.text.toString())
                                }
                            ),
                            painter = painterResource(trailingIconId),
                            contentDescription = null
                        )
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = {
//                expanded = it
                               },
        ) {
            // Display search results in a scrollable column
            Text("搜索内容")
            Column() {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result) }
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SearchComponent()
}