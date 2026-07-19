package com.didan.rapi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.didan.rapi.data.CategoryEntity
import com.didan.rapi.data.Priority
import com.didan.rapi.data.SubtaskEntity
import com.didan.rapi.data.TaskEntity
import com.didan.rapi.data.TaskWithDetails
import com.didan.rapi.data.TodoDatabase
import com.didan.rapi.reminder.ReminderWorker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val label: String) { SEMUA("Semua"), HARI_INI("Hari ini"), MENDATANG("Mendatang") }

data class DraftSubtask(val id: Long = 0, val title: String, val completed: Boolean = false)

data class TaskDraft(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val dueAt: Long? = null,
    val reminderAt: Long? = null,
    val priority: Priority = Priority.SEDANG,
    val categoryId: Long? = null,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val subtasks: List<DraftSubtask> = emptyList(),
) {
    companion object {
        fun from(item: TaskWithDetails) = TaskDraft(
            id = item.task.id,
            title = item.task.title,
            description = item.task.description,
            dueAt = item.task.dueAt,
            reminderAt = item.task.reminderAt,
            priority = item.task.priority,
            categoryId = item.task.categoryId,
            completed = item.task.completed,
            createdAt = item.task.createdAt,
            subtasks = item.subtasks.sortedBy { it.position }
                .map { DraftSubtask(it.id, it.title, it.completed) },
        )
    }
}

data class TodoUiState(
    val allTasks: List<TaskWithDetails> = emptyList(),
    val visibleTasks: List<TaskWithDetails> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.SEMUA,
) {
    val completedCount get() = allTasks.count { it.task.completed }
    val highPriorityCount get() = allTasks.count { !it.task.completed && it.task.priority == Priority.TINGGI }
}

fun filterTasks(
    tasks: List<TaskWithDetails>,
    filter: TaskFilter,
    today: LocalDate = LocalDate.now(),
    zone: ZoneId = ZoneId.systemDefault(),
): List<TaskWithDetails> = tasks.filter { item ->
    val date = item.task.dueAt?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
    when (filter) {
        TaskFilter.SEMUA -> true
        TaskFilter.HARI_INI -> date == today
        TaskFilter.MENDATANG -> date?.isAfter(today) == true
    }
}

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TodoDatabase.get(application).todoDao()
    private val filter = MutableStateFlow(TaskFilter.SEMUA)

    val uiState = combine(dao.observeTasks(), dao.observeCategories(), filter) { tasks, categories, activeFilter ->
        TodoUiState(tasks, filterTasks(tasks, activeFilter), categories, activeFilter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodoUiState())

    init {
        viewModelScope.launch {
            dao.insertCategories(listOf(
                CategoryEntity(name = "Kerja"),
                CategoryEntity(name = "Pribadi"),
                CategoryEntity(name = "Belanja"),
            ))
        }
    }

    fun setFilter(value: TaskFilter) { filter.value = value }

    fun quickAdd(title: String) {
        if (title.isBlank()) return
        save(TaskDraft(title = title.trim(), categoryId = uiState.value.categories.firstOrNull()?.id))
    }

    fun save(draft: TaskDraft) = viewModelScope.launch {
        if (draft.title.isBlank()) return@launch
        val task = TaskEntity(
            id = draft.id,
            title = draft.title.trim(),
            description = draft.description.trim(),
            dueAt = draft.dueAt,
            reminderAt = draft.reminderAt,
            priority = draft.priority,
            categoryId = draft.categoryId,
            completed = draft.completed,
            createdAt = draft.createdAt,
        )
        val taskId = dao.saveTask(task, draft.subtasks.filter { it.title.isNotBlank() }.mapIndexed { index, item ->
            SubtaskEntity(item.id, draft.id, item.title.trim(), item.completed, index)
        })
        if (draft.completed) ReminderWorker.cancel(getApplication(), taskId)
        else ReminderWorker.schedule(getApplication(), taskId, task.title, task.reminderAt)
    }

    fun delete(item: TaskWithDetails) = viewModelScope.launch {
        dao.deleteTask(item.task)
        ReminderWorker.cancel(getApplication(), item.task.id)
    }

    fun toggleTask(item: TaskWithDetails) = viewModelScope.launch {
        val updated = item.task.copy(completed = !item.task.completed)
        dao.updateTask(updated)
        if (updated.completed) ReminderWorker.cancel(getApplication(), updated.id)
        else ReminderWorker.schedule(getApplication(), updated.id, updated.title, updated.reminderAt)
    }

    fun toggleSubtask(item: SubtaskEntity) = viewModelScope.launch {
        dao.updateSubtask(item.copy(completed = !item.completed))
    }

    fun addCategory(name: String) = viewModelScope.launch {
        val clean = name.trim()
        if (clean.isNotEmpty()) dao.insertCategory(CategoryEntity(name = clean))
    }
}
