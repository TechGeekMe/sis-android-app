package com.techgeekme.sis;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;

import java.util.ArrayList;

/**
 * Created by anirudh on 09/12/15.
 */
public class MarksLinearLayout extends LinearLayout {
    private ArrayList<MarksBox> mTestRows;
    private String mTitle;

    public MarksLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarksLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMarks(ArrayList<String> marks) {
        if (marks.isEmpty()) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
        if (mTestRows == null) {
            mTestRows = new ArrayList<>(marks.size());
            for (int i = 0; i < marks.size(); i++) {
                addMarksBoxandSpace();
            }
        } else if (mTestRows.size() < marks.size()) {
            int additions = marks.size() - mTestRows.size();
            for (int i = 0; i < additions; i++) {
                addMarksBoxandSpace();
            }
        } else if (mTestRows.size() > marks.size()) {
            int removals = mTestRows.size() - marks.size();
            mTestRows.subList(0, removals).clear();
            removeViews(1, removals * 2);
        }
        for (int i = 0; i < marks.size(); i++) {
            mTestRows.get(i).setMarks(i + 1, marks.get(i));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addSpace();
    }

    private void addSpace() {
        Space space = new Space(getContext());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        space.setLayoutParams(layoutParams);
        addView(space);
    }

    private void addMarksBoxandSpace() {
        MarksBox testRow = (MarksBox) LayoutInflater.from(getContext()).inflate(R.layout.marks_box, this, false);
        testRow.setTitle(mTitle);
        mTestRows.add(testRow);
        addView(testRow);
        addSpace();
    }
}


