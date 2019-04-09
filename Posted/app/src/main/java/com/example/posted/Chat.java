package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageButton sendMessageButton;
    private EditText messageInput;

    private RecyclerView userMessagesList;

    // TODO: change from hardcoding sending messages to test account to connecting two users
    private String messageReceiverID = "HN7Ah7ShXGTu69oq3rakXBYzk4a2";
    private String messageReceiverName;
    private String messageSenderID;

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
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1, String param2) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-mm-yy");
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
                    //Toast.makeText(getContext(), "asdf", Toast.LENGTH_SHORT);
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        Toast.makeText(getContext(), "Message Sent Successfully", Toast.LENGTH_SHORT);
                    } else {
                        // message did not send successfully
                        // display error message
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT);
                    }

                    // clear the user message
                    messageInput.setText("");

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

        // Setup EditText listener
        messageInput = view.findViewById(R.id.text_input_message);

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
