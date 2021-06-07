package me.skean.fillblankview;

import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangminglong on 17/10/19.
 */

public class SpansManager {

    public static final String SPILT_TAG = "_";
    private final String FILL_TAG = "&nbsp;<edit>&nbsp;";//fix bugs -----多个空连续的情况下
    private final String FILL_TAG_NAME = "edit";
    private TextView mTv;
    private EditText mEt;
    protected ArrayList<ReplaceSpan> mSpans;
    public int mOldSpan = -1;
    private RectF mRf;
    private int mFontT; // 字体top
    private int mFontB;// 字体bottom
    private ImmFocus mFocus = new ImmFocus();
    private ReplaceSpan mCheckedSpan;//当前选中的空格
    private ReplaceSpan.OnClickListener onClickListener;

    private float inputTextSize;
    private int inputTextColor;

    private SparseIntArray inputTypeArray = new SparseIntArray();
    private int defaultInputType = InputType.TYPE_CLASS_TEXT;

    public SpansManager(TextView tv,
                        EditText et,
                        float inputTextSize,
                        int inputTextColor,
                        ReplaceSpan.OnClickListener onClickListener) {
        this.mTv = tv;
        this.mEt = et;
        this.onClickListener = onClickListener;
        mSpans = new ArrayList<>();
        this.inputTextSize = inputTextSize;
        this.inputTextColor = inputTextColor;
    }

    public void doFillBlank(List<String> textParts, int blankWidth) {
        String fullText = TextUtils.join(FILL_TAG, textParts);
        mTv.setMovementMethod(Method);

        Spanned spanned = Html.fromHtml(fullText, null, new Html.TagHandler() {
            int index = 0;

            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                if (tag.equalsIgnoreCase(FILL_TAG_NAME) && opening) {
                    TextPaint paint = new TextPaint(mTv.getPaint());
                    paint.setTextSize(inputTextSize);
                    paint.setColor(inputTextColor);
                    ReplaceSpan span = new ReplaceSpan(mTv.getContext(), paint, blankWidth);
                    span.mOnClick = onClickListener;
                    span.mText = "";
                    span.id = index++;
                    mSpans.add(span);
                    output.setSpan(span, output.length() - 1, output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        });

        mTv.setText(spanned);
    }

    /**
     * 设置选中样式
     *
     * @param position
     */
    public void setSpanChecked(int position) {
        mCheckedSpan = mSpans.get(position);
        for (int i = 0; i < mSpans.size(); i++) {
            ReplaceSpan replaceSpan = mSpans.get(i);
            if (i == position) {
                replaceSpan.setDrawTextColorRes(R.color.red);
            }
            else {
                replaceSpan.setDrawTextColor(inputTextColor);
            }
            mTv.invalidate();
        }
    }

    //填充缓存的数据
    public void setData(String str, Object o, int i) {
        if (mTv == null || mSpans == null || mSpans.size() == 0 || i < 0 || i > mSpans.size() - 1) return;
        ReplaceSpan span = mSpans.get(i);
        span.mText = str;
        span.mObject = o;
        mTv.invalidate();
    }

    //TextView触摸事件-->Span点击事件
    private LinkMovementMethod Method = new LinkMovementMethod() {

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ReplaceSpan[] link = buffer.getSpans(off, off, ReplaceSpan.class);

                if (link.length != 0) {
                    //Span的点击事件
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget, buffer, false, x, y, line, off);
                    }
                    else if (action == MotionEvent.ACTION_DOWN) {
                        link[0].onClick(widget, buffer, true, x, y, line, off);
//                        Selection.setSelection(buffer,
//                                buffer.getSpanStart(link[0]),
//                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                }
                else {
//                    Selection.removeSelection(buffer);
                }
            }
            return false;
        }
    };

    //获取出对应Span的RectF数据
    public RectF drawSpanRect(ReplaceSpan s) {

        Layout layout = mTv.getLayout();
        Spannable buffer = (Spannable) mTv.getText();
        int l = buffer.getSpanStart(s);
        int r = buffer.getSpanEnd(s);
        int line = layout.getLineForOffset(l);
        int l2 = layout.getLineForOffset(r);
        if (mRf == null) {
            mRf = new RectF();
            //Rect rt = new Rect();
            Paint.FontMetrics fontMetrics = mTv.getPaint().getFontMetrics();

            //mTv.getPaint().getTextBounds("TgQyYFJ",0,7,rt);
            mFontT = (int) fontMetrics.ascent;
            mFontB = (int) fontMetrics.descent;
        }
        mRf.left = layout.getPrimaryHorizontal(l);
        mRf.right = layout.getSecondaryHorizontal(r);
        // 通过基线去校准
        line = layout.getLineBaseline(line);
        mRf.top = line + mFontT;
        mRf.bottom = line + mFontB;
        return mRf;
    }

    //设置EditText填空题中的相对位置
    public void setEtXY(RectF rf, int id) {
        //设置et w,h的值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mEt.getLayoutParams();
        lp.width = (int) (rf.right - rf.left);
        lp.height = (int) (rf.bottom - rf.top);
        //设置et 相对于tv x,y的相对位置
        lp.leftMargin = (int) (mTv.getLeft() + rf.left);
        lp.topMargin = (int) (mTv.getTop() + rf.top);
        mEt.setLayoutParams(lp);
        mEt.setInputType(inputTypeArray.get(id, defaultInputType));
        //获取焦点，弹出软键盘
        mEt.setFocusable(true);
        mEt.requestFocus();
        showImm(true, mEt);
    }

    /**
     * 弹出软键盘
     *
     * @param bOn
     * @param focus
     */
    private void showImm(boolean bOn, View focus) {
        try {
            if (bOn) {
                if (focus != null) {
                    ImmFocus.show(true, focus);
                }
                else {
                    mFocus.setFocus(focus);
                }
            }
            else {
                ImmFocus.show(false, null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> getMyAnswers() {
        List<String> myAnswerList = new ArrayList<>();
        for (int i = 0; i < mSpans.size(); i++) {
            myAnswerList.add(mSpans.get(i).mText);
        }
        return myAnswerList;
    }

    public ReplaceSpan getCheckedReplaceSpan() {
        return mCheckedSpan;
    }

    public void setLastCheckedSpanText(String editText) {
        if (mCheckedSpan != null) {
            mCheckedSpan.mText = editText;
        }
    }

    public void setInputType(int defaultInputType, SparseIntArray inputTypeArray){
        this.defaultInputType =defaultInputType;
        this.inputTypeArray = inputTypeArray;
    }
}
