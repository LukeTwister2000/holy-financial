package com.example.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.ChurchViewModel
import com.example.ui.components.ChurchLogoView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ChurchViewModel) {
    val churchName by viewModel.churchName.collectAsState()
    val logoPath by viewModel.logoPath.collectAsState()
    val accentColorByPref by viewModel.accentColor.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val pastorName by viewModel.pastorName.collectAsState()
    val churchContact by viewModel.churchContact.collectAsState()
    val churchAddress by viewModel.churchAddress.collectAsState()

    val isExtracting by viewModel.isExtractingLogoText.collectAsState()
    val context = LocalContext.current

    // Forms states
    var editChurchName by remember(churchName) { mutableStateOf(churchName) }
    var editPastor by remember(pastorName) { mutableStateOf(pastorName) }
    var editContact by remember(churchContact) { mutableStateOf(churchContact) }
    var editAddress by remember(churchAddress) { mutableStateOf(churchAddress) }

    val members by viewModel.members.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val backupExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            exportBackupJson(context, uri, members, transactions)
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            viewModel.processChurchLogo(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ajustes & Customização", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Dynamic Logo & Intelligent Identity Recognition (HUD)
            Text(
                text = "Identidade e Logo da Igreja",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display Big Logo representation
                        ChurchLogoView(logoPath = logoPath, size = 72.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Logo da HUD Principal",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A logo selecionada altera o mini-thumbnail do topo da HUD e os cabeçalhos de relatórios gerados.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Importe a logo em qualquer formato do seu armazenamento. Uma I.A. dedicada extrairá automaticamente o nome textual e atualizará a HUD de forma autônoma.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { pickMedia.launch("image/*") },
                            enabled = !isExtracting,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Image, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isExtracting) "Lendo com I.A..." else "Selecionar Import")
                        }

                        if (!logoPath.isNullOrEmpty()) {
                            OutlinedButton(
                                onClick = { viewModel.clearLogo() },
                                modifier = Modifier.weight(0.7f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Remover", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    if (isExtracting) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                        )
                    }
                }
            }

            // Section 2: Accent Theming Swatches
            Text(
                text = "Paleta Litúrgica de Cores (Accent)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selecione o matiz litúrgico oficial da sua igreja. Toda a interface será ajustada dinamicamente.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThemeColorSwatchButton("GOLD", "Dourado", Color(0xFFD4AF37), accentColorByPref == "GOLD") {
                            viewModel.updateAccentColor("GOLD")
                        }
                        ThemeColorSwatchButton("GREEN", "Verde", Color(0xFF27AE60), accentColorByPref == "GREEN") {
                            viewModel.updateAccentColor("GREEN")
                        }
                        ThemeColorSwatchButton("BLUE", "Azul", Color(0xFF3498DB), accentColorByPref == "BLUE") {
                            viewModel.updateAccentColor("BLUE")
                        }
                        ThemeColorSwatchButton("PURPLE", "Roxo", Color(0xFF9855F7), accentColorByPref == "PURPLE") {
                            viewModel.updateAccentColor("PURPLE")
                        }
                    }
                }
            }

            // Section 3: Profile metadata edit forms
            Text(
                text = "Informações do Perfil da Igreja",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editChurchName,
                        onValueChange = { editChurchName = it },
                        label = { Text("Nome da Igreja", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = editPastor,
                        onValueChange = { editPastor = it },
                        label = { Text("Pastor Responsável", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = editContact,
                        onValueChange = { editContact = it },
                        label = { Text("E-mail Oficial", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Endereço Físico", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.updateChurchProfile(editChurchName, editPastor, editContact, editAddress)
                            Toast.makeText(context, "Nome e Perfil Salvos!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Salvar Perfil")
                    }
                }
            }

            // Section 4: Simulated Multi-user Role permissions selector
            Text(
                text = "Simulador Multifuncional de Acesso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Role Ativo no Aplicativo:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Acesso multiusuário fictício simulado. O perfil escolhido pode alterar travas de gravação do Livro Caixa.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleSelectRow("Administrador", "Direitos irrestritos para cadastrar dízimos e apagar despesas.", userRole == "Administrador") {
                            viewModel.updateUserRole("Administrador")
                        }
                        RoleSelectRow("Tesoureiro", "Pode visualizar saldo e cadastrar novas ofertas ativas.", userRole == "Tesoureiro") {
                            viewModel.updateUserRole("Tesoureiro")
                        }
                        RoleSelectRow("Pastor Responsável", "Focado na análise de relatórios IA e download compilado.", userRole == "Pastor Responsável") {
                            viewModel.updateUserRole("Pastor Responsável")
                        }
                    }
                }
            }

            // Section 5: Direct Link Feedback action items (To lucaswolf53@gmail.com)
            Text(
                text = "Canal de Suporte e Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Feedback Direto ao Desenvolvedor",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Encaminhe suas sugestões de melhoria diretamente para a caixa de e-mail do engenheiro designado. Os links abaixo estruturam o envio automático.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    FeedbackLinkItem(
                        title = "Enviar Feedback Geral (lucaswolf53@gmail.com)",
                        icon = Icons.Filled.MailOutline,
                        onClick = {
                            launchEmailIntent(context, "Feedback: Aplicativo de Finanças", "Olá dev,\n\nGostaria de sugerir o seguinte ajuste no aplicativo:")
                        }
                    )

                    FeedbackLinkItem(
                        title = "Relatar Bug Técnico",
                        icon = Icons.Filled.BugReport,
                        onClick = {
                            launchEmailIntent(context, "Bug: Aplicativo de Finanças", "Olá dev,\n\nIdentifiquei o seguinte erro ou comportamento imprevisto:")
                        }
                    )

                    FeedbackLinkItem(
                        title = "Solicitar Feature Customizada",
                        icon = Icons.Filled.Recommend,
                        onClick = {
                            launchEmailIntent(context, "Feature: Finanças Igreja", "Olá dev,\n\nSeria excelente se pudéssemos adicionar os seguintes recursos na próxima atualização:")
                        }
                    )
                }
            }
            // Section 6: Backup
            Text(
                text = "Segurança de Dados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Backup Offline Local",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gere um arquivo JSON com o banco de dados atual contendo todos os dados financeiros e registros de membros.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { backupExportLauncher.launch("ChurchFinance_Backup_${System.currentTimeMillis()}.json") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Backup, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Exportar Banco de Dados (JSON)")
                    }
                }
            }
        }
    }
}

fun exportBackupJson(
    context: android.content.Context,
    uri: Uri,
    members: List<com.example.data.Member>,
    transactions: List<com.example.data.Transaction>
) {
    try {
        val root = org.json.JSONObject()
        val membersArray = org.json.JSONArray()
        members.forEach { m ->
            val obj = org.json.JSONObject()
            obj.put("id", m.id)
            obj.put("name", m.name)
            obj.put("contact", m.contact)
            obj.put("groupName", m.groupName)
            obj.put("age", m.age)
            obj.put("gender", m.gender)
            obj.put("isLeader", m.isLeader)
            obj.put("birthDate", m.birthDate)
            obj.put("joinedDate", m.joinedDate)
            membersArray.put(obj)
        }
        
        val transactionsArray = org.json.JSONArray()
        transactions.forEach { t ->
            val obj = org.json.JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("amount", t.amount)
            obj.put("type", t.type.name)
            obj.put("date", t.date)
            obj.put("memberId", if (t.memberId == null) org.json.JSONObject.NULL else t.memberId)
            obj.put("isPaidViaPixOrCard", t.isPaidViaPixOrCard)
            transactionsArray.put(obj)
        }
        
        root.put("members", membersArray)
        root.put("transactions", transactionsArray)
        
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(root.toString(4).toByteArray(Charsets.UTF_8))
        }
        Toast.makeText(context, "Backup exportado com sucesso!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar o backup", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ThemeColorSwatchButton(
    prefName: String,
    displayName: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RoleSelectRow(
    title: String,
    sub: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FeedbackLinkItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(12.dp)
        )
    }
}

fun launchEmailIntent(context: android.content.Context, subject: String, body: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("lucaswolf53@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    try {
        context.startActivity(Intent.createChooser(emailIntent, "Enviar E-mail via..."))
    } catch (e: Exception) {
        Toast.makeText(context, "Aplicativo de e-mail não encontrado.", Toast.LENGTH_SHORT).show()
    }
}
