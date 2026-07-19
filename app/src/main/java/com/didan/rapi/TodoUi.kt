@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.didan.rapi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.didan.rapi.data.CategoryEntity
import com.didan.rapi.data.Priority
import com.didan.rapi.data.SubtaskEntity
import com.didan.rapi.data.TaskWithDetails
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Indigo = Color(0xFF5146E5)
private val Purple = Color(0xFF7C4DFF)
private val Coral = Color(0xFFB3261E)
private val Amber = Color(0xFF825500)
private val Green = Color(0xFF14734E)

private val LightColors = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    secondary = Purple,
    background = Color(0xFFF8F7FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDEAF5),
    onSurface = Color(0xFF24212E),
    onSurfaceVariant = Color(0xFF6F6B7A),
    outline = Color(0xFFD6D1E0),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB7AEFF),
    onPrimary = Color(0xFF25168E),
    secondary = Color(0xFFD0BCFF),
    background = Color(0xFF15131B),
    surface = Color(0xFF201D29),
    surfaceVariant = Color(0xFF302C3B),
    onSurface = Color(0xFFF0ECF6),
    onSurfaceVariant = Color(0xFFC9C3D2),
    outline = Color(0xFF4C4658),
    error = Color(0xFFFFB4AB),
)

@Composable
fun RapiTheme(darkMode: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkMode) DarkColors else LightColors,
        typography = MaterialTheme.typography.copy(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        ),
        content = content,
    )
}

@Composable
fun TodoApp(
    requestedTaskId: Long,
    onRequestHandled: () -> Unit,
    darkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    vm: TodoViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var editorItem by remember { mutableStateOf<TaskWithDetails?>(null) }
    var showNewEditor by remember { mutableStateOf(false) }
    var quickTitle by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(requestedTaskId, state.allTasks) {
        if (requestedTaskId != 0L) {
            state.allTasks.firstOrNull { it.task.id == requestedTaskId }?.let {
                editorItem = it
                onRequestHandled()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { contentPadding ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding(),
                    bottom = 96.dp,
                ),
            ) {
                item {
                    Header(
                        state = state,
                        darkMode = darkMode,
                        onToggleDarkMode = onToggleDarkMode,
                    )
                }
                item {
                    QuickAdd(
                        title = quickTitle,
                        onTitleChange = { quickTitle = it },
                        onAdd = {
                            if (quickTitle.isNotBlank()) {
                                vm.quickAdd(quickTitle)
                                quickTitle = ""
                            }
                        },
                        onOpenEditor = { showNewEditor = true },
                    )
                }
                item {
                    FilterBar(state.filter, vm::setFilter)
                }
                if (state.visibleTasks.isEmpty()) {
                    item { EmptyState(state.filter, onAdd = { showNewEditor = true }) }
                } else {
                    items(state.visibleTasks, key = { it.task.id }) { item ->
                        TaskCard(
                            item = item,
                            onToggle = { vm.toggleTask(item) },
                            onToggleSubtask = vm::toggleSubtask,
                            onEdit = { editorItem = item },
                        )
                    }
                }
            }
            Spacer(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(Brush.linearGradient(listOf(Indigo, Purple))),
            )
            Button(
                onClick = { showNewEditor = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tugas baru", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showNewEditor) {
        TaskEditor(
            initial = TaskDraft(categoryId = state.categories.firstOrNull()?.id),
            categories = state.categories,
            onAddCategory = vm::addCategory,
            onDismiss = { showNewEditor = false },
            onSave = { vm.save(it); showNewEditor = false },
        )
    }
    editorItem?.let { item ->
        TaskEditor(
            initial = TaskDraft.from(item),
            categories = state.categories,
            onAddCategory = vm::addCategory,
            onDismiss = { editorItem = null },
            onSave = { vm.save(it); editorItem = null },
            onDelete = { vm.delete(item); editorItem = null },
        )
    }
}

@Composable
private fun Header(state: TodoUiState, darkMode: Boolean, onToggleDarkMode: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Indigo, Purple)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Rapi", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("id", "ID"))),
                    color = Color.White.copy(alpha = .9f),
                )
            }
            IconButton(
                onClick = onToggleDarkMode,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = .16f)),
            ) {
                Text(if (darkMode) "☀" else "☾", color = Color.White, fontSize = 24.sp)
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "${state.completedCount}/${state.allTasks.size}",
                label = "Tugas selesai",
                accent = Color(0xFF7EF0C0),
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = state.highPriorityCount.toString(),
                label = "Prioritas tinggi",
                accent = Color(0xFFFFC0B9),
            )
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String, accent: Color) {
    Surface(modifier, color = Color.White.copy(alpha = .14f), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                Text(label, color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun QuickAdd(
    title: String,
    onTitleChange: (String) -> Unit,
    onAdd: () -> Unit,
    onOpenEditor: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 18.dp),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Tangkap ide dengan cepat", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Apa yang perlu dikerjakan?") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAdd() }),
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onAdd,
                    enabled = title.isNotBlank(),
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(),
                ) { Icon(Icons.Default.Add, contentDescription = "Tambah tugas") }
            }
            TextButton(onClick = onOpenEditor, contentPadding = PaddingValues(horizontal = 4.dp)) {
                Text("Atur detail, tanggal, atau subtugas")
            }
        }
    }
}

