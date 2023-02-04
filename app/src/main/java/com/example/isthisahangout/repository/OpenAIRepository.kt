package com.example.isthisahangout.repository

import com.deimos.openaiapi.OpenAI
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.openAI.OpenAIMessage
import com.example.isthisahangout.models.openAI.OpenAIMessageDto
import com.example.isthisahangout.models.openAI.toOpenAIMessage
import com.example.isthisahangout.models.openAI.toOpenAIMessageDto
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.asResourceFlow
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

const val OPEN_AI_PFP =
    "https://beebom.com/wp-content/uploads/2022/12/cool-things-do-with-chatgpt-featured.jpg?w=750&quality=75"
const val OPEN_AI_USER_ID = "open_ai"

@Singleton
class OpenAIRepository @Inject constructor(
    private val openAI: OpenAI,
    @Named("OpenAIRef")
    private val openAIRef: CollectionReference,
) {
    suspend fun getResponse(userMessage: String, aiUserName: String = "Jarvis"): Resource<Unit> {
        return try {
            var prompt =
                "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly."
            prompt += "\n\nHuman: $userMessage \nAI:"
            sendMessage(
                userId = MainActivity.userId,
                message = prompt,
                isUser = true,
                aiUserName = aiUserName
            )
            val response = openAI.createCompletion(
                model = "text-davinci-003",
                prompt = prompt,
                temperature = 0.9,
                max_tokens = 150,
                top_p = 1,
                frequency_penalty = 0.0,
                presence_penalty = 0.6,
                stop = listOf(" Human:", " AI:")
            )
            if (response.isSuccessful) {
                if (response.body() == null) {
                    Resource.Error(throwable = OpenAIException(msg = "Aw snap an error occurred"))
                } else {
                    val answer = response.body()!!.choices.first().text.trim()
                    sendMessage(
                        userId = MainActivity.userId,
                        message = answer,
                        isUser = false,
                        aiUserName = aiUserName
                    )
                    Resource.Success(Unit)
                }
            } else {
                Resource.Error(throwable = OpenAIException(msg = response.message()))
            }
        } catch (exception: Exception) {
            Resource.Error(throwable = exception)
        }
    }

    private suspend fun sendMessage(
        userId: String,
        message: String,
        isUser: Boolean,
        aiUserName: String,
    ) {
        val id = openAIRef.document(userId).collection("messages").id
        val openAIMessage = OpenAIMessage(
            id = id,
            userId = if (isUser) userId else OPEN_AI_USER_ID,
            userName = if (isUser) MainActivity.userName else aiUserName,
            pfp = if (isUser) MainActivity.userPfp else OPEN_AI_PFP,
            message = message,
            time = System.currentTimeMillis()
        )
        openAIRef.document(userId).collection("messages").document(openAIMessage.id)
            .set(openAIMessage.toOpenAIMessageDto()).await()
    }

    fun getOpenAIMessages(userId: String): Flow<Resource<List<OpenAIMessage?>>> =
        openAIRef.document(userId).collection("messages").asResourceFlow { querySnapshot ->
            val documents = querySnapshot.documents
            documents.map { it.toObject(OpenAIMessageDto::class.java) }
                .map {
                    it?.toOpenAIMessage()
                }
        }

    data class OpenAIException(val msg: String): Exception(msg)
}