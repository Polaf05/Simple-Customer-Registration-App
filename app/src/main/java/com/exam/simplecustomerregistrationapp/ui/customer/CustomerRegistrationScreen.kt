package com.exam.simplecustomerregistrationapp.ui.customer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRegistrationScreen(
    navController: NavController,
    viewModel: CustomerViewModel = hiltViewModel(),
    customerId: String? = null,
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    context as Activity

    val scrollState = rememberScrollState()

    // --- Photo Picker Setup ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            viewModel.onFormChange(photoUri = it.toString())
        }
    }

    // --- Legacy Picker (Android 12 and below) ---
    val legacyPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            viewModel.onFormChange(photoUri = it.toString())
        }
    }

    // --- Permission Request ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            legacyPickerLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Permission denied to access photos", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(customerId) {
        if (!customerId.isNullOrEmpty()) viewModel.loadCustomer(customerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = if (customerId == null) "Register Customer" else "Edit Customer",
                    style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo Section
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .clickable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            photoPickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                        } else {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    val intent = Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                    ).apply {
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    legacyPickerLauncher.launch(intent)
                                }

                                else -> permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    },
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (formState.photoUri != null) {
                    AsyncImage(
                        model = formState.photoUri,
                        contentDescription = "Selected Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Add Photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields
            OutlinedTextField(
                value = formState.firstName,
                onValueChange = { viewModel.onFormChange(firstName = it) },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.lastName,
                onValueChange = { viewModel.onFormChange(lastName = it) },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.mobileNumber,
                onValueChange = { viewModel.onFormChange(mobileNumber = it) },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.address,
                onValueChange = { viewModel.onFormChange(address = it) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            if (formState.errorMessage != null) {
                Text(
                    text = formState.errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveCustomer(customerId)
                    if (formState.isValid) {
                        Toast.makeText(context, "Customer Saved!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = formState.isValid
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (customerId == null) "Save Customer" else "Update Customer",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}