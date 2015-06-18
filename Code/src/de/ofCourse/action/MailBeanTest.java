/**
 * 
 */
package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import de.ofCourse.model.SmtpServer;
import de.ofCourse.system.Connection;
import de.ofCourse.utilities.PropertyManager;

/**
 * @author Sebastian Schwarz
 *
 */
public class MailBeanTest {

    private MailBean mailbean;
    private SmtpServer server;
    @Mock
    private PropertyManager manager = new PropertyManager();

    
    private Connection connection;


    @Before
    public void setup() {
        server = new SmtpServer();
        server.setHostaddr("smtp.gmail.com");
        server.setPassword("testmail");
        server.setPort(465);
        server.setSsl(true);
        server.setUsername("sep2015team13@gmail.com");
        
        PowerMockito.mockStatic(PropertyManager.class);
        manager = mock(PropertyManager.class);          
        Mockito.when(PropertyManager.getInstance()).thenReturn(manager);
        
        


    }

    @Test
    public void test() {


        mailbean = new MailBean();
        mailbean.setSmtpServer(server);

        mailbean.sendSingleMail("sebastian@nrschwarz.de", "MailbeanTest", "Hallo");

    }

}
