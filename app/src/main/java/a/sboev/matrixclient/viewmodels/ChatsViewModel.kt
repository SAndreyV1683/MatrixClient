package a.sboev.matrixclient.viewmodels


import a.sboev.matrixclient.AndroidApp
import a.sboev.matrixclient.RoomHeader
import a.sboev.matrixclient.isRoot
import a.sboev.matrixclient.isSpace
import a.sboev.matrixclient.nameFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.room
import net.folivo.trixnity.client.room.flatten
import net.folivo.trixnity.client.store.Room
import net.folivo.trixnity.client.store.sender
import net.folivo.trixnity.client.user
import net.folivo.trixnity.core.model.events.m.room.MemberEventContent
import net.folivo.trixnity.core.model.events.m.room.RoomMessageEventContent

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModel(val client: MatrixClient): ViewModel() {

    private val chatsState = MutableStateFlow(listOf<RoomHeader>())
    val chats: StateFlow<List<RoomHeader>> get() = chatsState

    init {

        val roomService = client.room
        val allHeaders = roomService.getAll()
            .flatten()
            .flatMapLatest { rooms ->
                val headers = rooms.map { it.headerFlow() }
                combine(headers) { it.asList() }
            }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

        val chatsFlow = allHeaders.map { headers ->
            headers
                .filter { !it.isSpace && roomService.isRoot(it.id) }
                .sortedByDescending { it.lastMessageDate }

        }
        viewModelScope.launch {
            client.room.getAll().collect() {
                chatsFlow.collect { chatsState.value = it }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = AndroidApp.INSTANCE
                ChatsViewModel(client = application.client)
            }
        }
    }



    private fun Room.headerFlow(): Flow<RoomHeader> {
        val initFlow = flow {
            emit(
                RoomHeader(
                    id = roomId,
                    title = "",
                    lastMessageText = "",
                    lastMessageDate = lastRelevantEventTimestamp ?: Instant.fromEpochMilliseconds(0),
                    unreadCount = unreadMessageCount,
                    avatarUrl = avatarUrl,
                    isSpace = client.room.isSpace(roomId)
                )
            )
        }
        val nameFlow = nameFlow(client)

        class LastMsg(val date: Instant, val userName: String, val text: String)

        val eventFlow = client.room.getLastTimelineEvent(roomId).filterNotNull().flatMapLatest { it }.filterNotNull()
        val user = eventFlow.flatMapLatest { client.user.getById(roomId, it.sender) }
        val date = eventFlow.map { Instant.fromEpochMilliseconds(it.event.originTimestamp) }
        val message = eventFlow.map { timelineEvent ->
            when (val roomEventContent = timelineEvent.content?.getOrNull()) {
                is RoomMessageEventContent.TextMessageEventContent -> roomEventContent.body
                is RoomMessageEventContent.FileMessageEventContent -> "[file]"
                is RoomMessageEventContent.ImageMessageEventContent -> "[image]"
                is MemberEventContent -> "[${roomEventContent.membership.name}]"
                null -> timelineEvent.event::class.simpleName
                else -> roomEventContent::class.simpleName
            }
        }
        val lastMsgFlow = combine(user, message, date) { u, m, d -> LastMsg(d, u?.name.orEmpty(), m.orEmpty()) }

        return combine(initFlow, nameFlow, lastMsgFlow) { header, name, msg ->
            header.copy(title = name, lastMessageText = "${msg.userName}: ${msg.text}", lastMessageDate = msg.date)
        }
    }
}