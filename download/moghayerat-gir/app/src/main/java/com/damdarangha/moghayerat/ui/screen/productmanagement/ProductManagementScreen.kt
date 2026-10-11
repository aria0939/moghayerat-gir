package com.damdarangha.moghayerat.ui.screen.productmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistantChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    onBack: () -> Unit,
    viewModel: ProductManagementViewModel = viewModel(
        factory = ProductManagementViewModel.factory(MoghayeratApplication.instance)
    )
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مدیریت محصولات") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("افزودن محصول") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("جست‌وجوی محصول...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.products, key = { it.id }) { product ->
                    ProductItemCard(
                        product = product,
                        onEdit = { viewModel.showEditDialog(product) },
                        onToggleActive = { viewModel.toggleActive(product) },
                        onDelete = { showDeleteDialog = product }
                    )
                }
            }
        }
    }

    // دیالوگ افزودن/ویرایش
    if (state.showAddDialog) {
        ProductFormDialog(
            product = state.editingProduct,
            onDismiss = { viewModel.hideDialog() },
            onConfirm = { name, group, packagingType, boxCapacity, displayOrder ->
                viewModel.addOrUpdateProduct(name, group, packagingType, boxCapacity, displayOrder)
            }
        )
    }

    // دیالوگ تأیید حذف
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("حذف محصول") },
            text = {
                Text(
                    if (product.isActive)
                        "آیا از حذف «${product.name}» مطمئن هستید؟ اگر این محصول آمار ثبت‌شده داشته باشد، غیرفعال می‌شود."
                    else "آیا از حذف «${product.name}» مطمئن هستید؟"
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteProduct(product)
                    showDeleteDialog = null
                }) { Text("حذف") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun ProductItemCard(
    product: Product,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isActive)
                MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (product.isActive)
                            MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Row {
                        AssistantChip(onClick = {}, label = { Text(product.group.displayName) })
                        Spacer(modifier = Modifier.width(4.dp))
                        AssistantChip(
                            onClick = {},
                            label = {
                                Text(if (product.isRoundBox) "باکس رند"
                                else "باکس ${product.boxCapacity.toPersian()}")
                            }
                        )
                        if (!product.isActive) {
                            Spacer(modifier = Modifier.width(4.dp))
                            AssistantChip(onClick = {}, label = { Text("غیرفعال") })
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش")
                    }
                    IconButton(onClick = onToggleActive) {
                        Icon(
                            imageVector = if (product.isActive) Icons.Default.Block
                            else Icons.Default.Check,
                            contentDescription = if (product.isActive) "غیرفعال کردن"
                            else "فعال کردن"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (String, ProductGroup, PackagingType, Int, Int) -> Unit
) {
    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    var selectedGroup by remember(product) {
        mutableStateOf(product?.group ?: ProductGroup.STERILE)
    }
    var selectedPackaging by remember(product) {
        mutableStateOf(product?.packagingType ?: PackagingType.STERILE)
    }
    var boxCapacity by remember(product) {
        mutableStateOf(product?.boxCapacity?.toString() ?: "27")
    }
    var displayOrder by remember(product) {
        mutableStateOf(product?.displayOrder?.toString() ?: "1")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "افزودن محصول جدید" else "ویرایش محصول") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام محصول") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text("گروه محصول", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ProductGroup.entries.forEach { group ->
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = {
                                selectedGroup = group
                                // تنظیم خودکار نوع بسته‌بندی و ظرفیت
                                when (group) {
                                    ProductGroup.STERILE -> {
                                        selectedPackaging = PackagingType.STERILE
                                        if (boxCapacity.toIntOrNull() == null ||
                                            boxCapacity.toInt() !in 1..1000) {
                                            boxCapacity = "27"
                                        }
                                    }
                                    ProductGroup.TUB -> {
                                        selectedPackaging = PackagingType.TUB
                                        boxCapacity = "4"
                                    }
                                    ProductGroup.DATE -> {
                                        selectedPackaging = PackagingType.ROUND_BOX
                                        boxCapacity = "1"
                                    }
                                }
                            },
                            label = { Text(group.displayName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text("نوع بسته‌بندی", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PackagingType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedPackaging == type,
                            onClick = { selectedPackaging = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = boxCapacity,
                    onValueChange = { boxCapacity = it.filter { c -> c.isDigit() } },
                    label = { Text("ظرفیت باکس") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = selectedPackaging != PackagingType.ROUND_BOX,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = displayOrder,
                    onValueChange = { displayOrder = it.filter { c -> c.isDigit() } },
                    label = { Text("ترتیب نمایش") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name.trim(),
                            selectedGroup,
                            selectedPackaging,
                            boxCapacity.toIntOrNull() ?: 1,
                            displayOrder.toIntOrNull() ?: 0
                        )
                    }
                }
            ) { Text("ذخیره") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

private fun Int.toPersian(): String {
    val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { c ->
        if (c.isDigit()) persianDigits[c - '0'] else c
    }.joinToString("")
}
