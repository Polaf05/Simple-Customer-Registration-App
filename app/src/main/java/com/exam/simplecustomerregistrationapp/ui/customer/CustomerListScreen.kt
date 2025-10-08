package com.exam.simplecustomerregistrationapp.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import com.exam.simplecustomerregistrationapp.ui.model.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    navController: NavController, viewModel: CustomerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { navController.navigate(Screen.CustomerRegistration.route + "/customerId=") },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Customer", tint = Color.White)
        }
    }, topBar = {
        TopAppBar(
            title = { Text("Customers", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }) { padding ->
        when (uiState) {
            is CustomerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CustomerUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (uiState as CustomerUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is CustomerUiState.Success -> {
                val customers = (uiState as CustomerUiState.Success).customers

                if (customers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PersonOutline,
                                contentDescription = "No Customers",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No customers yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        items(customers) { customer ->
                            CustomerListItem(customer = customer, onEdit = {
                                navController.navigate(Screen.CustomerRegistration.route + "/customerId=${customer.id}")
                            }, onDelete = { viewModel.deleteCustomer(it) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListItem(
    customer: Customer,
    onEdit: (Customer) -> Unit,
    onDelete: (Customer) -> Unit,
    modifier: Modifier = Modifier
) {

    val maxOffset = 200f // max drag distance in pixels (≈ 1/4 of screen width)
    var offsetX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onHorizontalDrag = { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(-maxOffset, 0f)
                }, onDragEnd = {
                    offsetX = if (offsetX < -maxOffset / 2) -maxOffset else 0f
                })
            }) {
        // Background delete action layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Red)
                .padding(end = 14.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(onClick = { onDelete(customer) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }

        // Foreground Card that slides left but stays partially visible
        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), 0) }
                .fillMaxWidth()
                .clickable { onEdit(customer) }, shape = RoundedCornerShape(0.dp), // full edge card
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile or Initials
                if (!customer.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = customer.photoUri,
                        contentDescription = "Customer Photo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val initials = remember(customer) {
                        val first = customer.firstName.firstOrNull()?.uppercaseChar() ?: '?'
                        val last = customer.lastName.firstOrNull()?.uppercaseChar() ?: '?'
                        "$first$last"
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials, style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${customer.firstName} ${customer.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = customer.mobileNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = customer.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
