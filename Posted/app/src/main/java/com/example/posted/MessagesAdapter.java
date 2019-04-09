package com.example.posted;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    // list to hold all of the messages within the context
    // of this specific chat between users
    private List<Messages> userMessageList;

    // Auth to get
    private FirebaseAuth mAuth;

    private DatabaseReference userDatabaseReference;

    public MessagesAdapter (List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        // Text views to hold the text sender and receiver messages
        public TextView senderMessageText, receiverMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text_body);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text_body);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        String messageSenderID = mAuth.getCurrentUser().getUid();

        Messages messages = userMessageList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessage = messages.getMessage();

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(v);
    }
}
