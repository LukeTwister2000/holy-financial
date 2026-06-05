package com.example.ui.members

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Member
import com.example.ui.ChurchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(viewModel: ChurchViewModel) {
    val allMembers by viewModel.members.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    
    val groups = listOf("Todos", "Geral", "Homens", "Mulheres", "Jovens", "Adolescentes", "Crianças")
    var selectedGroup by remember { mutableStateOf("Todos") }
    
    val members = if (selectedGroup == "Todos") allMembers else allMembers.filter { it.groupName == selectedGroup }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestão de Membros e Grupos", fontWeight = FontWeight.Bold, fontSize = 20.sp) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, "Novo Membro") },
                text = { Text("Membro") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar membro por nome") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(groups) { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { selectedGroup = group },
                        label = { Text(group) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            
            val filteredMembers = members.filter { it.name.contains(searchQuery, ignoreCase = true) }

            if (filteredMembers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PeopleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum Membro encontrado no Grupo: $selectedGroup.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMembers, key = { it.id }) { m ->
                        MemberItem(m)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddMemberDialog(
            availableGroups = groups.drop(1), // Exclude "Todos"
            onDismiss = { showDialog = false },
            onSave = { name, contact, age, gender, groupName, isLeader, birthDateText ->
                var finalBirthDate = 0L
                try {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    val date = sdf.parse(birthDateText)
                    if (date != null) finalBirthDate = date.time
                } catch (e: Exception) {
                    // Ignore parsing error, it stays 0L
                }
                viewModel.insertMember(name, contact, age, gender, groupName, isLeader, finalBirthDate)
                showDialog = false
            }
        )
    }
}

@Composable
fun MemberItem(m: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (m.isLeader) "${m.name} (Líder)" else m.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = if (m.contact.contains("@")) Icons.Filled.Mail else Icons.Filled.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = m.contact,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = m.groupName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            IconButton(
                onClick = { /* Simulated Call/Message interaction */ },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    Icons.Filled.ContactPhone,
                    contentDescription = "Falar com Membro",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    availableGroups: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Masculino") }
    var isLeader by remember { mutableStateOf(false) }
    var birthdateText by remember { mutableStateOf("") }

    val age = ageText.toIntOrNull() ?: 0

    val calculatedGroup = when {
        age < 12 -> "Crianças"
        age < 18 -> "Adolescentes"
        age <= 29 -> "Jovens"
        else -> if (gender == "Masculino") "Homens" else "Mulheres"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Membro", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Celular ou E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it.filter { char -> char.isDigit() } },
                        label = { Text("Idade") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = birthdateText,
                        onValueChange = { birthdateText = it },
                        label = { Text("Aniversário (dd/mm/aaaa)") },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sexo: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    listOf("Masculino", "Feminino").forEach { sex ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = gender == sex,
                                onClick = { gender = sex }
                            )
                            Text(sex, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isLeader, onCheckedChange = { isLeader = it })
                    Text("Líder do Grupo?")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Grupo Automático: $calculatedGroup", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, contact, age, gender, calculatedGroup, isLeader, birthdateText) },
                enabled = name.isNotBlank() && contact.isNotBlank() && ageText.isNotBlank()
            ) { Text("Gravar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
