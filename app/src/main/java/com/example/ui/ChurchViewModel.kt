package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.ChurchDatabase
import com.example.data.ChurchRepository
import com.example.data.Member
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.data.AuditLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ChurchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChurchRepository
    private val sharedPrefs = application.getSharedPreferences("church_prefs", Context.MODE_PRIVATE)

    val members: StateFlow<List<Member>>
    val transactions: StateFlow<List<Transaction>>
    val auditLogs: StateFlow<List<AuditLog>>

    private val _churchName = MutableStateFlow(sharedPrefs.getString("church_name", "Igreja Cristo Vive") ?: "Igreja Cristo Vive")
    val churchName = _churchName.asStateFlow()

    private val _logoPath = MutableStateFlow(sharedPrefs.getString("logo_path", null))
    val logoPath = _logoPath.asStateFlow()

    private val _accentColor = MutableStateFlow(sharedPrefs.getString("accent_color", "GOLD") ?: "GOLD")
    val accentColor = _accentColor.asStateFlow()

    private val _userRole = MutableStateFlow(sharedPrefs.getString("user_role", "Administrador") ?: "Administrador")
    val userRole = _userRole.asStateFlow()

    // Additional church profile info for AAAAA grade reporting
    private val _pastorName = MutableStateFlow(sharedPrefs.getString("pastor_name", "Pr. Lucas Wolf") ?: "Pr. Lucas Wolf")
    val pastorName = _pastorName.asStateFlow()

    private val _churchContact = MutableStateFlow(sharedPrefs.getString("church_contact", "contato@cristovive.org") ?: "contato@cristovive.org")
    val churchContact = _churchContact.asStateFlow()

    private val _churchAddress = MutableStateFlow(sharedPrefs.getString("church_address", "Av. das Nações, 700 - Centro") ?: "Av. das Nações, 700 - Centro")
    val churchAddress = _churchAddress.asStateFlow()

    private val _aiReport = MutableStateFlow<String?>(null)
    val aiReport = _aiReport.asStateFlow()

    private val _isGeneratingReport = MutableStateFlow(false)
    val isGeneratingReport = _isGeneratingReport.asStateFlow()
    
    private val _isExtractingLogoText = MutableStateFlow(false)
    val isExtractingLogoText = _isExtractingLogoText.asStateFlow()

    init {
        val database = ChurchDatabase.getDatabase(application)
        repository = ChurchRepository(database.churchDao())
        members = repository.allMembers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        transactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        auditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    private fun logAction(action: String, details: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAuditLog(com.example.data.AuditLog(action = action, details = details, userName = _userRole.value))
    }

    fun insertMember(name: String, contact: String, age: Int, gender: String, groupName: String, isLeader: Boolean, birthDate: Long = 0L) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertMember(Member(name = name, contact = contact, age = age, gender = gender, groupName = groupName, isLeader = isLeader, birthDate = birthDate))
        logAction("Membro Adicionado", "Membro: $name, Grupo: $groupName")
    }

    fun insertTransaction(title: String, amount: Double, type: TransactionType, memberId: Int?, isPaidViaPixOrCard: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTransaction(Transaction(title = title, amount = amount, type = type, memberId = memberId, isPaidViaPixOrCard = isPaidViaPixOrCard))
        logAction("Transação Financeira Adicionada", "${type.name} - Valor: ${formatCurrency(amount)} ($title)")
    }

    fun deleteTransaction(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTransaction(id)
        logAction("Transação Financeira Removida", "ID da transação: $id removida do sistema.")
    }

    // Dynamic preference updates
    fun updateAccentColor(color: String) {
        _accentColor.value = color
        sharedPrefs.edit().putString("accent_color", color).apply()
    }

    fun updateUserRole(role: String) {
        _userRole.value = role
        sharedPrefs.edit().putString("user_role", role).apply()
    }

    fun updateChurchProfile(name: String, pastor: String, contact: String, address: String) {
        _churchName.value = name
        _pastorName.value = pastor
        _churchContact.value = contact
        _churchAddress.value = address

        sharedPrefs.edit()
            .putString("church_name", name)
            .putString("pastor_name", pastor)
            .putString("church_contact", contact)
            .putString("church_address", address)
            .apply()
    }

    fun clearLogo() {
        _logoPath.value = null
        sharedPrefs.edit().remove("logo_path").apply()
        // Delete local stored logo file
        try {
            val file = File(getApplication<Application>().filesDir, "church_logo_captured.jpg")
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateAiReport(transactions: List<Transaction>) = viewModelScope.launch(Dispatchers.IO) {
        _isGeneratingReport.value = true
        try {
             val prompt = buildString {
                 appendLine("Você é um assistente financeiro profissional especializado de uma igreja com as seguintes características:")
                 appendLine("- Nome da Igreja: ${_churchName.value}")
                 appendLine("- Pastor Responsável: ${_pastorName.value}")
                 appendLine("- Endereço: ${_churchAddress.value}")
                 appendLine("- Contato: ${_churchContact.value}")
                 appendLine("")
                 appendLine("Por favor, gere um relatório profissional mensal de fluxo de caixa em português brasileiro com as seguintes transações. Destaque o saldo consolidado, total de receitas de dízimos, ofertas e total de despesas operacionais detalhadas.")
                 appendLine("Classifique o estado financeiro da igreja (Superávit ou Déficit) de maneira encorajadora e faça 3 recomendações financeiras personalizadas baseadas nas despesas dadas.")
                 appendLine("")
                 appendLine("Lista de Transações Cadastradas:")
                 transactions.forEach { t ->
                     val pText = if (t.isPaidViaPixOrCard) "(Via PIX/Cartão)" else ""
                     appendLine("- Título: ${t.title} | Tipo: ${t.type.name} | Valor: ${formatCurrency(t.amount)} $pText")
                 }
                 appendLine("")
                 appendLine("Gere um relatório estruturado, utilizando títulos amigáveis, listas com marcadores e uma análise profissional.")
             }

             val request = GenerateContentRequest(
                 contents = listOf(Content(parts = listOf(Part(text = prompt))))
             )

             val apiKey = BuildConfig.GEMINI_API_KEY
             if (apiKey.isBlank() || apiKey.contains("MY_GEMINI_API_KEY")) {
                 _aiReport.value = "⚠️ Chave da API do Gemini não configurada!\n\nPara gerar relatórios, por favor adicione a sua chave de API do Google Gemini com a variável GEMINI_API_KEY no painel de segredos (Secrets) do seu projeto."
                 _isGeneratingReport.value = false
                 return@launch
             }

             val response = RetrofitClient.service.generateContent(apiKey, request)
             val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
             _aiReport.value = text ?: "Erro ao gerar o relatório com a Inteligência Artificial."
        } catch (e: Exception) {
             _aiReport.value = "Erro de conexão ao acessar a Inteligência Artificial: ${e.message}"
        } finally {
             _isGeneratingReport.value = false
        }
    }

    fun processChurchLogo(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        _isExtractingLogoText.value = true
        try {
            // Copy image file to local storage to persist access reliably
            val context = getApplication<Application>()
            val logoFile = File(context.filesDir, "church_logo_captured.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                logoFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            _logoPath.value = logoFile.absolutePath
            sharedPrefs.edit().putString("logo_path", logoFile.absolutePath).apply()

            // Load bitmap for Gemini API analysis to automatically detect the church name
            val bitmap = loadBitmapFromUri(uri)
            val base64Image = bitmapToBase64(bitmap)

            val prompt = "Nesta imagem da logo da igreja, identifique o NOME DA IGREJA escrito. " +
                    "Retorne APENAS o nome textual puro identificado, sem aspas, sem descrições adicionais e sem nenhuma outra frase como 'O nome é'. " +
                    "Se você não conseguir identificar de forma clara o nome no texto, ou se for genérico, retorne 'Igreja Batista Cristo Vive'."

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                        )
                    )
                )
            )
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey.contains("MY_GEMINI_API_KEY")) {
                _churchName.value = "Insira sua API Key do Gemini no painel Secrets"
                return@launch
            }
            val response = RetrofitClient.service.generateContent(apiKey, request)
            var extractedText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()

            if (!extractedText.isNullOrEmpty() && extractedText.length < 55) {
                extractedText = extractedText.replace("\"", "").replace("'", "")
                _churchName.value = extractedText
                sharedPrefs.edit().putString("church_name", extractedText).apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isExtractingLogoText.value = false
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val resolver = getApplication<Application>().contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(resolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setTargetSampleSize(2)
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(resolver, uri)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    // Brazilian Real Currency Formatter Utility
    fun formatCurrency(amount: Double): String {
        val ptBr = Locale("pt", "BR")
        return NumberFormat.getCurrencyInstance(ptBr).format(amount)
    }
}
