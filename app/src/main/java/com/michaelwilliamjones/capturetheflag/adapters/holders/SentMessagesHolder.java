package com.michaelwilliamjones.capturetheflag.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelwilliamjones.capturetheflag.R;

/**
 * Created by mikejones on 4/15/18.
 */

public class SentMessagesHolder extends RecyclerView.ViewHolder {
    TextView messageText;

    public SentMessagesHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
    }

    public void bind(String message) {
        messageText.setText(message);
    }
}

