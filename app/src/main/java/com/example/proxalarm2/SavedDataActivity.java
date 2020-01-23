package com.example.proxalarm2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class SavedDataActivity extends AppCompatActivity {
    
    Button backButton;
    Button deleteButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);
        
        backButton = findViewById(R.id.back_button);
        deleteButton = findViewById(R.id.delete_button);
        
        
        //back and delete buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //intent to change from current to new activity
                Intent intent = new Intent(SavedDataActivity.this, MainActivity.class);
                startActivity(intent);
                
                
            }
        });
        
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Simple Button 2", Toast.LENGTH_LONG).show();//display the text of button2
            }
        });
        
    }
}
