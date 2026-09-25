package com.qurafy.hamraj37.ui.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.qurafy.hamraj37.data.model.QuranMetaData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JumpToPageDialog(
    currentPage: Int,
    maxPage: Int = 604,
    bookmarkedPages: Set<Int> = emptySet(),
    onDismiss: () -> Unit,
    onJumpToPage: (Int) -> Unit
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    var isError by remember { mutableStateOf(false) }

    fun submit() {
        val pageNum = pageInput.toIntOrNull()
        if (pageNum != null && pageNum in 1..maxPage) {
            isError = false
            onJumpToPage(pageNum)
        } else {
            isError = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = "Jump to Page")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Enter page number (1 to $maxPage):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pageInput,
                    onValueChange = { input ->
                        pageInput = input.filter { it.isDigit() }
                        isError = false
                    },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(text = "Please enter a valid page number between 1 and $maxPage")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { submit() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Saved Bookmarks (${bookmarkedPages.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (bookmarkedPages.isEmpty()) {
                    Text(
                        text = "No saved bookmarks yet. Tap the bookmark icon on the bottom bar to bookmark pages.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        bookmarkedPages.sorted().forEach { page ->
                            val label = "Page $page"

                            SuggestionChip(
                                onClick = {
                                    pageInput = page.toString()
                                    submit()
                                },
                                label = { Text(text = label) },
                                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { submit() }) {
                Text(text = "Jump")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
