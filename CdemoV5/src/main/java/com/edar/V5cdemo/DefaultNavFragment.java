package com.edar.V5cdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class DefaultNavFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.default_nav_fragment, container, false);
        Button demo = (Button)view.findViewById(R.id.button_demo);
        Button chatbot = view.findViewById(R.id.chatbot);

        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UnityPlayerActivity.class);
                startActivity(intent);
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ChatbotActivity.class);
                startActivity(intent);
            }
        });

//        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),ChatbotActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

}
