package com.damdarangha.moghayerat.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup

/**
 * موجودیت محصول در پایگاه داده Room
 *
 * هر محصول به یک گردش کار (Workflow) متصل است.
 * محصولات گردش کارهای مختلف مخلوط نمی‌شوند.
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = WorkflowEntity::class,
            parentColumns = ["id"],
            childColumns = ["workflowId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["workflowId", "name"], unique = true),
        Index(value = ["workflowId"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val workflowId: Long,           // شناسه گردش کار
    val name: String,
    val group: String,              // نام ProductGroup (برای سازگاری با نسخه قبلی)
    val packagingType: String,
    val boxCapacity: Int,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toDomain(): Product = Product(
        id = id,
        workflowId = workflowId,
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
            workflowId = product.workflowId,
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
