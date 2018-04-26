package com.michaelwilliamjones.capturetheflag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.michaelwilliamjones.capturetheflag.adapters.MyAdapter;
import com.michaelwilliamjones.capturetheflag.websockets.EchoWebSocketListener;
import com.michaelwilliamjones.capturetheflag.websockets.MessageListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MessageListActivity extends AppCompatActivity implements MessageListener{
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;



    private OkHttpClient webSocketClient;
    private WebSocket _webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        // set up the recycler view for incoming messages.
        // use a linear layout manager
        mRecyclerView = findViewById(R.id.my_recycler_view);
        // mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<String> theData = new ArrayList<String>();
        theData.add("this is a test message");
        mAdapter = new MyAdapter(theData);
        mRecyclerView.setAdapter(mAdapter);

        // set up a dingus websocket connection.
        this.webSocketClient = new OkHttpClient();
        Request request = new Request.Builder().url(Constants.SKELETOR_URI + "/" + Constants.WEBSOCKET_ENDPOINT).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        listener.addMessageListener(this);
        _webSocket = webSocketClient.newWebSocket(request, listener);
        this.webSocketClient.dispatcher().executorService().shutdown();

        // send a message.
        _webSocket.send("{\"contents\": {\"message\": \"" + "over the wire" + "\"}}");
    }

    @Override
    protected void onStop() {
        super.onStop();
        _webSocket.close(1000, "Normal Closure");
    }

    public void onMessageReceived(String message) {
        mAdapter.addItem(message);
        mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
    }

    public void onSendClick(View view) {
        // String messageText = ((EditText)findViewById(R.id.edittext_chatbox)).getText().toString();
        // if (messageText != null && !messageText.isEmpty()) {
            // send it over the websocket.
        //     _webSocket.send("{\"contents\": {\"message\": \"" + messageText + "\"}}");
        // }
    }
}
