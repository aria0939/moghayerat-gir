package com.damdarangha.moghayerat.data.repository

import com.damdarangha.moghayerat.data.db.dao.ProductDao
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import com.damdarangha.moghayerat.domain.model.PackagingType
import com.damdarangha.moghayerat.domain.model.Product
import com.damdarangha.moghayerat.domain.model.ProductGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository برای محصولات.
 */
class ProductRepository(private val productDao: ProductDao) {

    fun observeActiveProducts(): Flow<List<Product>> =
        productDao.observeActiveProducts().map { it.map { e -> e.toDomain() } }

    fun observeAllProducts(): Flow<List<Product>> =
        productDao.observeAllProducts().map { it.map { e -> e.toDomain() } }

    fun observeActiveProductsByWorkflow(workflowId: Long): Flow<List<Product>> =
        productDao.observeActiveProductsByWorkflow(workflowId).map { it.map { e -> e.toDomain() } }

    fun observeAllProductsByWorkflow(workflowId: Long): Flow<List<Product>> =
        productDao.observeAllProductsByWorkflow(workflowId).map { it.map { e -> e.toDomain() } }

    fun observeActiveProductsByGroup(group: String): Flow<List<Product>> =
        productDao.observeActiveProductsByGroup(group).map { it.map { e -> e.toDomain() } }

    fun search(query: String): Flow<List<Product>> =
        productDao.search(query).map { it.map { e -> e.toDomain() } }

    suspend fun getById(id: Long): Product? = productDao.getById(id)?.toDomain()

    fun observeById(id: Long): Flow<Product?> =
        productDao.observeById(id).map { it?.toDomain() }

    suspend fun countActiveByWorkflow(workflowId: Long): Int =
        productDao.countActiveByWorkflow(workflowId)

    suspend fun countActiveByGroup(group: String): Int =
        productDao.countActiveByGroup(group)

    suspend fun addProduct(product: Product): Long {
        return productDao.insert(ProductEntity.fromDomain(product))
    }

    suspend fun updateProduct(product: Product) {
        productDao.update(ProductEntity.fromDomain(product))
    }

    suspend fun deactivateOrDelete(product: Product) {
        val statsCount = productDao.countStatsForProduct(product.id)
        if (statsCount == 0) {
            productDao.delete(ProductEntity.fromDomain(product))
        } else {
            productDao.deactivate(product.id)
        }
    }

    suspend fun activate(id: Long) = productDao.activate(id)
    suspend fun deactivate(id: Long) = productDao.deactivate(id)
    suspend fun updateDisplayOrder(id: Long, order: Int) =
        productDao.updateDisplayOrder(id, order)

    /**
     * محصولات نمونه گردش کار استریل.
     */
    suspend fun seedSterileProducts(workflowId: Long) {
        val products = listOf(
            Product(workflowId = workflowId, name = "شیر استریل پرچرب ۱۰۰۰cc",
                group = ProductGroup.STERILE, packagingType = PackagingType.STERILE,
                boxCapacity = 10, displayOrder = 1),
            Product(workflowId = workflowId, name = "شیر استریل کم چرب ۱۰۰۰cc",
                group = ProductGroup.STERILE, packagingType = PackagingType.STERILE,
                boxCapacity = 10, displayOrder = 2),
            Product(workflowId = workflowId, name = "شیر کاکائو استریل ۱۰۰۰cc",
                group = ProductGroup.STERILE, packagingType = PackagingType.STERILE,
                boxCapacity = 10, displayOrder = 3),
            Product(workflowId = workflowId, name = "شیر موز استریل ۱۰۰۰cc",
                group = ProductGroup.STERILE, packagingType = PackagingType.STERILE,
                boxCapacity = 10, displayOrder = 4)
        )
        products.forEach { productDao.insert(ProductEntity.fromDomain(it)) }
    }

