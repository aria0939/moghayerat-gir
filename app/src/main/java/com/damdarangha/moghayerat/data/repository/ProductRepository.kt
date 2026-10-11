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
            // ===================== استریل (ظرفیت باکس ۱۰ عددی) =====================
            Product(
                name = "شیر استریل پرچرب ۱۰۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 10,
                displayOrder = 1
            ),
            Product(
                name = "شیر استریل کم چرب ۱۰۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 10,
                displayOrder = 2
            ),
            Product(
                name = "شیر کاکائو استریل ۱۰۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 10,
                displayOrder = 3
            ),
            Product(
                name = "شیر موز استریل ۱۰۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.STERILE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STERILE,
                boxCapacity = 10,
                displayOrder = 4
            ),

            // ===================== شیر (باکس ۶ و ۱۲ عددی) =====================
            Product(
                name = "شیر کم چرب ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 1
            ),
            Product(
                name = "شیر پرچرب ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 2
            ),
            Product(
                name = "شیر لاکتوز ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 3
            ),
            Product(
                name = "شیر کاکائو ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 4
            ),
            Product(
                name = "شیر ESL پرچرب ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 5
            ),
            Product(
                name = "شیر ESL کم چرب ۹۰۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 6,
                displayOrder = 6
            ),
            Product(
                name = "شیر کاکائو ۲۳۰cc",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 12,
                displayOrder = 7
            ),
            Product(
                name = "شیر کم چرب کوچیک",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.MILK,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 12,
                displayOrder = 8
            ),

            // ===================== ماست باکسی (باکس ۴ عددی) =====================
            Product(
                name = "ماست ۲۰۰۰ کم چرب ساده",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.YOGHURT_BOX,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 1
            ),
            Product(
                name = "ماست ۲۰۰۰ پر چرب ساده",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.YOGHURT_BOX,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 2
            ),
            Product(
                name = "ماست ۲۰۰۰ پرچرب پلاس +",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.YOGHURT_BOX,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 3
            ),
            Product(
                name = "ماست ۲۰۰۰ کم چرب پلاس +",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.YOGHURT_BOX,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.TUB,
                boxCapacity = 4,
                displayOrder = 4
            ),

            // ===================== خرما (باکس رند) =====================
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
            ),

            // ===================== دونات و کیک =====================
            Product(
                name = "دونات رضوی",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.DONUT_CAKE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 20,
                displayOrder = 1
            ),
            Product(
                name = "کیک دو قلو",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.DONUT_CAKE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 20,
                displayOrder = 2
            ),
            Product(
                name = "پیراشکی",
                group = com.damdarangha.moghayerat.domain.model.ProductGroup.DONUT_CAKE,
                packagingType = com.damdarangha.moghayerat.domain.model.PackagingType.STANDARD_BOX,
                boxCapacity = 24,
                displayOrder = 3
            )
        )
    }
}
