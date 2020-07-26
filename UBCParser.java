import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class UBCParser {

  static String URL = "https://courses.students.ubc.ca/cs/courseschedule?tname=subj-all-departments&sessyr=2020&sesscd=W&pname=subjarea";

  //key is course acronym
  //list[0] is the full course name, list[1] is the link
  Map<String,List<String>> coursesByAlphabetMap;
  int indexForFullCourseName = 0;
  int indexForLink = 1;
  Map<String,List<String>> coursesByNumberMap;

  static String wantedSectionL = "Lecture";
  static String wantedSectionWOC = "Web-Oriented Course";



  public UBCParser(){

    coursesByAlphabetMap = new HashMap();
    coursesByNumberMap = new HashMap();
    getCoursesByAlphabet();
    getCoursesByNumber("CPSC");

  }

  //return true if it was success else false
  public boolean getCoursesByAlphabet() {

    Response response = null;
    Document doc = null;
    try {
      // Try to connect to the URL 
      response = Jsoup.connect(URL).execute(); 

      // Check if the connect was successful 
      if (200 == response.statusCode()) {
        //System.out.println ("success");
        doc = Jsoup.connect(URL).get();
      }
      else {
        throw new IOException("Connection Failed");
      }

      // Get elements from html 
      Elements courses = doc.select("tbody > tr > td > a,b");
      Elements coursesFullName = doc.select("tbody > tr > td:eq(1)");

      // Check if the getting elements was successful
      if (courses.isEmpty() || coursesFullName.isEmpty()){
        throw new IOException("Getting Courses Elements Failed");
      }

      int numberOfCourses = courses.size();
      int numberOfcoursesFullName  = coursesFullName.size();
      int min = (numberOfCourses > numberOfcoursesFullName)? numberOfcoursesFullName : numberOfCourses;

      // in the coursesByAlphabetMap, put the course acronym as key, and in the value list, put the full name and the link 
      for (int i = 0; i < min; i++) {
        List<String> courseInfo = new ArrayList<>();
        courseInfo.add(coursesFullName.get(i).text());
        courseInfo.add(courses.get(i).attr("abs:href"));
        coursesByAlphabetMap.put(courses.get(i).text(),courseInfo);
      }

    } catch(IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  //return true if it was success else false
  public boolean getCoursesByNumber(String courseName) {

    Response response = null;
    Document doc = null;
    String courseURL = null;

    try {

      //check if there is the course that algorithm looking for
      if (coursesByAlphabetMap.get(courseName) == null) {
        return false;
      }

      //get URL
      courseURL = coursesByAlphabetMap.get(courseName).get(indexForLink);
      
      // Try to connect to the courseURL 
      response = Jsoup.connect(courseURL).execute(); 

      // Check if the connect was successful 
      if (200 == response.statusCode()) {
        //System.out.println ("success");
        doc = Jsoup.connect(courseURL).get();
      }
      else {
        throw new IOException("Connection Failed");
      }

      // Get elements from html 
      Elements courses = doc.select("tbody > tr > td > a,b");
      Elements coursesFullName = doc.select("tbody > tr > td:eq(1)");

      // Check if the getting elements was successful
      if (courses.isEmpty() || coursesFullName.isEmpty()){
        throw new IOException("Getting Courses Elements Failed");
      }

      int numberOfCourses = courses.size();
      int numberOfcoursesFullName  = coursesFullName.size();
      int min = (numberOfCourses > numberOfcoursesFullName)? numberOfcoursesFullName : numberOfCourses;

      // in the coursesMap, put the course acronym as key, and in the value list, put the full name and the link 
      for (int i = 0; i < min; i++) {
        List<String> courseInfo = new ArrayList<>();
        courseInfo.add(coursesFullName.get(i).text());
        courseInfo.add(courses.get(i).attr("abs:href"));
        coursesByNumberMap.put(courses.get(i).text(),courseInfo);
      }

    return true;

    } catch(IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean findCourse(String courseName) {

    Response response = null;
    Document doc = null;
    String courseURL = null;

    try {

      //check if there is the course that algorithm looking for
      if (coursesByNumberMap.get(courseName) == null) {
        return false;
      }

      //get URL
      courseURL = coursesByNumberMap.get(courseName).get(indexForLink);
      
      // Try to connect to the courseURL 
      response = Jsoup.connect(courseURL).execute(); 

      // Check if the connect was successful 
      if (200 == response.statusCode()) {
        //System.out.println ("success");
        doc = Jsoup.connect(courseURL).get();
      }
      else {
        throw new IOException("Connection Failed");
      }

      // Get elements from html 
      Elements sections = doc.select("tbody > tr");

      // Check if the getting elements was successful
      if (sections.isEmpty()){
        throw new IOException("Getting Courses Elements Failed");
      }

      // in the coursesMap, put the course sections as key, and in the value list, put the term, days, start time and end time 
      String sectionActivity = null;
      for (element section : sections) {
        sectionActivity = section.select("tbody > tr > td:eq(2)").text();
        if (sectionActivity.equals(wantedSectionL) || sectionActivity.equals(wantedSectionWOC)) {
          
        }

      }

    return true;

    } catch(IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}