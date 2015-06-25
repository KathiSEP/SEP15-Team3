package de.ofCourse.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * This Servlet jumps in if a request for a picture was done. Then it search in the Database
 * the picture and returns it or loads the Dummy Picture if nothing is found in the Database
 * 
 * @author Sebastian Schwarz
 *
 */

public class UserPictureHandler extends HttpServlet {

    /**
     * serial ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructer uses the Constructor from the parent class
     */
    public UserPictureHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        Transaction trans = Connection.create();
        trans.start();
        try {
        	
        	//Checks whether it is a profilImage or CourseImage
            if (req.getParameter("profilImage") != null) {
                String pictureBelongsToUserID = req.getParameter("profilImage");
                int userID = Integer.parseInt(pictureBelongsToUserID);
                byte[] userPicture = UserDAO.getImage(trans, userID);

                //Loads the DummyPicture because no Image in the Database
                if (userPicture == null) {
                    
                    dummypicture(resp);
                    LogHandler.getInstance().debug("No User Picture found, "
                            + "load DummyPicture");
                    
                } else {
                    resp.reset();
                    resp.setContentType("image/jpg");
                    resp.setContentLength(userPicture.length);
                    resp.getOutputStream().write(userPicture);                    
                    LogHandler.getInstance().debug(
                            "User Picture succesfully loaded");
                }
                trans.commit();

            } else if (req.getParameter("courseImage") != null) {

                String pictureBelongsToCourseID = req
                        .getParameter("courseImage");                
                int courseID = Integer.parseInt(pictureBelongsToCourseID);
                byte[] courseImage = CourseDAO.getImage(trans, courseID);
                
                if (courseImage == null) {
                    
                    dummypicture(resp);
                    LogHandler.getInstance().debug("No Course Picture found, "
                            + "load DummyPicture");
                    
                } else {
                    
                    resp.reset();
                    resp.setContentType("image/jpg");
                    resp.setContentLength(courseImage.length);
                    resp.getOutputStream().write(courseImage);
                    
                    LogHandler.getInstance().debug(
                            "Course Picture succesfully loaded");
                }
                trans.commit();
            } 
        } catch (Exception e) {
            LogHandler.getInstance().error(
                    "HTTPServlet funktioniert nicht: getUser");
            trans.rollback();
        }

    }

    /**
     * Sets the Path of the Dummy picture which is stored on the Server
     * 
     * @param resp
     * @throws IOException
     */
    private void dummypicture(HttpServletResponse resp) throws IOException {      
        resp.reset();
        resp.setContentType("image/jpg");
        resp.sendRedirect("http://localhost:8003/OfCourse/resources/img/userdata/userphoto.jpg");
    }

}
