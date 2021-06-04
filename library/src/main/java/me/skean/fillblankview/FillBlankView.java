package me.skean.fillblankview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Skean on 21/6/4.
 */
public class FillBlankView extends RelativeLayout {

    private TextView tvContent;
    private EditText etInput;
    private SpansManager spansManager;

    public FillBlankView(Context context) {
        this(context, null);
    }

    public FillBlankView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FillBlankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.fbv_layout_fillblankview, this);
        tvContent = findViewById(R.id.fbvTvContent);
        etInput = findViewById(R.id.fbvEtInput);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FillBlankView, defStyleAttr, 0);
        float textSize = a.getDimension(R.styleable.FillBlankView_fbvTextSize, DensityUtils.sp2px(context, 15));
        int contentTextColor = a.getColor(R.styleable.FillBlankView_fbvContentTextColor, Color.BLACK);
        float lineSpacing = a.getFloat(R.styleable.FillBlankView_fbvLineSpacingMultiplier, 1.4f);
        int inputTextColor = a.getColor(R.styleable.FillBlankView_fbvInputTextColor, Color.BLACK);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tvContent.setTextColor(contentTextColor);
        tvContent.setLineSpacing(0, lineSpacing);
        etInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        etInput.setTextColor(inputTextColor);
        a.recycle();

        spansManager = new SpansManager(tvContent, etInput, (v, id, span) -> {
            spansManager.setData(etInput.getText().toString(), null, spansManager.mOldSpan);
            spansManager.mOldSpan = id;
            //如果当前span身上有值，先赋值给et身上
            etInput.setText(TextUtils.isEmpty(span.mText) ? "" : span.mText);
            etInput.setSelection(span.mText.length());
            span.mText = "";
            //通过rf计算出et当前应该显示的位置
            RectF rf = spansManager.drawSpanRect(span);
            //设置EditText填空题中的相对位置
            spansManager.setEtXY(rf);
            spansManager.setSpanChecked(id);
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();

    }



    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    public void setFillText(String fillText) {
        spansManager.doFillBlank(fillText);
    }

    public String getAnswerText() {
        spansManager.setLastCheckedSpanText(etInput.getText().toString());
        return spansManager.getMyAnswer().toString();
    }

}
