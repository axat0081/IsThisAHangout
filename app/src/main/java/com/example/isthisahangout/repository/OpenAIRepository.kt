package com.example.isthisahangout.repository

import com.deimos.openaiapi.OpenAI
import com.example.isthisahangout.models.openAI.OpenAIMessage
import com.example.isthisahangout.models.openAI.OpenAIMessageDto
import com.example.isthisahangout.models.openAI.toOpenAIMessage
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.asResourceFlow
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

const val OPEN_AI_PFP =
    "https://beebom.com/wp-content/uploads/2022/12/cool-things-do-with-chatgpt-featured.jpg?w=750&quality=75"

@Singleton
class OpenAIRepository @Inject constructor(
    private val openAI: OpenAI,
    @Named("OpenAIRef")
    private val openAIRef: CollectionReference,
) {

    suspend fun getResponse(prompt: String, aiUserName: String): Resource<OpenAIMessage> {
        return try {
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
                    Resource.Error(throwable = Exception(message = "Aw snap an error occurred"))
                } else {
                    val answer = response.body()!!.choices.first().text.trim()
                    val id = openAIRef.document().id
                    val message = OpenAIMessageDto(
                        id = id,
                        userId = "OPEN_AI",
                        userName = aiUserName,
                        pfp = OPEN_AI_PFP,
                        message = answer,
                        time = 0
                    )
                    Resource.Success(data = message.toOpenAIMessage())
                }
            } else {
                Resource.Error(throwable = Exception(message = response.message()))
            }
        } catch (exception: Exception) {
            Resource.Error(throwable = exception)
        }
    }

    suspend fun sendMessage(userId: String, openAIMessage: OpenAIMessage): Resource<Unit> {
        return try {
            openAIRef.document(userId).collection("messages").document(openAIMessage.id)
                .set(openAIMessage)
            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Error(throwable = exception)
        }
    }

    fun getOpenAIMessages(userId: String): Flow<Resource<List<OpenAIMessageDto?>>> =
        openAIRef.document(userId).collection("messages").asResourceFlow { querySnapshot ->
            val documents = querySnapshot.documents
            documents.map { it.toObject(OpenAIMessageDto::class.java) }
        }
}