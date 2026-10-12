package com.damdarangha.moghayerat.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.damdarangha.moghayerat.data.db.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY displayOrder ASC, name ASC")
    fun observeActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY displayOrder ASC, name ASC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE workflowId = :workflowId AND isActive = 1 ORDER BY displayOrder ASC, name ASC")
    fun observeActiveProductsByWorkflow(workflowId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE workflowId = :workflowId ORDER BY displayOrder ASC, name ASC")
    fun observeAllProductsByWorkflow(workflowId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE `group` = :group AND isActive = 1 ORDER BY displayOrder ASC, name ASC")
    fun observeActiveProductsByGroup(group: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY displayOrder ASC, name ASC")
    fun search(query: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1 AND workflowId = :workflowId")
    suspend fun countActiveByWorkflow(workflowId: Long): Int

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1 AND `group` = :group")
    suspend fun countActiveByGroup(group: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("UPDATE products SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Query("UPDATE products SET displayOrder = :order WHERE id = :id")
    suspend fun updateDisplayOrder(id: Long, order: Int)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM daily_stats WHERE productId = :productId")
    suspend fun countStatsForProduct(productId: Long): Int

    @Query("SELECT * FROM products ORDER BY displayOrder ASC, name ASC")
    suspend fun getAllProducts(): List<ProductEntity>
}
