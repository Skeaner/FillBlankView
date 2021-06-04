package me.skean.fillblankviewdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.skean.fillblankview.FillBlankView;

/**
 * Created by Skean on 21/6/4.
 */
public class DemoActivity extends AppCompatActivity {

    private FillBlankView fillBlankView;

    private String mTestStr = "我是个________学生,我有一个梦想，我要成为像____，____一样的人.";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        fillBlankView = findViewById(R.id.fbvTest);
        fillBlankView.setFillText(mTestStr);
        findViewById(R.id.btn_submit).setOnClickListener(view -> {
            Toast.makeText(this, fillBlankView.getPlainText(), Toast.LENGTH_LONG).show();
        });
    }
}
