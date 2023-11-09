package a.sboev.matrixclient.viewmodels


import a.sboev.matrixclient.MatrixApp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.core.model.RoomId


class ChatsViewModel(val client: MatrixClient): ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = MatrixApp.INSTANCE
                ChatsViewModel(client = application.client)
            }
        }
        val TAG = ChatsViewModel::class.simpleName
    }

    fun setReadMarkers(roomId: RoomId) {
        viewModelScope.launch {
            //
        }
    }




}