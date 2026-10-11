package com.damdarangha.moghayerat.ui.screen.productmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damdarangha.moghayerat.MoghayeratApplication
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductManagementUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingProduct: Product? = null,
    val message: String? = null
)

class ProductManagementViewModel(
    private val app: MoghayeratApplication
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _showAddDialog = MutableStateFlow(false)
    private val _editingProduct = MutableStateFlow<Product?>(null)
    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductManagementUiState> = combine(
        app.productRepository.observeAllProducts(),
        _searchQuery,
        _showAddDialog,
        _editingProduct,
        _message
    ) { products, query, showAdd, editing, message ->
        val filtered = if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }
        ProductManagementUiState(
            products = filtered,
            searchQuery = query,
            isLoading = false,
            showAddDialog = showAdd,
            editingProduct = editing,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductManagementUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showAddDialog() {
        _showAddDialog.value = true
        _editingProduct.value = null
    }

    fun showEditDialog(product: Product) {
        _editingProduct.value = product
        _showAddDialog.value = true
    }

    fun hideDialog() {
        _showAddDialog.value = false
        _editingProduct.value = null
    }

    fun addOrUpdateProduct(
        name: String,
        group: ProductGroup,
        packagingType: PackagingType,
        boxCapacity: Int,
        displayOrder: Int
    ) {
        viewModelScope.launch {
            try {
                val editing = _editingProduct.value
                if (editing != null) {
                    val updated = editing.copy(
                        name = name,
                        group = group,
                        packagingType = packagingType,
                        boxCapacity = boxCapacity,
                        displayOrder = displayOrder
                    )
                    app.productRepository.updateProduct(updated)
                    _message.value = "محصول با موفقیت به‌روزرسانی شد"
                } else {
                    val newProduct = Product(
                        name = name,
                        group = group,
                        packagingType = packagingType,
                        boxCapacity = boxCapacity,
                        displayOrder = displayOrder
                    )
                    app.productRepository.addProduct(newProduct)
                    _message.value = "محصول جدید اضافه شد"
                }
                hideDialog()
            } catch (e: Exception) {
                _message.value = "خطا: ${e.message}"
            }
        }
    }

    fun toggleActive(product: Product) {
        viewModelScope.launch {
            if (product.isActive) {
                app.productRepository.deactivate(product.id)
                _message.value = "محصول غیرفعال شد"
            } else {
                app.productRepository.activate(product.id)
                _message.value = "محصول فعال شد"
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                app.productRepository.deactivateOrDelete(product)
                _message.value = if (product.isActive) "محصول حذف شد" else "محصول حذف شد"
            } catch (e: Exception) {
                _message.value = "خطا در حذف: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    companion object {
        fun factory(app: MoghayeratApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProductManagementViewModel(app) as T
                }
            }
    }
}
