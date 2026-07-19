package com.didan.rapi

import com.didan.rapi.data.Priority
import com.didan.rapi.data.TaskEntity
import com.didan.rapi.data.TaskWithDetails
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskFilterTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val today = LocalDate.of(2026, 7, 19)

    private fun task(id: Long, date: LocalDate?) = TaskWithDetails(
        task = TaskEntity(
            id = id,
            title = "Task $id",
            dueAt = date?.atTime(23, 59)?.atZone(zone)?.toInstant()?.toEpochMilli(),
            priority = Priority.SEDANG,
        ),
        category = null,
        subtasks = emptyList(),
    )

    @Test
    fun filtersTodayAndUpcomingWithoutIncludingUndatedTasks() {
        val tasks = listOf(task(1, today), task(2, today.plusDays(1)), task(3, null))

        assertEquals(listOf(1L), filterTasks(tasks, TaskFilter.HARI_INI, today, zone).map { it.task.id })
        assertEquals(listOf(2L), filterTasks(tasks, TaskFilter.MENDATANG, today, zone).map { it.task.id })
        assertEquals(3, filterTasks(tasks, TaskFilter.SEMUA, today, zone).size)
    }
}
