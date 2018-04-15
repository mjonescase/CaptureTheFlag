package com.michaelwilliamjones.capturetheflag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.michaelwilliamjones.captureflag.R;
import com.michaelwilliamjones.capturetheflag.adapters.MessageListAdapter;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView _messageRecycler;
    private MessageListAdapter _messageAdapter;

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
    }
}
