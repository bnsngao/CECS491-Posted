package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Home.
     */
    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Setup button listeners
        Button btn_locations = (Button) view.findViewById(R.id.button_discover_locations);
        btn_locations.setOnClickListener(this);
        Button btn_guides = (Button) view.findViewById(R.id.button_discover_guides);
        btn_guides.setOnClickListener(this);
        Button btn_chat = (Button) view.findViewById(R.id.button_chat);
        btn_chat.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.button_discover_locations:
                mListener.onFragmentInteraction(Uri.parse(getString(R.string.discover_locations)));
                break;
            case R.id.button_discover_guides:
                mListener.onFragmentInteraction(Uri.parse(getString(R.string.discover_guides)));
                break;
            case R.id.button_chat:
                mListener.onFragmentInteraction(Uri.parse(getString(R.string.chat)));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

}