@Composable
private fun FilterBar(active: TaskFilter, onSelect: (TaskFilter) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(TaskFilter.entries) { filter ->
            FilterChip(
                selected = active == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.label, fontWeight = if (active == filter) FontWeight.Bold else FontWeight.Medium) },
                leadingIcon = if (active == filter) {{ Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }} else null,
            )
        }
    }
}

@Composable
private fun EmptyState(filter: TaskFilter, onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = .1f), shape = CircleShape) {
            Text("✓", modifier = Modifier.padding(horizontal = 25.dp, vertical = 17.dp), fontSize = 38.sp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            if (filter == TaskFilter.SEMUA) "Semua sudah rapi" else "Tidak ada tugas ${filter.label.lowercase()}",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            "Tambahkan satu hal kecil untuk memulai.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
        )
        OutlinedButton(onClick = onAdd) { Text("Tambah tugas") }
    }
}

@Composable
private fun TaskCard(
    item: TaskWithDetails,
    onToggle: () -> Unit,
    onToggleSubtask: (SubtaskEntity) -> Unit,
    onEdit: () -> Unit,
) {
    val done = item.task.completed
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onEdit),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(checked = done, onCheckedChange = { onToggle() })
                Column(Modifier.weight(1f).padding(top = 5.dp)) {
                    Text(
                        item.task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textDecoration = if (done) TextDecoration.LineThrough else null,
                        color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                    if (item.task.description.isNotBlank()) {
                        Text(
                            item.task.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit tugas") }
            }
            Row(
                modifier = Modifier.padding(start = 48.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PriorityBadge(item.task.priority)
                item.category?.let { MiniPill(it.name) }
                item.task.dueAt?.let { MiniPill(formatDue(it)) }
            }
            if (item.subtasks.isNotEmpty()) {
                val sorted = item.subtasks.sortedBy { it.position }
                val completed = sorted.count { it.completed }
                Column(Modifier.padding(start = 48.dp, top = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { completed.toFloat() / sorted.size },
                            modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                        )
                        Text(
                            "$completed/${sorted.size}",
                            modifier = Modifier.padding(start = 10.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                    sorted.take(3).forEach { subtask ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onToggleSubtask(subtask) }.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = subtask.completed,
                                onCheckedChange = { onToggleSubtask(subtask) },
                                modifier = Modifier.size(44.dp),
                            )
                            Text(
                                subtask.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textDecoration = if (subtask.completed) TextDecoration.LineThrough else null,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val dark = MaterialTheme.colorScheme.background.luminance() < .5f
    val (label, color) = when (priority) {
        Priority.TINGGI -> "Tinggi" to if (dark) Color(0xFFFFB4AB) else Coral
        Priority.SEDANG -> "Sedang" to if (dark) Color(0xFFFFDDB0) else Amber
        Priority.RENDAH -> "Rendah" to if (dark) Color(0xFF74DDB0) else Green
    }
    Surface(color = color.copy(alpha = .14f), shape = CircleShape) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(5.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MiniPill(label: String) {
    Surface(
        modifier = Modifier.widthIn(max = 110.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun TaskEditor(
    initial: TaskDraft,
    categories: List<CategoryEntity>,
    onAddCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (TaskDraft) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var draft by remember(initial.id) { mutableStateOf(initial) }
    var newSubtask by remember { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.imePadding()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, "Tutup") }
                    Text(
                        if (draft.id == 0L) "Tugas baru" else "Detail tugas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    if (onDelete != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Hapus tugas", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = draft.title,
                    onValueChange = { draft = draft.copy(title = it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Judul *") },
                    placeholder = { Text("Contoh: Kirim laporan") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
            }
            item {
                OutlinedTextField(
                    value = draft.description,
                    onValueChange = { draft = draft.copy(description = it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Catatan") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp),
                )
            }
            item { SectionTitle("Prioritas") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { priority ->
                        FilterChip(
                            selected = draft.priority == priority,
                            onClick = { draft = draft.copy(priority = priority) },
                            label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item { SectionTitle("Kategori") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories, key = { it.id }) { category ->
                        FilterChip(
                            selected = draft.categoryId == category.id,
                            onClick = { draft = draft.copy(categoryId = category.id) },
                            label = { Text(category.name) },
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { showCategoryDialog = true },
                            label = { Text("Kategori baru") },
                            leadingIcon = { Icon(Icons.Default.Add, null, Modifier.size(18.dp)) },
                        )
                    }
                }
            }
            item { SectionTitle("Waktu") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            pickDate(context, draft.dueAt) { value -> draft = draft.copy(dueAt = value) }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) { Text(draft.dueAt?.let(::formatDue) ?: "Pilih tenggat") }
                    if (draft.dueAt != null) {
                        IconButton(onClick = { draft = draft.copy(dueAt = null) }) {
                            Icon(Icons.Default.Close, "Hapus tenggat")
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            pickDateTime(context, draft.reminderAt ?: draft.dueAt) { value ->
                                draft = draft.copy(reminderAt = value)
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        Text(draft.reminderAt?.let(::formatDateTime) ?: "Atur pengingat")
                    }
                    if (draft.reminderAt != null) {
                        IconButton(onClick = { draft = draft.copy(reminderAt = null) }) {
                            Icon(Icons.Default.Close, "Hapus pengingat")
                        }
                    }
                }
            }
            item {
                Text(
                    "Pengingat dikirim secara lokal tanpa internet. Mode hemat baterai dapat menundanya.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
            item { SectionTitle("Subtugas (${draft.subtasks.count { it.completed }}/${draft.subtasks.size})") }
            items(draft.subtasks.indices.toList(), key = { index -> "${draft.subtasks[index].id}-$index" }) { index ->
                val subtask = draft.subtasks[index]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = subtask.completed,
                        onCheckedChange = { checked ->
                            draft = draft.copy(subtasks = draft.subtasks.toMutableList().also {
                                it[index] = subtask.copy(completed = checked)
                            })
                        },
                    )
                    OutlinedTextField(
                        value = subtask.title,
                        onValueChange = { title ->
                            draft = draft.copy(subtasks = draft.subtasks.toMutableList().also {
                                it[index] = subtask.copy(title = title)
                            })
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                    )
                    IconButton(onClick = {
                        draft = draft.copy(subtasks = draft.subtasks.toMutableList().also { it.removeAt(index) })
                    }) { Icon(Icons.Default.Close, "Hapus subtugas") }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newSubtask,
                        onValueChange = { newSubtask = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tambah langkah kecil") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (newSubtask.isNotBlank()) {
                                draft = draft.copy(subtasks = draft.subtasks + DraftSubtask(title = newSubtask.trim()))
                                newSubtask = ""
                            }
                        }),
                    )
                    IconButton(
                        enabled = newSubtask.isNotBlank(),
                        onClick = {
                            draft = draft.copy(subtasks = draft.subtasks + DraftSubtask(title = newSubtask.trim()))
                            newSubtask = ""
                        },
                    ) { Icon(Icons.Default.Add, "Tambah subtugas") }
                }
            }
            item {
                Button(
                    onClick = { onSave(draft) },
                    enabled = draft.title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Simpan tugas", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCategoryDialog) {
        var categoryName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Kategori baru") },
            text = {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nama kategori") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    enabled = categoryName.isNotBlank(),
                    onClick = { onAddCategory(categoryName); showCategoryDialog = false },
                ) { Text("Tambah") }
            },
            dismissButton = { TextButton(onClick = { showCategoryDialog = false }) { Text("Batal") } },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus tugas?") },
            text = { Text("Tugas dan semua subtugasnya akan dihapus dari perangkat ini.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete?.invoke() }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
}

private fun pickDate(context: Context, current: Long?, onPicked: (Long) -> Unit) {
    val zone = ZoneId.systemDefault()
    val date = current?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() } ?: LocalDate.now()
    DatePickerDialog(context, { _, year, month, day ->
        val endOfDay = LocalDate.of(year, month + 1, day).atTime(23, 59)
        onPicked(endOfDay.atZone(zone).toInstant().toEpochMilli())
    }, date.year, date.monthValue - 1, date.dayOfMonth).show()
}

private fun pickDateTime(context: Context, current: Long?, onPicked: (Long) -> Unit) {
    val zone = ZoneId.systemDefault()
    val dateTime = current?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDateTime() }
        ?: LocalDateTime.now().plusHours(1).withSecond(0).withNano(0)
    DatePickerDialog(context, { _, year, month, day ->
        TimePickerDialog(context, { _, hour, minute ->
            val selected = LocalDateTime.of(LocalDate.of(year, month + 1, day), LocalTime.of(hour, minute))
            onPicked(selected.atZone(zone).toInstant().toEpochMilli())
        }, dateTime.hour, dateTime.minute, true).show()
    }, dateTime.year, dateTime.monthValue - 1, dateTime.dayOfMonth).show()
}

private fun formatDue(epoch: Long): String {
    val date = Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    return when (date) {
        today -> "Hari ini"
        today.plusDays(1) -> "Besok"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM", Locale("id", "ID")))
    }
}

private fun formatDateTime(epoch: Long): String = Instant.ofEpochMilli(epoch)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale("id", "ID")))
