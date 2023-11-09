package a.sboev.matrixclient.ui

import a.sboev.matrixclient.service.SessionManager
import a.sboev.matrixclient.viewmodels.ChatsViewModel
import a.sboev.matrixclient.viewmodels.HomeViewScreenModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import net.folivo.trixnity.core.model.RoomId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatsScreen: KoinComponent, Screen {

    private val sessionManager by inject<SessionManager>()

    @Composable
    override fun Content() {
        val chatsViewModel: ChatsViewModel = viewModel(factory = ChatsViewModel.Factory)
        var selectedRoomId: RoomId? by remember { mutableStateOf(null) }
        var roomNavigator: Navigator? by remember { mutableStateOf(null) }
        val homeView = remember {
            movableContentOf {
                ChatsScreenCompose(
                    selectedRoomId = selectedRoomId,
                    onRoomClick = { roomId ->
                        if (selectedRoomId != roomId) {
                            roomNavigator?.popUntilRoot()
                            roomNavigator?.push(RoomScreen(roomId))
                            chatsViewModel.setReadMarkers(roomId)
                        }
                    }
                )
            }
        }
        val roomContainer = remember {
            movableContentOf {
                Navigator(EmptyRoomScreen) {
                    roomNavigator = it
                    selectedRoomId = (it.lastItem as? RoomScreen)?.id?.let { RoomId(it) }
                    CurrentScreen()
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            homeView()
            roomContainer()
        }
    }

    @Composable
    fun ChatsScreenCompose(
        selectedRoomId: RoomId?,
        onRoomClick: (RoomId) -> Unit
    ) {
        val client = sessionManager.getClient()
        val screenModel = rememberScreenModel { HomeViewScreenModel(client) }
        val syncState by client.syncState.collectAsState()
        var showChats by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                Surface(shadowElevation = 3.dp) {
                    HomeTopBar(sessionManager, syncState)
                }
            },
            content = { innerPadding ->
                val chats by screenModel.chats.collectAsState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                ) {
                    itemsIndexed(
                        chats
                    ) { _, item ->

                        val isSelected = selectedRoomId == item.id
                        item.Render(
                            modifier = Modifier
                                .clickable { onRoomClick(item.id) }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                ),
                            client = screenModel.client
                        )
                    }
                }
            },
            bottomBar = {
                SmallNavigationBar {
                    NavigationBarItem(
                        selected = showChats,
                        onClick = { showChats = true },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = null
                            )
                        },
                        label = null
                    )
                    NavigationBarItem(
                        selected = !showChats,
                        onClick = { showChats = false },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Outlined.FolderCopy,
                                contentDescription = null
                            )
                        },
                        label = null
                    )
                }
            }
        )
    }

}