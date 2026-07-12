package com.example.dashdine.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * DeepSeek AI 对话服务
 * 使用 DeepSeek Chat API 实现智能饮食建议
 */
object DeepSeekApiService {

    private const val API_URL = "https://api.deepseek.com/v1/chat/completions"
    private const val API_KEY = "YOUR_DEEPSEEK_API_KEY" // 替换为你的 API Key
    private const val MODEL = "deepseek-chat"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * 发送消息到 DeepSeek，获取 AI 回复
     */
    suspend fun chat(
        messages: List<ChatMessage>,
        systemPrompt: String = SYSTEM_PROMPT
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messagesArray = JSONArray().apply {
                // 系统提示
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                // 历史消息
                messages.forEach { msg ->
                    put(JSONObject().apply {
                        put("role", msg.role)
                        put("content", msg.content)
                    })
                }
            }

            val requestBody = JSONObject().apply {
                put("model", MODEL)
                put("messages", messagesArray)
                put("temperature", 0.8)
                put("max_tokens", 1024)
            }

            val request = Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer $API_KEY")
                .header("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val json = JSONObject(responseBody)
                val choices = json.getJSONArray("choices")
                val content = choices
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                Result.success(content)
            } else {
                val errorBody = response.body?.string() ?: "未知错误"
                Result.failure(Exception("API 请求失败 (${response.code}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 消息数据类
     */
    data class ChatMessage(
        val role: String,  // "user" | "assistant"
        val content: String
    )

    /**
     * 系统提示词 — 专注于饮食推荐
     */
    private val SYSTEM_PROMPT = """
你是一个专业的美食推荐助手，名字叫"小食光"。你的职责是：

1. 根据用户的口味偏好、预算、心情等，推荐合适的美食
2. 给出专业的饮食建议，包括营养搭配、热量提示等
3. 用温暖、亲切的语气交流，像朋友一样聊天
4. 推荐菜品时，按以下格式结构化输出，方便前端解析展示：
   - 菜品名称用【】括起来，例如：【招牌红烧肉饭】
   - 在菜品名后紧接着用{}给出简短的一两句推荐理由，例如：{肥而不腻，入口即化，本店爆款}
   - 价格用 ¥XX 格式标注

你只推荐中餐、日料、轻食、烧烤等外卖常见品类。每次回复推荐 2-4 个菜品即可，不要太多。
如果用户问无关饮食的问题，礼貌地引导回美食话题。
""".trimIndent()
}
