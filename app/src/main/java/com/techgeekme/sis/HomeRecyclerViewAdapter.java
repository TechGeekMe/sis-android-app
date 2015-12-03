package com.techgeekme.sis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anirudh on 03/12/15.
 */
public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.CourseCardViewHolder> {
    private ArrayList<Course> mCourses;

    public HomeRecyclerViewAdapter(ArrayList<Course> courses) {
        mCourses = courses;
    }
    @Override
    public CourseCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card, parent, false);
        CourseCardViewHolder vh = new CourseCardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CourseCardViewHolder holder, int position) {
        Course currentCourse = mCourses.get(position);
        holder.courseNameTextView.setText(currentCourse.courseName);
        holder.creditsTextView.setText(currentCourse.credits);
        holder.classesAttendedTextView.setText(currentCourse.classesAttended);
        holder.classesHeldTextView.setText(currentCourse.classesHeld);
        holder.attendancePercentTextView.setText(currentCourse.attendancePercent);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public static class CourseCardViewHolder extends RecyclerView.ViewHolder {
        public TextView courseNameTextView;
        public TextView creditsTextView;
        public TextView classesAttendedTextView;
        public TextView classesHeldTextView;
        public TextView attendancePercentTextView;
        public CourseCardViewHolder(View subjectCardView) {
            super(subjectCardView);
            courseNameTextView = (TextView) subjectCardView.findViewById(R.id.courseNameTextView);
            creditsTextView = (TextView) subjectCardView.findViewById(R.id.creditsTextView);
            attendancePercentTextView = (TextView) subjectCardView.findViewById(R.id.attendancePercentTextView);
            classesAttendedTextView = (TextView) subjectCardView.findViewById(R.id.classesAttendedTextView);
            classesHeldTextView = (TextView) subjectCardView.findViewById(R.id.classesHeldTextView);
        }
    }

}
