package com.example.cs426_mobileproject.friend.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.cs426_mobileproject.LocalTopBarController
import com.example.cs426_mobileproject.TopBarConfig
import com.example.cs426_mobileproject.core.presentation.components.CenteredHeader
import com.example.cs426_mobileproject.core.presentation.components.RoundedFieldContainer
import com.example.cs426_mobileproject.utils.ProfileAvatar
import com.example.cs426_mobileproject.utils.ProfileReadOnlyFields

@Composable
fun FriendDetailScreen(
    navController: NavController,
    viewModel: FriendDetailViewModel = hiltViewModel(),
    onUnfriended: () -> Unit = {}
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var showConfirm by remember { mutableStateOf(false) }

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }

    val title = state.friendProfile?.name

    SideEffect {
        topBar.set(
            owner,
            TopBarConfig(
                visible = true,
                title = title,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                }
            )
        )
    }

    DisposableEffect(owner) {
        onDispose { topBar.clear(owner) }
    }


    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.friendProfile == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Friend not found")
            }
        }
        else -> {
            val p = state.friendProfile
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val p = state.friendProfile!!
                    CenteredHeader(
                        imageUrl = p.img_url,
                        title = p.name,
                        idText = "ID: ${p.id}",
                        onImageSelected = {uri -> },
                        clickable = false
                    )

                    RoundedFieldContainer {
                        ProfileReadOnlyFields(
                            email = p.email.orEmpty(),
                            phone = p.phone.orEmpty(),
                            dob = p.dob.orEmpty()
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    //                ProfileAvatar(url = p!!.img_url)
                    //                Text(
                    //                    text = p.name ?: "No name",
                    //                    style = MaterialTheme.typography.headlineSmall,
                    //                    maxLines = 1,
                    //                    overflow = TextOverflow.Ellipsis
                    //                )
                    //                Text(
                    //                    text = "ID: ${p.id}",
                    //                    style = MaterialTheme.typography.bodySmall,
                    //                    color = MaterialTheme.colorScheme.onSurfaceVariant
                    //                )
                    //
                    //                ProfileReadOnlyFields(
                    //                    email = p.email.orEmpty(),
                    //                    phone = p.phone.orEmpty(),
                    //                    dob = p.dob.orEmpty()
                    //                )
                    //
                    //                if (state.error != null) {
                    //                    Text(
                    //                        text = "Error: ${state.error}",
                    //                        color = MaterialTheme.colorScheme.error,
                    //                        style = MaterialTheme.typography.bodySmall
                    //                    )
                    //                }
                    //
                    Spacer(Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showConfirm = true },
                        enabled = !state.isUnfriending
                    ) {
                        if (state.isUnfriending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Unfriend")
                    }

                    //                Spacer(Modifier.height(8.dp))
                    //
                    //                Button(
                    //                    modifier = Modifier.fillMaxWidth(),
                    //                    onClick = { showConfirm = true },
                    //                    enabled = !state.isUnfriending
                    //                ) {
                    //                    if (state.isUnfriending) {
                    //                        CircularProgressIndicator(
                    //                            modifier = Modifier.size(18.dp),
                    //                            strokeWidth = 2.dp
                    //                        )
                    //                        Spacer(Modifier.width(8.dp))
                    //                    }
                    //                    Text("Unfriend")
                    //                }
                }
            }

            if (showConfirm) {
                AlertDialog(
                    onDismissRequest = { showConfirm = false },
                    title = { Text("Unfriend ${p!!.name ?: "this user"}?") },
                    text = { Text("This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirm = false
                                viewModel.unfriend(onSuccess = onUnfriended)
                            }
                        ) { Text("Unfriend") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
