package com.michaelwilliamjones.capturetheflag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.michaelwilliamjones.capturetheflag.adapters.MessageListAdapter;
import com.michaelwilliamjones.capturetheflag.adapters.holders.SentMessagesHolder;
import com.michaelwilliamjones.capturetheflag.websockets.EchoWebSocketListener;
import com.michaelwilliamjones.capturetheflag.websockets.MessageListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MessageListActivity extends AppCompatActivity implements MessageListener{
    private RecyclerView _messageRecycler;
    private MessageListAdapter _messageAdapter;
    private OkHttpClient webSocketClient;
    private WebSocket _webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _messageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        _messageAdapter = new MessageListAdapter(this);
        _messageRecycler.setLayoutManager(new LinearLayoutManager(this));

        // set up a dingus websocket connection.
        this.webSocketClient = new OkHttpClient();
        Request request = new Request.Builder().url(Constants.SKELETOR_URI + "/" + Constants.WEBSOCKET_ENDPOINT).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        listener.addMessageListener(this);
        _webSocket = webSocketClient.newWebSocket(request, listener);
        this.webSocketClient.dispatcher().executorService().shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _webSocket.close(1000, "Normal Closure");
    }

    public void onMessageReceived(String message) {
        this._messageAdapter.addMessage(message);
        SentMessagesHolder holder = (SentMessagesHolder) _messageAdapter.createViewHolder((ViewGroup)
                findViewById(R.id.message_list_view_group), 0);
        this._messageAdapter.bindViewHolder(holder,
                this._messageAdapter.getItemCount() - 1);
    }

    public void onSendClick(View view) {
        String messageText = ((EditText)findViewById(R.id.edittext_chatbox)).getText().toString();
        if (messageText != null && !messageText.isEmpty()) {
            // send it over the websocket.
            _webSocket.send("{\"contents\": {\"message\": \"" + messageText + "\"}}");
        }
    }
}
