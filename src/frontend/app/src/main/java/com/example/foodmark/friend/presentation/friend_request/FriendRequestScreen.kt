package com.example.cs426_mobileproject.friend.presentation.friend_request

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cs426_mobileproject.LocalTopBarController
import com.example.cs426_mobileproject.TopBarConfig
import com.example.cs426_mobileproject.utils.ProfileAvatar
import com.example.cs426_mobileproject.utils.ProfileReadOnlyFields
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch

@Composable
fun FriendRequestScreen(
    navController: NavController,
    viewModel: FriendRequestViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }
    val coroutineScope = rememberCoroutineScope()

    val title = "Friend Request"

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

        state.friendRequestId == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Friend Request not found")
            }
        }

        else -> {
            val p = state.friendProfile
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileAvatar(
                    url = p!!.img_url,
                    onImageSelected = {uri -> }
                )
                Text(
                    text = p.name ?: "No name",
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID: ${p.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ProfileReadOnlyFields(
                    email = p.email.orEmpty(),
                    phone = p.phone.orEmpty(),
                    dob = p.dob.orEmpty()
                )

                if (state.error != null) {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.removeFriendRequest()
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Reject")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.setFriendShip()
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Accept")
                    }


                }

            }
        }
    }
}
