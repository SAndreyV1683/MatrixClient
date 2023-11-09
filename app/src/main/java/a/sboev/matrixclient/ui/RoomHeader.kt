package a.sboev.matrixclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.room
import net.folivo.trixnity.client.store.sender
import net.folivo.trixnity.client.user
import net.folivo.trixnity.core.model.RoomId
import net.folivo.trixnity.core.model.events.m.room.MemberEventContent
import net.folivo.trixnity.core.model.events.m.room.RoomMessageEventContent

data class RoomHeader(
    val id: RoomId,
    val title: String,
    var lastMessageText: String,
    val lastMessageDate: Instant,
    val unreadCount: Long,
    val avatarUrl: String?,
    val isSpace: Boolean
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun Render(modifier: Modifier = Modifier, client: MatrixClient) {
        var lastMessage by remember { mutableStateOf("") }

        LaunchedEffect(lastMessage) {
            val eventFlow = client.room.getLastTimelineEvent(id).filterNotNull().flatMapLatest { it }.filterNotNull()
            val user = eventFlow.flatMapLatest { client.user.getById(id, it.sender) }
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

            val resultMsg = message.combine(user){ m, u -> "${u?.name}: $m"}
            resultMsg.collect {
                lastMessage = it
            }
        }
        if (!isSpace) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp),
                    client,
                    id.full,
                    avatarUrl,
                    title
                )
                Column(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            style = MaterialTheme.typography.bodySmall,
                            text = lastMessageDate.toText()
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = lastMessage,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (unreadCount > 0) Badge {
                            Text(if (unreadCount < 100) unreadCount.toString() else "99+")
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .size(24.dp),
                    client,
                    id.full,
                    avatarUrl,
                    title,
                    RoundedCornerShape(20)
                )
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}