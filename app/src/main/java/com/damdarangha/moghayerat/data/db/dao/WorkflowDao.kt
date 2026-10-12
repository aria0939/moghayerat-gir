package com.damdarangha.moghayerat.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.damdarangha.moghayerat.data.db.entity.WorkflowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkflowDao {

    @Query("SELECT * FROM workflows WHERE isActive = 1 ORDER BY displayOrder ASC, name ASC")
    fun observeActiveWorkflows(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows ORDER BY displayOrder ASC, name ASC")
    fun observeAllWorkflows(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE id = :id")
    suspend fun getById(id: Long): WorkflowEntity?

    @Query("SELECT * FROM workflows WHERE id = :id")
    fun observeById(id: Long): Flow<WorkflowEntity?>

    @Query("SELECT * FROM workflows WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): WorkflowEntity?

    @Query("SELECT COUNT(*) FROM workflows")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workflow: WorkflowEntity): Long

    @Update
    suspend fun update(workflow: WorkflowEntity)

    @Query("UPDATE workflows SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("UPDATE workflows SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Delete
    suspend fun delete(workflow: WorkflowEntity)
}
