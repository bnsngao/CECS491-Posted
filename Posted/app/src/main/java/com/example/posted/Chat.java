package com.example.posted;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageButton sendMessageButton;
    private EditText messageInput;

    private RecyclerView userMessagesList;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    // TODO: change from hardcoding sending messages to test account to connecting two users
    private String messageReceiverID; //"HN7Ah7ShXGTu69oq3rakXBYzk4a2";
    private String messageReceiverName;
    private String messageSenderID;

    private ImageView rateButton;

    // Create variables for time info storage within the database
    private String currDate, currTime;

    // FirebaseAuth for getting userIDs
    private FirebaseAuth mAuth;

    // Create a root reference to the database for this instance
    private DatabaseReference rootReference;

    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up mAuth
        mAuth = FirebaseAuth.getInstance();

        // Get current user ID for message sending
        messageSenderID = mAuth.getCurrentUser().getUid();

        // Create connection to database reference for this instance
        rootReference = FirebaseDatabase.getInstance().getReference();

        if (getArguments() != null) {
            messageReceiverID = getArguments().getString(ARG_PARAM1);
        }

        FetchMessages();
    }

    private void FetchMessages() {
        rootReference.child("Messages").child(messageSenderID).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Messages m = dataSnapshot.getValue(Messages.class);
                    messageList.add(m);
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage() {
        // Get message text from EditText box
        String messageText = messageInput.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            // Warn user to enter a message if the textbox is empty
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
        } else {
            // Create Database References
            String messageSenderReference = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverReference = "Messages/" + messageReceiverID + "/" + messageSenderID;

            // Create unique key for each message sent to the receiver
            DatabaseReference userMessageKeyReference = rootReference.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            //Toast.makeText(getContext(), "test3", Toast.LENGTH_SHORT);

            // Save unique key as a reference
            String messagePushID = userMessageKeyReference.getKey();

            // Get current Date to save info into database
            Calendar calDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            String saveCurrDate = currentDate.format(calDate.getTime());

            // Get current Time to save info into database
            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat currTime = new SimpleDateFormat("hh:mm aa");
            String saveCurrTime = currTime.format(calTime.getTime());

            // Create hashmaps for database references
            Map messageBody = new HashMap();
            messageBody.put("message", messageText);
            messageBody.put("time", saveCurrTime);
            messageBody.put("date", saveCurrDate);
            messageBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderReference + "/" + messagePushID, messageBody);
            messageBodyDetails.put(messageReceiverReference + "/" + messagePushID, messageBody);

            // Save the message to the database

            rootReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        Toast.makeText(getContext(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();

                        // clear the user message
                        messageInput.setText("");
                    } else {
                        // message did not send successfully
                        // display error message
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                        // clear the user message
                        messageInput.setText("");
                    }

                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Setup button listener
        sendMessageButton = view.findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(this);

//        View view2 = inflater.inflate(R.layout.activity_main, container, false);
        rateButton = getActivity().findViewById(R.id.rateImageButton);
        rateButton.setVisibility(View.VISIBLE);
        rateButton.setOnClickListener(this);

        // Setup EditText listener
        messageInput = view.findViewById(R.id.text_input_message);

        //
        messagesAdapter = new MessagesAdapter(messageList);
        userMessagesList = (RecyclerView) view.findViewById(R.id.messages_list_users);

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);


        return view;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.send_message_button:
                // Call SendMessage function
                SendMessage();
                break;
        }
        if (v==rateButton){
            startActivity(new Intent(getContext(),Rate.class));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}