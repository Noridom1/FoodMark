package com.example.foodmark.foodtour.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults.properties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.cs426_mobileproject.foodmap.ui.MapMarkerUtils
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTourMapScreen(
    viewModel: FoodTourViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val location by viewModel.location.collectAsState() // observe location
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            viewModel.loadCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.loadCurrentLocation()
        }
    }

    // Build cameraPositionState that updates when location changes
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.lat, it.lng),
                15f
            )
        }
    }

    // state to toggle description visibility
    var showDescription by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Starting Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.routeRecommendation == null) {
                // Show Confirm button before recommendation
                ExtendedFloatingActionButton(
                    onClick = {
                        val target = cameraPositionState.position.target
                        viewModel.getFoodTour(target.latitude, target.longitude)
                    },
                    text = { Text("Confirm Location") },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) }
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.clearRecommendations() },
                    text = { Text("Choose again") },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = permissionGranted,
                    mapType = MapType.NORMAL,
                    mapStyleOptions = MapStyleOptions(
                        """
                        [
                          {
                            "featureType": "poi",
                            "stylers": [ { "visibility": "off" } ]
                          },
                          {
                            "featureType": "transit",
                            "stylers": [ { "visibility": "off" } ]
                          },
                          {
                            "featureType": "road",
                            "elementType": "labels.icon",
                            "stylers": [ { "visibility": "off" } ]
                          }
                        ]
                        """.trimIndent()
                    )
                )
            ) {
                // Show recommended stores when available
                uiState.routeRecommendation?.route?.forEach { store ->
                    val icon by produceState<BitmapDescriptor?>(initialValue = null, store.id) {
                        value = MapMarkerUtils.getFoodMarkerBitmap(
                            context,
                            "",
                            store.name,
                            store.id
                        )
                    }

                    Marker(
                        state = MarkerState(LatLng(store.lat, store.lng)),
                        title = store.name,
                        icon = icon,
                        anchor = Offset(0.5f, 1f)
                    )
                }

                // Polyline (if we have a path)
                if (uiState.routePath.isNotEmpty()) {
                    Polyline(
                        points = uiState.routePath.map { LatLng(it.lat, it.lng) },
                        color = Color.Blue,
                        width = 8f
                    )
                    val startNode = uiState.routePath.first()
                    Marker(
                        state = MarkerState(LatLng(startNode.lat, startNode.lng)),
                        title = "Start Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        anchor = Offset(0.5f, 1f)
                    )
                }
            }

            // Only show center pin when selecting location
            if (uiState.routeRecommendation == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Loading overlay when isRecommending
            if (uiState.isRecommending) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // --- Floating description card ---
            uiState.routeRecommendation?.description?.let { desc ->
                if (showDescription) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 250.dp), // limit card height
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // scrollable description
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Food Tour Description",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            HorizontalDivider()

                            // hide button pinned at bottom
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                TextButton(onClick = { showDescription = false }) {
                                    Text("Hide")
                                }
                            }
                        }
                    }
                } else {
                    FloatingActionButton(
                        onClick = { showDescription = true },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Show Description")
                    }
                }
            }
        }
    }
}