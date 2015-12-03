package com.techgeekme.sis;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anirudh on 29/11/15.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    public String courseName;
    public String courseCode;
    public String courseType;
    public String credits;
    public String attendancePercent;
    public String classesAttended;
    public String classesHeld;
    public ArrayList<String> tests = new ArrayList<String>();
}