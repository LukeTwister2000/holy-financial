package com.example.ui.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.Transaction
import com.example.ui.ChurchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: ChurchViewModel) {
    val report by viewModel.aiReport.collectAsState()
    val isGenerating by viewModel.isGeneratingReport.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val churchName by viewModel.churchName.collectAsState()
    val context = LocalContext.current

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null && report != null) {
            exportPdf(context, uri, report!!, churchName)
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            exportCsvFile(context, uri, churchName, transactions)
        }
    }

    val excelExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.ms-excel")
    ) { uri ->
        if (uri != null) {
            exportExcelFile(context, uri, churchName, transactions)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Relatório com I.A.", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Introductory Card explaining the integration
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "A IA analisa o saldo total, divide as fontes de dízimos ou ofertas e gera projeções financeiras para apoiar as decisões ministeriais da igreja.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.generateAiReport(transactions) },
                    enabled = !isGenerating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isGenerating) "Consultando..." else "Gerar com I.A.")
                }
            }

            AnimatedVisibility(!isGenerating && !report.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { csvExportLauncher.launch("Relatorio_Financeiro.csv") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CSV", style = MaterialTheme.typography.labelSmall)
                    }
                    
                    OutlinedButton(
                        onClick = { excelExportLauncher.launch("Relatorio_Financeiro.xls") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Description, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Excel", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = { pdfExportLauncher.launch("Relatorio_Financeiro.pdf") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Imprimir A4", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }

            AnimatedContent(
                targetState = isGenerating,
                label = "ReportStateAnimation"
            ) { targetIsGenerating ->
                if (targetIsGenerating) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Text(
                                "Analisando transações coletadas...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "A inteligência artificial do Gemini está redigindo seu relatório consolidado...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else if (!report.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "Relatório Inteligente",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            pdfExportLauncher.launch("Relatorio_Financeiro.pdf")
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Filled.PictureAsPdf,
                                            contentDescription = "Exportar PDF",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, report!!)
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Relatório IA")
                                            context.startActivity(shareIntent)
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Filled.Share,
                                            contentDescription = "Compartilhar Texto",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = report!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.25f,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                "Nenhum Relatório Ativo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Clique em 'Gerar com I.A.' para ler as transações do Livro Caixa e obter diagnósticos profundos.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun exportCsvFile(context: Context, uri: Uri, churchName: String, transactions: List<Transaction>) {
    try {
        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        val currentDate = dateFormatter.format(java.util.Date())
        
        val csvHeader = "RELATÓRIO FINANCEIRO CONSOLIDADO: $churchName\nExportado em: $currentDate\n\nID;Data;Título;Tipo;Valor;Situação\n"
        
        var totalAmount = 0.0
        val csvBody = transactions.joinToString("\n") {
            totalAmount += if (it.type != com.example.data.TransactionType.DESPESA) it.amount else -it.amount
            val formattedDate = dateFormatter.format(java.util.Date(it.date))
            val convertedAmount = java.text.NumberFormat.getNumberInstance(java.util.Locale("pt", "BR")).format(it.amount)
            "${it.id};$formattedDate;\"${it.title}\";${if (it.type != com.example.data.TransactionType.DESPESA) "Receita" else "Despesa"};$convertedAmount;${if (it.isPaidViaPixOrCard) "Digital" else "Espécie"}"
        }
        
        val formattedTotal = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(totalAmount)
        val content = csvHeader + csvBody + "\n\n;;SUMÁRIO GERAL;;;$formattedTotal\n"

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray(Charsets.UTF_8))
        }
        Toast.makeText(context, "CSV Exportado com Sucesso!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar CSV", Toast.LENGTH_SHORT).show()
    }
}

fun exportExcelFile(context: Context, uri: Uri, churchName: String, transactions: List<Transaction>) {
    try {
        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        val currentDate = dateFormatter.format(java.util.Date())
        
        val htmlHeader = """<html>
            <head><meta charset="UTF-8"></head>
            <body>
            <h3>RELATÓRIO FINANCEIRO CONSOLIDADO: $churchName</h3>
            <p>Exportado em: $currentDate</p>
            <table border='1'>
            <tr><th>ID</th><th>Data</th><th>Título</th><th>Tipo</th><th>Valor</th><th>Situação</th></tr>
        """.trimIndent()
        
        var totalAmount = 0.0
        val htmlBody = transactions.joinToString("\n") {
            totalAmount += if (it.type != com.example.data.TransactionType.DESPESA) it.amount else -it.amount
            val formattedDate = dateFormatter.format(java.util.Date(it.date))
            val convertedAmount = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(it.amount)
            // Colorise cells
            val typeColor = if (it.type != com.example.data.TransactionType.DESPESA) "green" else "red"
            "<tr><td>${it.id}</td><td>$formattedDate</td><td>${it.title}</td><td style='color:$typeColor;'>${if (it.type != com.example.data.TransactionType.DESPESA) "Receita" else "Despesa"}</td><td>$convertedAmount</td><td>${if (it.isPaidViaPixOrCard) "Digital" else "Espécie"}</td></tr>"
        }
        val formattedTotal = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(totalAmount)
        val totalColor = if (totalAmount >= 0) "green" else "red"
        val htmlFooter = "<tr><td colspan='4'><b>SUMÁRIO GERAL</b></td><td colspan='2' style='color:$totalColor;'><b>$formattedTotal</b></td></tr></table></body></html>"
        
        val content = htmlHeader + htmlBody + htmlFooter

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray(Charsets.UTF_8))
        }
        Toast.makeText(context, "Excel Exportado com Sucesso!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar Excel", Toast.LENGTH_SHORT).show()
    }
}

