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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverGuides.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverGuides#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverGuides extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverGuides() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverGuides.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverGuides newInstance(String param1, String param2) {
        DiscoverGuides fragment = new DiscoverGuides();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        List<ProfileItem> mList = new ArrayList<>();
        mList.add(new ProfileItem(R.drawable.profile_test, 2, "Lady"));
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());


        //set up recycler view with profileAdapter
        RecyclerView recyclerView = getView().findViewById(R.id.rv_profile_list);
        ProfileAdapter profileAdapter = new ProfileAdapter(getActivity(), mList);
        recyclerView.setAdapter(profileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover_guide, container, false);
    }

}
