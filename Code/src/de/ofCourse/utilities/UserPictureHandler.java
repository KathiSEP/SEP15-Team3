/**
 * 
 */
package de.ofCourse.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ofCourse.Database.dao.UserDAO;
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
    
    public UserPictureHandler(){
        super();
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        Transaction trans = Connection.create();
        trans.start();
        
        int pictureBelongsToUserID = Integer.parseInt(req.getPathInfo().substring(1));
        
        User user = UserDAO.getUser(trans, pictureBelongsToUserID);
        InputStream picture = new ByteArrayInputStream(user.getProfilImage());
        
        resp.setHeader("Content Typ", getServletContext().getMimeType(user.getUsername()));
        resp.setHeader("Content-Length", "" + user.getProfilImage().length);
        resp.setHeader("Content-Disposition", "inline; filename=\"" + user.getUsername() + "\"");
    
    
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
    
        try{
            input = new BufferedInputStream(picture);
            output = new BufferedOutputStream(resp.getOutputStream());
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) > 0){
                output.write(buffer, 0, length);
            }
        } finally {
            if(output !=null){
                try{
                    output.close();
                } catch(Exception e){
                    LogHandler.getInstance().error("HTTPServlet funktioniert nicht: output.close");
                }
            }
                
            if(input != null){
                try{
                    input.close();
                } catch(Exception e){
                    LogHandler.getInstance().error("HTTPServlet funktioniert nicht: input.close");
                }
            }
        }
    
    
    
    }
    
    
    

}
