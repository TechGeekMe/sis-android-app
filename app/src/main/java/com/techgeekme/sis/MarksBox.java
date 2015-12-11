package com.techgeekme.sis;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by anirudh on 09/12/15.
 */
public class MarksBox extends LinearLayout {

    TextView mTextNumberTextView;
    TextView mTextMarksTextView;
    private String mTitle;

    public MarksBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarksBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMarks(int index, String marks) {
        mTextNumberTextView.setText(mTitle + " " + index);
        mTextMarksTextView.setText(marks);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextNumberTextView = (TextView) findViewById(R.id.marks_title_text_view);
        mTextMarksTextView = (TextView) findViewById(R.id.marks_text_view);
    }
}
