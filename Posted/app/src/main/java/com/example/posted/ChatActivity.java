package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private ImageButton sendMessageButton;
    private EditText messageInput;
    private RecyclerView mesagesList;

    private String messageRecieverUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitializeFields();
    }

    private void InitializeFields() {
        chatToolBar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);

        sendMessageButton = findViewById(R.id.send_message_button);

        messageInput = findViewById(R.id.text_input_message);
    }
}
