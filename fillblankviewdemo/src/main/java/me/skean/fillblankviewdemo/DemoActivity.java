package me.skean.fillblankviewdemo;

import android.os.Bundle;
import android.text.InputType;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.skean.fillblankview.FillBlankView;

/**
 * Created by Skean on 21/6/4.
 */
public class DemoActivity extends AppCompatActivity {

    private FillBlankView fillBlankView;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        fillBlankView = findViewById(R.id.fbvTest);
        fillBlankView.setFillText(Arrays.asList("我是个", "学生,我有一个梦想，我要成为像", "，", "一样的人."));
        SparseIntArray inputTypeArray = new SparseIntArray();
        inputTypeArray.put(0, InputType.TYPE_CLASS_TEXT);
        fillBlankView.setInputType(InputType.TYPE_CLASS_NUMBER, inputTypeArray);
        findViewById(R.id.btn_submit).setOnClickListener(view -> {
            Toast.makeText(this, fillBlankView.getPlainText(), Toast.LENGTH_LONG).show();
        });
    }
}
