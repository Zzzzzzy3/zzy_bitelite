package com.example.dashdine.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.network.DeepSeekApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 解析出的食物推荐项
 */
data class FoodRecommendation(
    val name: String,
    val reason: String,
    val price: String = ""
)

/**
 * 聊天消息 UI 模型
 */
data class ChatMessageUi(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String,        // "user" | "assistant"
    val content: String,
    val recommendations: List<FoodRecommendation> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * AI 对话 UI 状态
 */
data class AiChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        // 欢迎消息
        val welcomeMsg = ChatMessageUi(
            role = "assistant",
            content = "嗨！我是你的美食助手 **小食光** 🍽️\n\n今天想吃什么？告诉我你的口味偏好，我来帮你推荐！\n\n比如：\n- 天冷了想喝热汤\n- 想吃辣的解解馋\n- 帮我推荐低卡的午餐",
            recommendations = listOf(
                FoodRecommendation("招牌红烧肉饭", "本店招牌，月销1200+", "¥32"),
                FoodRecommendation("番茄牛肉面", "暖心暖胃，浓郁鲜香", "¥26"),
                FoodRecommendation("冰镇柠檬水", "解暑必备，清爽解腻", "¥8")
            )
        )
        _uiState.value = _uiState.value.copy(messages = listOf(welcomeMsg))
    }

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, error = null)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return

        val userMsg = ChatMessageUi(role = "user", content = text)
        val currentMessages = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            inputText = "",
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            // 转换为 API 消息格式（只传最近10轮对话以控制 token）
            val apiMessages = currentMessages.takeLast(10).map { msg ->
                DeepSeekApiService.ChatMessage(role = msg.role, content = msg.content)
            }

            val result = DeepSeekApiService.chat(apiMessages)

            result.onSuccess { replyContent ->
                val recommendations = parseRecommendations(replyContent)
                val cleanContent = cleanContent(replyContent)
                val assistantMsg = ChatMessageUi(
                    role = "assistant",
                    content = cleanContent,
                    recommendations = recommendations
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + assistantMsg,
                    isLoading = false
                )
            }.onFailure { error ->
                // 如果 API 不可达，用本地逻辑生成回复
                val localReply = generateLocalReply(text)
                val recommendations = parseRecommendations(localReply)
                val assistantMsg = ChatMessageUi(
                    role = "assistant",
                    content = localReply,
                    recommendations = recommendations
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + assistantMsg,
                    isLoading = false,
                    error = if (error.message?.contains("Unable to resolve host") == true)
                        null  // 网络不通时静默降级
                    else
                        "网络连接异常，已切换到离线模式"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 从 AI 回复中解析食物推荐（纯字符串解析，避免 regex 兼容性问题）
     * 匹配格式：【菜品名】{推荐理由} 或 【菜品名】
     */
    private fun parseRecommendations(content: String): List<FoodRecommendation> {
        val results = mutableListOf<FoodRecommendation>()
        var pos = 0
        while (true) {
            val nameStart = content.indexOf('【', pos)
            if (nameStart < 0) break
            val nameEnd = content.indexOf('】', nameStart + 1)
            if (nameEnd < 0) break

            val name = content.substring(nameStart + 1, nameEnd).trim()

            var reason = "值得一试"
            var nextPos = nameEnd + 1
            // 紧接着检查 {推荐理由}
            if (nameEnd + 1 < content.length) {
                val reasonStart = content.indexOf('{', nameEnd)
                if (reasonStart >= 0 && reasonStart <= nameEnd + 3) {
                    val reasonEnd = content.indexOf('}', reasonStart + 1)
                    if (reasonEnd > reasonStart) {
                        reason = content.substring(reasonStart + 1, reasonEnd).trim()
                        nextPos = reasonEnd + 1
                    }
                }
            }
            pos = nextPos

            results.add(
                FoodRecommendation(
                    name = name,
                    reason = reason,
                    price = extractPrice(content, name)
                )
            )
        }
        return results
    }

    private fun extractPrice(content: String, dishName: String): String {
        // 在菜品名附近查找 ¥xx 价格
        val area = if (content.length > 200) content.take(200) else content
        var idx = 0
        while (idx < area.length) {
            val yenPos = area.indexOf('¥', idx)
            if (yenPos < 0) break
            // 读取数字部分
            val numStart = yenPos + 1
            var numEnd = numStart
            while (numEnd < area.length && (area[numEnd].isDigit() || area[numEnd] == '.')) {
                numEnd++
            }
            if (numEnd > numStart) {
                return area.substring(yenPos, numEnd)
            }
            idx = yenPos + 1
        }
        return ""
    }

    /**
     * 清理内容：去掉结构化标记符号（纯字符串操作）
     */
    private fun cleanContent(content: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < content.length) {
            when {
                // 跳过 {xxx} 块
                content[i] == '{' -> {
                    var j = i + 1
                    while (j < content.length && content[j] != '}') j++
                    i = j + 1  // 跳过 } 本身
                }
                // 【xxx】 → xxx（去掉符号，保留内容）
                content[i] == '【' -> {
                    val start = i + 1
                    var j = start
                    while (j < content.length && content[j] != '】') {
                        sb.append(content[j])
                        j++
                    }
                    i = j + 1  // 跳过 】
                }
                else -> {
                    sb.append(content[i])
                    i++
                }
            }
        }
        return sb.toString().trim()
    }

    /**
     * 本地离线回复生成器 — API 不可达时的降级方案
     */
    private fun generateLocalReply(userInput: String): String {
        val input = userInput.lowercase()
        return when {
            input.contains("辣") || input.contains("麻辣") || input.contains("川") ->
                "想吃辣的吗？推荐这些给你：\n\n【水煮鱼】{麻辣鲜香，鱼片嫩滑，川菜经典} ¥48\n【麻婆豆腐】{正宗川味，麻辣鲜香嫩，下饭神器} ¥18\n【担担面】{芝麻酱和肉末的完美结合，一碗不过瘾} ¥16\n\n配一杯【老成都冰粉】{冰凉解辣，手工制作} ¥10，绝了！"

            input.contains("甜") || input.contains("甜品") || input.contains("蛋糕") ->
                "甜食让人快乐！来看看这些：\n\n【抹茶提拉米苏】{抹茶与提拉米苏的完美融合，微苦回甘} ¥22\n【芒果糯米饭】{泰国芒果配椰浆糯米，香甜软糯} ¥18\n【巧克力熔岩蛋糕】{巧克力岩浆缓缓流出，配冰淇淋，冰火两重天} ¥26"

            input.contains("轻食") || input.contains("沙拉") || input.contains("低卡") || input.contains("减肥") ->
                "健康轻食来啦，好吃不胖：\n\n【凉拌黄瓜】{清脆爽口，解腻必备，仅80kcal} ¥8\n【蒜蓉西兰花】{清爽健康，清淡不油腻，120kcal} ¥12\n【鲜榨橙汁】{100%鲜榨，不加糖不加水，满满维C} ¥16"

            input.contains("面") || input.contains("粉") || input.contains("汤") ->
                "来碗热乎的面食吧：\n\n【番茄牛肉面】{新鲜番茄熬汤，大块牛腩配手工拉面} ¥26\n【红烧牛肉面】{牛腱肉慢炖4小时，浓郁汤头} ¥28\n【担担面】{芝麻酱和肉末的完美结合，麻辣鲜香} ¥16"

            input.contains("饭") || input.contains("盖浇") || input.contains("主食") ->
                "推荐这些热门主食：\n\n【招牌红烧肉饭】{五花肉慢炖2小时，肥而不腻，本店爆款} ¥32\n【宫保鸡丁套餐】{鸡丁嫩滑，花生酥脆，限时特价} ¥19.9\n【糖醋里脊盖饭】{外酥里嫩，酸甜可口} ¥24"

            input.contains("炸鸡") || input.contains("烧烤") || input.contains("小食") ->
                "解馋小食安排上：\n\n【香酥炸鸡翅】{外酥里嫩，秘制香料，4只装} ¥16\n【春卷】{金黄酥脆，馅料丰富，蘸甜辣酱更美味} ¥14\n【红油抄手】{皮薄馅大，红油花椒，一口一个} ¥18"

            else ->
                "好的！根据你的口味，我推荐这些热门菜品：\n\n【招牌红烧肉饭】{五花肉慢炖2小时，肥而不腻，月销1200+} ¥32\n【番茄牛肉面】{新鲜番茄熬汤，大块牛腩，暖心暖胃} ¥26\n【宫保鸡丁套餐】{鸡丁嫩滑，花生酥脆，限时特价中} ¥19.9\n\n想了解哪道菜的详情，或者换个口味试试？"
        }
    }
}