    /**
     * محصولات نمونه گردش کار «دبه و خرما».
     * ۱۱ محصول با ترتیب دقیق درخواستی.
     *
     * نکته: سه محصول خرمای ۶۰۰، ۶۵۰ و ۶۵۰ پروتئین با ظرفیت ۱ (نامشخص)
     * ایجاد می‌شوند. این ظرفیت باید از بخش تنظیمات تعیین شود.
     */
    suspend fun seedTubAndDateProducts(workflowId: Long) {
        val products = listOf(
            // ۴ محصول ماست باکس ۴ عددی
            Product(workflowId = workflowId, name = "ماست پرچرب ۲۵۰۰ پلاس",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 1),
            Product(workflowId = workflowId, name = "ماست کم چرب ۲۵۰۰ پلاس",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 2),
            Product(workflowId = workflowId, name = "ماست پرچرب ۲۵۰۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 3),
            Product(workflowId = workflowId, name = "ماست کم چرب ۲۵۰۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 4),
            // ماست سنتی ۱۱۰ گرمی باکس ۲۴ عددی
            Product(workflowId = workflowId, name = "ماست سنتی ۱۱۰ گرمی",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 24, displayOrder = 5),
            // ۲ محصول پروتئینی ۱۰۰۰ باکس ۶ عددی
            Product(workflowId = workflowId, name = "ماست پر چرب پروتین ۱۰۰۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 6, displayOrder = 6),
            Product(workflowId = workflowId, name = "ماست کم چرب پروتین ۱۰۰۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 6, displayOrder = 7),
            // خرما ۵۵۰ با بسته‌بندی رند
            Product(workflowId = workflowId, name = "خرما ۵۵۰",
                group = ProductGroup.DATE, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 8),
            // ۳ محصول خرما با ظرفیت نامشخص (موقتاً ۱، باید از تنظیمات تعیین شود)
            Product(workflowId = workflowId, name = "خرما ۶۰۰",
                group = ProductGroup.DATE, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 9),
            Product(workflowId = workflowId, name = "خرما ۶۵۰",
                group = ProductGroup.DATE, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 10),
            Product(workflowId = workflowId, name = "خرما ۶۵۰ پروتین",
                group = ProductGroup.DATE, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 11)
        )
        products.forEach { productDao.insert(ProductEntity.fromDomain(it)) }
    }

    /**
     * محصولات گردش کار ماست ۷۵۰.
     * ۱۳ محصول با ترتیب دقیق درخواستی.
     */
    suspend fun seedYoghurt750Products(workflowId: Long) {
        val products = listOf(
            Product(workflowId = workflowId, name = "لیوان کم",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 12, displayOrder = 1),
            Product(workflowId = workflowId, name = "لیوان پر",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 12, displayOrder = 2),
            Product(workflowId = workflowId, name = "پرچرب برو ۷۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 3),
            Product(workflowId = workflowId, name = "کم چرب برو ۷۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 4),
            Product(workflowId = workflowId, name = "همزده ۴۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 12, displayOrder = 5),
            Product(workflowId = workflowId, name = "پرچرب ۷۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 6),
            Product(workflowId = workflowId, name = "کم چرب ۷۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 4, displayOrder = 7),
            Product(workflowId = workflowId, name = "همزده ۵۰۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 12, displayOrder = 8),
            Product(workflowId = workflowId, name = "ماست همزده ۲۵۰",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 9),
            Product(workflowId = workflowId, name = "ماست لیوانی ص",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 10),
            Product(workflowId = workflowId, name = "همزده ۴۵۰ سحر",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.TUB,
                boxCapacity = 12, displayOrder = 11),
            Product(workflowId = workflowId, name = "کم چرب ۷۵۰ ص",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 12),
            Product(workflowId = workflowId, name = "پرچرب ۷۵۰ ص",
                group = ProductGroup.YOGHURT_BOX, packagingType = PackagingType.ROUND_BOX,
                boxCapacity = 1, displayOrder = 13)
        )
        products.forEach { productDao.insert(ProductEntity.fromDomain(it)) }
    }
}
