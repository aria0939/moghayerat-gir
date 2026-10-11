package com.damdarangha.moghayerat.data.repository

import com.damdarangha.moghayerat.data.db.dao.ProductDao
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import com.damdarangha.moghayerat.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository برای محصولات.
 * مسئولیت: مدیریت داده‌های محصولات و اعمال قوانین کسب‌وکار.
 */
class ProductRepository(private val productDao: ProductDao) {

    fun observeActiveProducts(): Flow<List<Product>> =
        productDao.observeActiveProducts().map { entities ->
            entities.map { it.toDomain() }
        }

    fun observeAllProducts(): Flow<List<Product>> =
        productDao.observeAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }

    fun observeActiveProductsByGroup(group: String): Flow<List<Product>> =
        productDao.observeActiveProductsByGroup(group).map { entities ->
            entities.map { it.toDomain() }
        }

    fun search(query: String): Flow<List<Product>> =
        productDao.search(query).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getById(id: Long): Product? = productDao.getById(id)?.toDomain()

    fun observeById(id: Long): Flow<Product?> =
        productDao.observeById(id).map { it?.toDomain() }

    suspend fun countActiveByGroup(group: String): Int =
        productDao.countActiveByGroup(group)

    suspend fun addProduct(product: Product): Long {
        return productDao.insert(ProductEntity.fromDomain(product))
    }

    suspend fun updateProduct(product: Product) {
        productDao.update(ProductEntity.fromDomain(product))
    }

    /**
     * غیرفعال‌سازی محصول به‌جای حذف.
     * فقط در صورتی حذف واقعی انجام می‌شود که هیچ آماری ثبت نشده باشد.
     */
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
     * مقداردهی اولیه محصولات پیش‌فرض اگر پایگاه داده خالی است.
     */
    suspend fun seedDefaultProductsIfEmpty() {
        val defaults = getDefaultProducts()
        // فقط در صورتی اضافه می‌کنیم که محصولاتی با همین نام وجود نداشته باشند
        defaults.forEach { product ->
            productDao.insert(ProductEntity.fromDomain(product))
        }
    }

    private fun getDefaultProducts(): List<Product> {
        return listOf(
            // استریل - ظرفیت باکس ۲۷
            Product(
                name = "شیر استریل ۲۰۰",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 1
            ),
            Product(
                name = "شیر کاکائو استریل ۲۰۰",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 2
            ),
            Product(
                name = "خامه استریل ۲۰۰",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 3
            ),
            Product(
                name = "شیر قهوه استریل ۲۰۰",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 4
            ),
            Product(
                name = "شیر موز استریل ۲۰۰",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 5
            ),
            Product(
                name = "شیر قهوه ۱۲۵",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 6
            ),
            Product(
                name = "شیر کاکائو ۱۲۵",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 7
            ),
            Product(
                name = "شیر کم چرب ۱۲۵",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 8
            ),
            Product(
                name = "شیر نیم چرب ۱۲۵",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 27,
                displayOrder = 9
            ),
            // دبه - ظرفیت باکس ۴
            Product(
                name = "ماست ۹۰۰ گرمی",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.TUB,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 1
            ),
            Product(
                name = "ماست ۱۸۰۰ گرمی",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.TUB,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 2
            ),
            // خرما - باکس رند
            Product(
                name = "خرما کبک",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.DATE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.ROUND_BOX,
                boxCapacity = 1,
                displayOrder = 1
            ),
            Product(
                name = "خرما زاهد",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.DATE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.ROUND_BOX,
                boxCapacity = 1,
                displayOrder = 2
            )
        )
    }
}
