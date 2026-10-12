package com.damdarangha.moghayerat.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.damdarangha.moghayerat.domain.model.Workflow

/**
 * موجودیت گردش کار در پایگاه داده
 */
@Entity(
    tableName = "workflows",
    indices = [Index(value = ["name"], unique = true)]
)
data class WorkflowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toDomain(): Workflow = Workflow(
        id = id,
        name = name,
        displayOrder = displayOrder,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(workflow: Workflow): WorkflowEntity = WorkflowEntity(
            id = workflow.id,
            name = workflow.name,
            displayOrder = workflow.displayOrder,
            isActive = workflow.isActive,
            createdAt = workflow.createdAt
        )
    }
}
