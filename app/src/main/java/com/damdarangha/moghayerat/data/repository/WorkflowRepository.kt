package com.damdarangha.moghayerat.data.repository

import com.damdarangha.moghayerat.data.db.dao.WorkflowDao
import com.damdarangha.moghayerat.data.db.entity.WorkflowEntity
import com.damdarangha.moghayerat.domain.model.Workflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository برای مدیریت گردش کارها.
 */
class WorkflowRepository(private val workflowDao: WorkflowDao) {

    fun observeActiveWorkflows(): Flow<List<Workflow>> =
        workflowDao.observeActiveWorkflows().map { it.map { e -> e.toDomain() } }

    fun observeAllWorkflows(): Flow<List<Workflow>> =
        workflowDao.observeAllWorkflows().map { it.map { e -> e.toDomain() } }

    fun observeById(id: Long): Flow<Workflow?> =
        workflowDao.observeById(id).map { it?.toDomain() }

    suspend fun getById(id: Long): Workflow? = workflowDao.getById(id)?.toDomain()

    suspend fun count(): Int = workflowDao.count()

    suspend fun addWorkflow(workflow: Workflow): Long {
        return workflowDao.insert(WorkflowEntity.fromDomain(workflow))
    }

    suspend fun updateWorkflow(workflow: Workflow) {
        workflowDao.update(WorkflowEntity.fromDomain(workflow))
    }

    suspend fun deactivate(id: Long) = workflowDao.deactivate(id)
    suspend fun activate(id: Long) = workflowDao.activate(id)

    /**
     * مقداردهی اولیه گردش کارهای پیش‌فرض.
     * اگر هیچ گردش کاری وجود نداشته باشد، دو گردش کار اولیه ایجاد می‌شود:
     * 1. استریل
     * 2. دبه و خرما
     *
     * همچنین محصولات هر گردش کار هم ایجاد می‌شوند.
     */
    suspend fun seedDefaultWorkflowsIfEmpty(
        productRepository: ProductRepository
    ) {
        if (workflowDao.count() > 0) return

        // گردش کار استریل
        val sterileWorkflowId = workflowDao.insert(
            WorkflowEntity(
                name = "استریل",
                displayOrder = 1,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )
        )
        productRepository.seedSterileProducts(sterileWorkflowId)

        // گردش کار دبه و خرما
        val tubDateWorkflowId = workflowDao.insert(
            WorkflowEntity(
                name = "دبه و خرما",
                displayOrder = 2,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )
        )
        productRepository.seedTubAndDateProducts(tubDateWorkflowId)

        // گردش کار ماست ۷۵۰
        val yoghurt750WorkflowId = workflowDao.insert(
            WorkflowEntity(
                name = "ماست ۷۵۰",
                displayOrder = 3,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )
        )
        productRepository.seedYoghurt750Products(yoghurt750WorkflowId)
    }
}
