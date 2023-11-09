package a.sboev.matrixclient.viewmodels

import a.sboev.matrixclient.isSpace
import a.sboev.matrixclient.ui.RoomHeader
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.room
import net.folivo.trixnity.client.room.flatten

class HomeViewScreenModel(
    val client: MatrixClient
) : ScreenModel {
    private val chatsState = MutableStateFlow(listOf<RoomHeader>())
    val chats: StateFlow<List<RoomHeader>> get() = chatsState
    private val catalogState = MutableStateFlow(listOf<RoomHeader>())
    val catalog: StateFlow<List<RoomHeader>> get() = catalogState

    init {
        val roomService = client.room
        coroutineScope.launch {
            roomService.getAll().flatten().collect {
                val roomHeadersList = arrayListOf<RoomHeader>()
                for (room in it) {
                    val roomHeader = RoomHeader(
                        id = room.roomId,
                        title = room.name?.explicitName.toString(),
                        lastMessageText = "",
                        lastMessageDate = room.lastRelevantEventTimestamp ?: Instant.fromEpochMilliseconds(0),
                        unreadCount = room.unreadMessageCount,
                        avatarUrl = room.avatarUrl,
                        isSpace = client.room.isSpace(room.roomId)
                    )
                    roomHeadersList.add(roomHeader)
                }
                chatsState.value = roomHeadersList
            }
        }
    }
}