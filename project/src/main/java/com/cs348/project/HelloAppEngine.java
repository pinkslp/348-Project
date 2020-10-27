package com.cs348.project;

import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.util.Properties;
import java.util.Date;
import java.util.stream.Collectors;
import org.jsoup.*;
import org.jsoup.safety.Whitelist;
import java.text.SimpleDateFormat;  
// import org.jsoup.helper.*;
// import org.jsoup.nodes.*;
// import org.jsoup.select.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.text.ParseException;



import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import java.sql.Connection;
import java.util.*;

@WebServlet(name = "HelloAppEngine", value = "/hello")
public class HelloAppEngine extends HttpServlet {

  Connection conn; // Cloud SQL connection

  @Override
  public void init() throws ServletException {
    try {
      String url = System.getProperty("cloudsql");

      try {
        conn = DriverManager.getConnection(url);
      } catch (SQLException e) {
        throw new ServletException("Unable to connect to SQL server", e);
      }

    } finally {
      // Nothing really to do here.
    }
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Properties properties = System.getProperties();

    response.setContentType("text/plain");
    response.getWriter().println("Hello App Engine - Standard using "
        + SystemProperty.version.get() + " Java " + properties.get("java.specification.version"));
  }

  public static String getInfo() {
    return "Version: " + System.getProperty("java.version")
          + " OS: " + System.getProperty("os.name")
          + " User: " + System.getProperty("user.name");
  }
  
  //---------------------------------------------------------------------------------------------------------

  // Post creation query
final String createPostSql =
    "INSERT INTO PERSON (p_id, name, dob, vehicle) VALUES (?, ?, ?, ?)";

@Override
public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

  //Create a map of the httpParameters that we want and run it through jSoup
  System.out.println("Code reaches here");
  class Person {
    public int p_id;
    public String name;
    public Date dob;
    public String vehicle;

    public Person(int id_, String name_, Date dob_, String vehicle_){
      p_id = id_;
      name = name_;
      dob_ = dob_;
      vehicle = vehicle_;
    }
  }
  Date dob_conv;
  try{
    dob_conv = new SimpleDateFormat("dd/MM/yyyy").parse(req.getParameter("person_dob"));
  }catch(ParseException e){
    throw new ServletException(e);
  }
  
  Person person = new Person(Integer.parseInt(req.getParameter("p_id")), req.getParameter("person_name"),dob_conv,req.getParameter("person_vehicle"));
  // Map<Integer, Person> person =
  //     req.getParameterMap()
  //         .entrySet()
  //         .stream()
  //         .filter(a -> a.getKey().startsWith("person_"));
          // .collect(
          //     Collectors.toMap(
          //         p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));
  //List <Integer, String, Date, String> person = new ArrayList();
  // Build the SQL command to insert the blog post into the database
  try (PreparedStatement statementCreatePerson = conn.prepareStatement(createPostSql)) {
    // set the author to the user ID from the user table
    statementCreatePerson.setInt(1, person.p_id);
    //statementCreatePerson.setTimestamp(2, new Timestamp(new Date().getTime()));
    statementCreatePerson.setString(2, person.name);
    java.sql.Date sqlDate = new java.sql.Date(dob_conv.getTime());
    statementCreatePerson.setDate(3, sqlDate);
    statementCreatePerson.setString(4, person.vehicle);
    statementCreatePerson.executeUpdate();

    conn.close(); // close the connection to the Cloud SQL server

    // Send the user to the confirmation page with personalised confirmation text
    String confirmation = "Person with name " + person.name + " created.";

    req.setAttribute("confirmation", confirmation);
    //req.getRequestDispatcher("/confirm.jsp").forward(req, resp);

  } catch (SQLException e) {
    throw new ServletException("SQL error when creating post", e);
  }
}
}
