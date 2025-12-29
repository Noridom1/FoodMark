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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodmark.LocalTopBarController
import com.example.foodmark.TopBarConfig
import com.example.foodmark.core.presentation.components.HeroImage
import com.example.foodmark.core.presentation.components.KeyValueRow
import com.example.foodmark.core.presentation.components.RoundedCard
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    navController: NavController,
    viewModel: DishDetailViewModel = hiltViewModel(),
    onEdit: (storeId: String, dishId: Int) -> Unit = { storeId, dishId ->
        navController.navigate("edit/store/$storeId/dish/$dishId")
    }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val dish = state.dish

    val topBar = LocalTopBarController.current
    val owner = remember { Any() }

    val title = dish?.name
    val storeId = dish?.store_id
    val dishId = dish?.dish_id

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showPicker by remember { mutableStateOf(false) }
    val tmpUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                val res = viewModel.addDishImage(uri)
                if (res == false) {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.refresh()
                    Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tmpUri.value?.let { uri ->
                scope.launch {
                    val res = viewModel.addDishImage(uri)
                    if (res == false) {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.refresh()
                        Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "dish_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    SideEffect {
        topBar.set(
            owner,
            TopBarConfig(
                visible = true,
                title = title,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (storeId != null && dishId != null) {
                        IconButton(onClick = { onEdit(storeId, dishId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit dish")
                        }
                    }
                }
            )
        )
    }
    DisposableEffect(owner) { onDispose { topBar.clear(owner) } }

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.error}") }
        dish == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Dish not found") }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HeroImage(
                        imageUrl = dish.img_url?.toString(),
                        onClick = { showPicker = true },
                        height = 220.dp
                    )
                }
                item {
                    RoundedCard {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = dish.name,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${dish.price}VND",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    RoundedCard {
                        Text("Details", style = MaterialTheme.typography.titleMedium)
                        Divider()
                        KeyValueRow("Favorite", if (dish.favorite) "Yes" else "No")
//                        dish.description?.takeIf { it.isNotBlank() }?.let {
//                            Divider()
//                            Text(it, style = MaterialTheme.typography.bodyMedium)
//                        }
                    }
                }
            }

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
    }
}