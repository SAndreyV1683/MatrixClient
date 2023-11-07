package a.sboev.matrixclient.ui


import a.sboev.matrixclient.nameFlow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.core.model.RoomId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


internal val LocalPhoneMode = compositionLocalOf { mutableStateOf(false) }
object EmptyRoomScreen : Screen {

    @Composable
    override fun Content() {
        val phoneMode by LocalPhoneMode.current
        if (phoneMode) {
            Spacer(modifier = Modifier.fillMaxSize())
        } else {
            Text(
                modifier = Modifier.fillMaxSize().wrapContentHeight(),
                text = "Select chat",
                textAlign = TextAlign.Center
            )
        }
    }
}

class RoomScreen(val id: String) : Screen, KoinComponent {
    constructor(roomId: RoomId) : this(roomId.full)

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val client by inject<MatrixClient>()
        val roomId = RoomId(id)
        Scaffold(
            topBar = {
                Surface(shadowElevation = 3.dp) {
                    RoomTopBar(roomId, client)
                }
            },
            content = {
                Timeline(roomId, client, it)
            },
            bottomBar = {
                NewMessageWidget(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .windowInsetsPadding(WindowInsets.ime),
                    client = client,
                    roomId = roomId
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RoomTopBar(id: RoomId, client: MatrixClient) {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        TopAppBar(
            title = {
                val name by id.nameFlow(client).collectAsState("")
                Text(
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                val phoneMode by LocalPhoneMode.current
                if (phoneMode) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch {
                            client.api.rooms.leaveRoom(id, null, null)
                            navigator.pop()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.PersonAddDisabled, contentDescription = null)
                }
            }
        )
    }

    @Composable
    private fun Timeline(
        roomId: RoomId,
        client: MatrixClient,
        paddingValues: PaddingValues
    ) {
        val scope = rememberCoroutineScope()
        val screenModel = rememberScreenModel { RoomScreenModel(roomId, client) }
        val timelineItems by screenModel.items.collectAsState()
        val isLoadingBefore by screenModel.isLoadingBefore.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            contentPadding = paddingValues,
            reverseLayout = true,
        ) {
            itemsIndexed(
                timelineItems,
                key = { index, element -> if (index == 0) index else element.id }
            ) { index, element ->
                if (index == timelineItems.size - 1) {
                    scope.launch {
                        Napier.d("loadBefore [${roomId.full}]")
                        screenModel.loadBefore()
                    }
                }
                element.render(client)
            }
            item {
                if (isLoadingBefore) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(8.dp).align(Alignment.Center).size(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}