fun exportPdf(context: Context, uri: Uri, reportText: String, churchName: String) {
    if (reportText.isBlank()) return
    try {
        val document = android.graphics.pdf.PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 50
        
        val contentWidth = pageWidth - 2 * margin
        
        val paint = android.text.TextPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }

        val titlePaint = android.text.TextPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        val staticLayout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            android.text.StaticLayout.Builder.obtain(reportText, 0, reportText.length, paint, contentWidth)
                .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            android.text.StaticLayout(reportText, paint, contentWidth, android.text.Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, false)
        }

        val totalLines = staticLayout.lineCount
        var currentLine = 0

        var pageNum = 1
        while (currentLine < totalLines) {
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            var yPos = margin.toFloat()
            if (pageNum == 1) {
                canvas.drawText("Relatório Financeiro: $churchName", margin.toFloat(), yPos, titlePaint)
                canvas.drawText("Data: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}", margin.toFloat(), yPos + 20f, paint)
                yPos += 50f
            }

            val maxContentHeight = pageHeight - margin - yPos
            
            var drawnHeight = 0f
            var linesToDrawThisPage = 0
            while (currentLine + linesToDrawThisPage < totalLines) {
                 val lineBottom = staticLayout.getLineBottom(currentLine + linesToDrawThisPage)
                 val lineTop = staticLayout.getLineTop(currentLine + linesToDrawThisPage)
                 val lineHeight = lineBottom - lineTop
                 if (drawnHeight + lineHeight > maxContentHeight) {
                     break
                 }
                 drawnHeight += lineHeight
                 linesToDrawThisPage++
            }

            if (linesToDrawThisPage == 0 && currentLine < totalLines) {
                // Failsafe in case a single line is too tall
                linesToDrawThisPage = 1
            }

            val startOffset = staticLayout.getLineStart(currentLine)
            val endOffset = staticLayout.getLineEnd(currentLine + linesToDrawThisPage - 1)
            val textToDraw = reportText.substring(startOffset, endOffset)
            
            val pageStaticLayout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.text.StaticLayout.Builder.obtain(textToDraw, 0, textToDraw.length, paint, contentWidth)
                    .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.2f)
                    .setIncludePad(false)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                android.text.StaticLayout(textToDraw, paint, contentWidth, android.text.Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, false)
            }

            canvas.save()
            canvas.translate(margin.toFloat(), yPos)
            pageStaticLayout.draw(canvas)
            canvas.restore()

            document.finishPage(page)
            currentLine += linesToDrawThisPage
            pageNum++
        }

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            document.writeTo(outputStream)
        }
        document.close()
        Toast.makeText(context, "PDF Exportado com Sucesso!", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar PDF", Toast.LENGTH_SHORT).show()
    }
}
