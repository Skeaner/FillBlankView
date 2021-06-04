package me.skean.fillblankviewdemo;

import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import me.skean.fillblankview.SpansManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String mTestStr = "我是个________学生,我有一个梦想，我要成为像____，____一样的人.";
    private TextView mTvContent;
    private EditText mEtInput;
    private SpansManager mSpansManager;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = (TextView) findViewById(R.id.fbvTvContent);
        mEtInput = (EditText) findViewById(R.id.fbvEtInput);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        mSpansManager = new SpansManager(mTvContent, mEtInput, (v, id, span) -> {
            mSpansManager.setData(mEtInput.getText().toString(),null, mSpansManager.mOldSpan);
            mSpansManager.mOldSpan = id;
            //如果当前span身上有值，先赋值给et身上
            mEtInput.setText(TextUtils.isEmpty(span.mText)?"":span.mText);
            mEtInput.setSelection(span.mText.length());
            span.mText = "";
            //通过rf计算出et当前应该显示的位置
            RectF rf = mSpansManager.drawSpanRect(span);
            //设置EditText填空题中的相对位置
            mSpansManager.setEtXY(rf);
            mSpansManager.setSpanChecked(id);
        });
        mSpansManager.doFillBlank(mTestStr);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            Toast.makeText(this, getMyAnswerStr(), Toast.LENGTH_LONG).show();
        }
    }


    private String getMyAnswerStr(){
        mSpansManager.setLastCheckedSpanText(mEtInput.getText().toString());
        return mSpansManager.getMyAnswers().toString();
    }
}
