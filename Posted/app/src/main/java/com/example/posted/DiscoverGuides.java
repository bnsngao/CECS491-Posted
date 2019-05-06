package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverGuides#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverGuides extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public DiscoverGuides() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DiscoverGuides.
     */
    public static DiscoverGuides newInstance() {
        DiscoverGuides fragment = new DiscoverGuides();
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
        View view = inflater.inflate(R.layout.fragment_discover_guide, container, false);

        // Setup button listeners
        Button btn_guide = (Button) view.findViewById(R.id.button_guide);
        btn_guide.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.button_guide:
                mListener.onFragmentInteraction(Uri.parse(getString(R.string.guide)));
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
