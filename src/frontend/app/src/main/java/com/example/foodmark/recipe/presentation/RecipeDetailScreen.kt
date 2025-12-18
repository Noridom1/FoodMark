package com.example.foodmark.recipe.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodmark.LocalTopBarController
import com.example.foodmark.TopBarConfig
import com.example.foodmark.recipe.domain.model.CookingStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()


    val topBar = LocalTopBarController.current
    val owner = remember { Any() }

    val title = state.recipe?.name

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

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.addOrChangeImage(uri)
        }
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        }
        state.recipe == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Recipe not found")
            }
        }
        else -> {
            val recipe = state.recipe
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    RecipeHeader(
                        imageUrl = recipe!!.img_url,
                        title = recipe.name,
                        summary = recipe.summary,
                        onAddImageClick = { imagePicker.launch("image/*") },
                        isUploadingImage = state.isUploadingImage
                    )
                }

                item {
                    IngredientsSection(ingredients = recipe!!.ingredients)
                }

                if (state.steps.isNotEmpty()) {
                    item {
                        Text("Steps", style = MaterialTheme.typography.titleMedium)
                    }
                    items(state.steps, key = { "${it.recipe_id}-${it.step_number}" }) { step ->
                        CookingStepItem(step = step)
                    }
                } else {
                    item {
                        Text(
                            text = "No steps provided.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeHeader(
    imageUrl: String,
    title: String,
    summary: String,
    onAddImageClick: () -> Unit,
    isUploadingImage: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val shape = RoundedCornerShape(12.dp)
        if (imageUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Recipe image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onAddImageClick,
                enabled = !isUploadingImage
            ) {
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Uploading...")
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (imageUrl.isBlank()) "Add Image" else "Change Image")
                }
            }
        }

        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        if (summary.isNotBlank()) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IngredientsSection(ingredients: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        if (ingredients.isBlank()) {
            Text(
                text = "No ingredients listed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        val items = remember(ingredients) {
            // A simple split that handles comma/newline separated lists
            ingredients
                .split('\n', ',', ';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items.forEach { ing ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("• ", style = MaterialTheme.typography.bodyMedium)
                    Text(ing, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun CookingStepItem(step: CookingStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "Step ${step.step_number}: ${step.title}",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (step.instruction.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}