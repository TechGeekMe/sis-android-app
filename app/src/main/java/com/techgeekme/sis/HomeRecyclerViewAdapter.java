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
        holder.creditsTextView.setText("Credits: " + currentCourse.credits);
        holder.attendanceFractonTextView.setText(currentCourse.classesAttended + " / " + currentCourse.classesHeld);
        holder.attendancePercentTextView.setText(currentCourse.attendancePercent + "%");
        holder.courseCodeTextView.setText(currentCourse.courseCode);
        holder.testsLinearLayout.setMarks(currentCourse.tests);
        holder.assignmentLinearLayout.setMarks(currentCourse.assignments);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public static class CourseCardViewHolder extends RecyclerView.ViewHolder {
        public TextView courseNameTextView;
        public TextView creditsTextView;
        public TextView attendancePercentTextView;
        public TextView courseCodeTextView;
        public MarksLinearLayout testsLinearLayout;
        public MarksLinearLayout assignmentLinearLayout;
        public TextView attendanceFractonTextView;
        public CourseCardViewHolder(View subjectCardView) {
            super(subjectCardView);
            courseNameTextView = (TextView) subjectCardView.findViewById(R.id.course_name_text_view);
            creditsTextView = (TextView) subjectCardView.findViewById(R.id.credits_text_view);
            attendancePercentTextView = (TextView) subjectCardView.findViewById(R.id.attendance_percent_text_view);
            attendanceFractonTextView = (TextView) subjectCardView.findViewById(R.id.attendance_fraction_text_view);
            courseCodeTextView = (TextView) subjectCardView.findViewById(R.id.course_code_text_view);
            testsLinearLayout = (MarksLinearLayout) subjectCardView.findViewById(R.id.tests_linear_layout);
            assignmentLinearLayout = (MarksLinearLayout) subjectCardView.findViewById(R.id.assignments_linear_layout);
            testsLinearLayout.setTitle("Test");
            assignmentLinearLayout.setTitle("Assignment");
        }
    }

}
