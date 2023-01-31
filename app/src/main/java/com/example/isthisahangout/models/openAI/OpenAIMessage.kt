package com.example.isthisahangout.models.openAI

data class OpenAIMessageDto(
    val id: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val pfp: String? = null,
    val message: String? = null,
    val time: Long = 0,
)


data class OpenAIMessage(
    val id: String,
    val userId: String,
    val userName: String,
    val pfp: String,
    val message: String,
    val time: Long,
)

fun OpenAIMessageDto.toOpenAIMessage(): OpenAIMessage =
    OpenAIMessage(
        id = id ?: "NA",
        userId = userId ?: "NA",
        userName = userName ?: "NA",
        pfp = pfp ?: "NA",
        message = message ?: "NA",
        time = time
    )

fun OpenAIMessage.toOpenAIMessageDto(): OpenAIMessageDto =
    OpenAIMessageDto(
        id = id,
        userId = userId,
        userName = userName,
        pfp = pfp,
        message = message,
        time = time
    )