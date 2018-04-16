package com.michaelwilliamjones.capturetheflag.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelwilliamjones.capturetheflag.R;
import com.michaelwilliamjones.capturetheflag.adapters.holders.SentMessagesHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikejones on 4/15/18.
 */

public class MessageListAdapter extends Adapter {
    private Context _context;
    private List<String> _messages;

    public MessageListAdapter(Context context) {
        this._context = context;
        this._messages = new ArrayList<String>();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SentMessagesHolder) holder).bind(this._messages.get(position));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_sent, parent, false);
        return new SentMessagesHolder(view);
    }

    @Override
    public int getItemCount() {
        return this._messages.size();
    }

    public void addMessage(String message) {
        this._messages.add(message);
    }
}
