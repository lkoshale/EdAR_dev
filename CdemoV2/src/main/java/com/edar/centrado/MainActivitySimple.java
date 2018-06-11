package com.edar.centrado;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivitySimple extends Activity {

    private Button button;
    private TextView name_text;
    private TextView email_text;
    private Button signOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_simple);
        signOut = (Button)findViewById(R.id.sign_out);

        name_text = (TextView)findViewById(R.id.username);
        email_text = (TextView)findViewById(R.id.useremail);

        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatbot = new Intent(MainActivitySimple.this,ChatbotActivity.class);
                startActivity(chatbot);
            }
        });

        setTextViews();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(MainActivitySimple.this,LoginActivity.class);
                startActivity(login);
            }
        });

    }

    void setTextViews(){
        // set nav header
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        if(mauth!=null) {
            FirebaseUser user = mauth.getCurrentUser();
            if(user!=null) {
                String name = "NAME :"+user.getDisplayName();
                String email = "EMAIL :"+user.getEmail();
                name_text.setText(name);
                email_text.setText(email);
            }
            else
                Log.e("Main","User is null");
        }
        else
            Log.e("Main","mauth is NULL");
    }

}
