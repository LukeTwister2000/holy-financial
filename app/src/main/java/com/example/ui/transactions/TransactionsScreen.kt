package com.example.ui.transactions

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.ui.ChurchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: ChurchViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("TODOS") } // TODOS, ENTRADAS, SAIDAS

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "ENTRADAS" -> transactions.filter { it.type == TransactionType.DIZIMO || it.type == TransactionType.OFERTA }
            "SAIDAS" -> transactions.filter { it.type == TransactionType.DESPESA }
            else -> transactions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Livro Caixa da Igreja", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, "Lançamento") },
                text = { Text("Adicionar") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Minimal filter selection segment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabChip("Todos", selectedFilter == "TODOS") { selectedFilter = "TODOS" }
                FilterTabChip("Dízimos e Ofertas", selectedFilter == "ENTRADAS") { selectedFilter = "ENTRADAS" }
                FilterTabChip("Despesas", selectedFilter == "SAIDAS") { selectedFilter = "SAIDAS" }
            }

            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                                Icons.Filled.Inbox,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum lançamento no Caixa encontrado.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Comece adicionando novos dízimos, ofertas espontâneas ou despesas gerais operacionais pelo botão abaixo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTransactions, key = { it.id }) { t ->
                        TransactionItem(t, viewModel, onDelete = { viewModel.deleteTransaction(t.id) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddTransactionDialog(
            viewModel = viewModel,
            onDismiss = { showDialog = false },
            onSave = { title, amount, type, memberId, isPixOrCard ->
                viewModel.insertTransaction(title, amount, type, memberId, isPixOrCard)
                showDialog = false
            }
        )
    }
}

@Composable
fun FilterTabChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItem(t: Transaction, viewModel: ChurchViewModel, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (t.type == TransactionType.DESPESA) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (t.type) {
                            TransactionType.DIZIMO -> Icons.Filled.VolunteerActivism
                            TransactionType.OFERTA -> Icons.Filled.CardGiftcard
                            TransactionType.DESPESA -> Icons.Filled.Build
                        },
                        contentDescription = null,
                        tint = if (t.type == TransactionType.DESPESA) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when (t.type) {
                                TransactionType.DIZIMO -> "Dízimo"
                                TransactionType.OFERTA -> "Oferta"
                                TransactionType.DESPESA -> "Despesa"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (t.isPaidViaPixOrCard) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(
                                        Icons.Filled.QrCode,
                                        contentDescription = null,
                                        modifier = Modifier.size(10.dp),
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        "PIX/Cartão",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = viewModel.formatCurrency(t.amount),
                    color = if (t.type == TransactionType.DESPESA) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Excluir Lançamento",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: ChurchViewModel,
    onDismiss: () -> Unit,
    onSave: (String, Double, TransactionType, Int?, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.DIZIMO) }
    var isPixOrCard by remember { mutableStateOf(false) }

    // Simulated Checkout Systems (Pix integration) - Level AAAAA Visual details
    var selectedPaymentMethod by remember { mutableStateOf("PIX") } // PIX or CARD
    var showSimulatorDetails by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Credit Card simulation states
    var cardNum by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Lançamento", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descrição (Ex: Oferta Culto de Domingo)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Valor (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                Text("Tipo de Lançamento:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TransactionType.values().forEach { t ->
                        val isSelected = type == t
                        val chipText = when (t) {
                            TransactionType.DIZIMO -> "Dízimo"
                            TransactionType.OFERTA -> "Oferta"
                            TransactionType.DESPESA -> "Despesa"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    type = t
                                    if (t == TransactionType.DESPESA) {
                                        isPixOrCard = false
                                        showSimulatorDetails = false
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chipText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (type != TransactionType.DESPESA) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(10.dp)
                    ) {
                        Checkbox(
                            checked = isPixOrCard,
                            onCheckedChange = { 
                                isPixOrCard = it
                                showSimulatorDetails = it
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Simular Integração de Pagamento", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("Simula Gateway via PIX ou Cartão", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Expand checkout simulator (Level AAAAA!)
                AnimatedVisibility(
                    visible = showSimulatorDetails,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text("Simulador de Gateway", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedPaymentMethod == "PIX") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedPaymentMethod = "PIX" }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Código PIX", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (selectedPaymentMethod == "PIX") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedPaymentMethod == "CARD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedPaymentMethod = "CARD" }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Cartão de Crédito", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (selectedPaymentMethod == "CARD") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedPaymentMethod == "PIX") {
                            // Pix QR Payload simulator
                            val amountStr = amount.ifEmpty { "50.00" }
                            val mockPixKey = "00020101021226830014br.gov.pix0114lucaswolf53@gmail.com5204000053039865405${amountStr}5802BR5918IgrejaCristoVive6009SaoPaulo62070503***6304"
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.QrCode, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${mockPixKey.take(24)}... (Chave IA)",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Copiar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(mockPixKey))
                                                Toast.makeText(context, "Chave PIX copiada!", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("QR Code integrado com a chave do desenvolvedor. Pronto para dízimos instantâneos.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        } else {
                            // Credit Card layout simulator
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = cardNum,
                                    onValueChange = { if (it.length <= 16) cardNum = it },
                                    label = { Text("Número do Cartão (Simulado)", style = MaterialTheme.typography.labelSmall) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardName,
                                        onValueChange = { cardName = it },
                                        label = { Text("Nome do Titular", style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = cardCvv,
                                        onValueChange = { if (it.length <= 3) cardCvv = it },
                                        label = { Text("CVV", style = MaterialTheme.typography.labelSmall) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val finalTitle = title.ifEmpty { "Oferta Ministerial" }
                    onSave(finalTitle, value, type, null, isPixOrCard)
                },
                enabled = amount.isNotEmpty()
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
