package de.ofCourse.action;




import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.UserDAO;
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
        
        
        System.out.println("ich bin im servlet");
        Transaction trans = Connection.create();
        trans.start();
        try {
            
            if(req.getParameter("profilImage") != null){
                String pictureBelongsToUserID = req.getParameter("profilImage");
                int userID = Integer.parseInt(pictureBelongsToUserID);
                User user = UserDAO.getUser(trans, userID);
                resp.reset();
                resp.setContentType("image/jpg");
                resp.setContentLength(user.getProfilImage().length);
                resp.getOutputStream().write(user.getProfilImage());
            
            } else if(req.getParameter("courseImage") != null){
                
                String pictureBelongsToCourseID = req.getParameter("courseImage");
                int courseID = Integer.parseInt(pictureBelongsToCourseID);
                Course course = CourseDAO.getCourse(trans, courseID);
                resp.reset();
                resp.setContentType("image/jpg");
                resp.setContentLength(course.getCourseImage().length);
                resp.getOutputStream().write(course.getCourseImage());
            }
            
            
            
            
//            InputStream picture = new ByteArrayInputStream(
//                    user.getProfilImage());
            
            
           
            
            
//            BufferedInputStream input = null;
//            BufferedOutputStream output = null;
//
//            try {
//                input = new BufferedInputStream(picture);
//                output = new BufferedOutputStream(resp.getOutputStream());
//
//                byte[] buffer = new byte[8192];
//                int length;
//                while ((length = input.read(buffer)) > 0) {
//                    output.write(buffer, 0, length);
//                }
//                trans.commit();
//            } finally {
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (Exception e) {
//                        LogHandler.getInstance().error(
//                                "HTTPServlet funktioniert nicht: output.close");
//                    }
//                }
//
//                if (input != null) {
//                    try {
//                        input.close();
//                    } catch (Exception e) {
//                        LogHandler.getInstance().error(
//                                "HTTPServlet funktioniert nicht: input.close");
//                    }
//                }
//            }

        } catch (Exception e) {
            //TODO Error page 
            LogHandler.getInstance().error(
                    "HTTPServlet funktioniert nicht: getUser");
            trans.rollback();
        }

    }

}
