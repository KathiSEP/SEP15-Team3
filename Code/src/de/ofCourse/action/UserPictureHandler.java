package de.ofCourse.action;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.model.Course;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * @author Sebastian Schwarz
 *
 */

public class UserPictureHandler extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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

            if (req.getParameter("profilImage") != null) {
                String pictureBelongsToUserID = req.getParameter("profilImage");
                int userID = Integer.parseInt(pictureBelongsToUserID);
                byte[] userPicture = UserDAO.getImage(trans, userID);


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
            // TODO Error page
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
        
        //TODO Anpassen per facletcontext
        resp.sendRedirect("http://localhost:8003/OfCourse/resources/img/userdata/userphoto.jpg");
    }

}
