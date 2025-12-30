package com.example.cs426_mobileproject.friend.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cs426_mobileproject.LocalTopBarController
import com.example.cs426_mobileproject.TopBarConfig
import com.example.cs426_mobileproject.friend.domain.model.Friend
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.cs426_mobileproject.core.domain.model.RepoResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun FriendScreen (
    navController: NavController,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val friends = state.friends

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }
    val title = "My Friends"

    var showEmailDialog by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

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
                    IconButton(
                        enabled = true,
                        onClick = {
                            emailError = null
                            showEmailDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend",
//                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        )
    }

    DisposableEffect(owner) {
        onDispose { topBar.clear(owner) }
    }

    // 👉 Loading, Error, or Success UI
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer) // indent 2 sides
                ) {

                    Spacer(modifier = Modifier.height(12.dp))

                    if (friends.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("You don’t have any friends yet.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(friends) { friend ->
                                FriendListItem(
                                    friend = friend,
                                    onViewClick = {
                                        navController.navigate("friendDetail/${friend.friend_id}")
                                    }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.primary,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔹 Show dialog when button clicked
    if (showEmailDialog) {
        EmailInputDialog(
            onDismiss = { showEmailDialog = false },
            errorMessage = emailError,
            onConfirm = { email ->
                coroutineScope.launch {
                    val result = viewModel.addFriend(
                        email = email
                    )
                    when (result) {
                        is RepoResult.Success -> {
                            showEmailDialog = false
                            viewModel.refresh()
                        }
                        is RepoResult.Error -> {
                            emailError = result.message ?: "Failed to send friend request"
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun FriendListItem(friend: Friend, onViewClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!friend.img_url.isNullOrEmpty()) {
                AsyncImage(
                    model = friend.img_url,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Avatar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = friend.friend_name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }

        Text(
            text = "View",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable {
                onViewClick()
            }
        )
    }
}
