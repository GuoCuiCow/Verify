package com.mana.verify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.BindView;
import com.mana.butterknife.MyButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_hello)
    TextView hello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButterKnife.bind(this);
        hello.setText("success");
    }
}
