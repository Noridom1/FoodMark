package com.example.foodmark.core.presentation.foodstore.dishdetail

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodmark.LocalTopBarController
import com.example.foodmark.TopBarConfig
import com.example.foodmark.core.presentation.components.RoundedCard
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishScreen(
    navController: NavController,
    viewModel: EditDishViewModel = hiltViewModel()
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showDiscardDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }

    SideEffect {
        topBar.set(
            owner,
            TopBarConfig(
                visible = true,
                title = "Edit Dish",
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.isDirty) showDiscardDialog = true
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        enabled = state.canSave && state.isDirty,
                        onClick = {
                            scope.launch {
                                val ok = viewModel.save()
                                if (ok) navController.popBackStack()
                            }
                        }
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save dish")
                        }
                    }
                }
            )
        )
    }
    DisposableEffect(owner) { onDispose { topBar.clear(owner) } }

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.error != null && state.original == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.error}") }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Preview image (click to change)
                    RoundedCard {
                        DishPreviewImage(
                            url = state.imgUrl,
                            onImageSelected = { uri ->
                                scope.launch {
                                    val res = viewModel.addDishImage(uri)
                                    if (res) {
                                        viewModel.refresh()
                                        Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
                item {
                    RoundedCard {
                        Text("Dish Info", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = viewModel::onNameChange,
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.priceText,
                            onValueChange = viewModel::onPriceChange,
                            label = { Text("Price") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            isError = state.priceError != null,
                            supportingText = {
                                state.priceError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.imgUrl,
                            onValueChange = viewModel::onImgUrlChange,
                            label = { Text("Image URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Favorite", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = state.favorite,
                                onCheckedChange = viewModel::onFavoriteChange
                            )
                        }

                        if (state.error != null && !state.isSaving) {
                            Text(
                                "Error: ${state.error}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    navController.popBackStack()
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DishPreviewImage(
    url: String,
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var showPicker by remember { mutableStateOf(false) }
    val tmpUri = remember { mutableStateOf<Uri?>(null) }

// Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

// Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tmpUri.value?.let { onImageSelected(it) }
        }
    }

    // Create temp URI for camera
    fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "store_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    val shape = RoundedCornerShape(12.dp)
    if (url.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { showPicker = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = null)
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(shape)
                .clickable { showPicker = true },
            contentScale = ContentScale.Crop
        )
    }

// Picker dialog
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Upload Image") },
            text = { Text("Choose how you want to add the dish image.") },
            confirmButton = {
                TextButton(onClick = {
                    showPicker = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Gallery") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPicker = false
                    val uri = createImageUri()
                    tmpUri.value = uri
                    uri?.let { cameraLauncher.launch(it) }
                }) { Text("Camera") }
            }
        )
    }
}