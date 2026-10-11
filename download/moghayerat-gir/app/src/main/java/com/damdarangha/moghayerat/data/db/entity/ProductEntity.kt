package com.damdarangha.moghayerat.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup

/**
 * موجودیت محصول در پایگاه داده Room
 */
@Entity(
    tableName = "products",
    indices = [Index(value = ["name", "group"], unique = true)]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val group: String,          // نام ProductGroup
    val packagingType: String, // نام PackagingType
    val boxCapacity: Int,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        group = ProductGroup.valueOf(group),
        packagingType = PackagingType.valueOf(packagingType),
        boxCapacity = boxCapacity,
        displayOrder = displayOrder,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(product: Product): ProductEntity = ProductEntity(
            id = product.id,
            name = product.name,
            group = product.group.name,
            packagingType = product.packagingType.name,
            boxCapacity = product.boxCapacity,
            displayOrder = product.displayOrder,
            isActive = product.isActive,
            createdAt = product.createdAt
        )
    }
}
