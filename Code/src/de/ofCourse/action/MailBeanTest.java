/**
 * 
 */
package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;








import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.Mock;
import org.mockito.Mockito;





import de.ofCourse.model.User;
import de.ofCourse.system.LogHandler;
import de.ofCourse.utilities.PropertyManager;

/**
 * @author Sebastian Schwarz
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertyManager.class, Integer.class, LogHandler.class })
public class MailBeanTest {

	/**
     * Logger
     */
    private LogHandler logger;
    
    private User userToSendMail;
    private PropertyManager manager;
    
    private MailBean bean = new MailBean();
    private String message = "Hallo";
    private String subject = "MailBeanTest";
    
    List<String> recipients = new ArrayList<String>();
    
    @Mock
    private Integer test;
    
    
    
    @Before
    public void setup(){
    	// mocks the lgger
    	PowerMockito.mockStatic(LogHandler.class);
    	logger = mock(LogHandler.class);
    	Mockito.when(LogHandler.getInstance()).thenReturn(logger);
    	
        recipients.add("sebastian@nrschwarz.de");
        
        PowerMockito.mockStatic(PropertyManager.class);
        manager = mock(PropertyManager.class);
        Mockito.when(PropertyManager.getInstance()).thenReturn(manager);
        

        
       // Mockito.when(PropertyManager.getInstance().getPropertyMail("smtphost")).thenReturn("smtp.gmail.com");
       // Mockito.when(Integer.parseInt(anyString())).thenReturn(465);
    }
    
    
    
    /**
     * Test method for {@link de.ofCourse.action.MailBean#sendConfirmationMessage(int)}.
     */
    @Test
    public void testSendConfirmationMessage() {
        bean.sendMail(recipients, subject, message);
    }

}
