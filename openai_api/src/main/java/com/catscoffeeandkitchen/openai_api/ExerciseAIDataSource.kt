package com.catscoffeeandkitchen.openai_api

import com.aallam.openai.api.chat.ChatCompletionFunction
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import timber.log.Timber
import javax.inject.Inject

class ExerciseAIDataSource @Inject constructor(
    private val openAI: OpenAI
) {
    @Serializable
    private data class ExerciseCues(
        val cues: List<String>
    )

    suspend fun getExerciseCues(exerciseName: String): List<String>? {
        val completions = openAI.chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = "What are some good cues for the exercise $exerciseName?"
                    )
                ),
                tools = listOf(
                    Tool.function(
                        name = "ExerciseCues",
                        description = "Create list of cues for an exercise",
                        parameters = Parameters.buildJsonObject {
                            put("type", "object")
                            putJsonObject("properties") {
                                putJsonObject("cues") {
                                    put("type", "array")
                                    put("description", "short list of cues to follow for an exercise")
                                    putJsonObject("items") {
                                        put("type", "string")
                                    }
                                    put("maxItems", 4)
                                }
                            }
                            putJsonArray("required") {
                                add("cues")
                            }
                        }
                    )
                )
            )
        )

        Timber.d("${completions.choices}")
        val toolCall = completions.choices.firstOrNull()?.message?.toolCalls?.firstOrNull()
        Timber.d("$toolCall")
        val result = toolCall?.let { (it as ToolCall.Function).function.argumentsOrNull }
        Timber.d("$result")

        return result?.let { jsonString ->
            Json.decodeFromString<ExerciseCues>(jsonString).cues
        }
    }
}