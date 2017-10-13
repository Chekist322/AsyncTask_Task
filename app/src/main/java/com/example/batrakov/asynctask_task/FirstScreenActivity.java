package com.example.batrakov.asynctask_task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Provide access to second screen.
 */
public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.first_screen);
        Button gotoSecondScreen = (Button) findViewById(R.id.gotoSecondScreen);
        gotoSecondScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                startSecondScreenActivity();
            }
        });
    }

    /**
     * Start {@link SecondScreenActivity}.
     */
    private void startSecondScreenActivity() {
        Intent intent = new Intent(this, SecondScreenActivity.class);
        startActivity(intent);
    }
}
