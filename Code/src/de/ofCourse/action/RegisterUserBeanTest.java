package de.ofCourse.action;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.Language;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.utilities.LanguageManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class, UserDAO.class, FacesMessage.class, 
                        MailBean.class, FacesMessageCreator.class, 
                        InvalidDBTransferException.class, FacesContext.class,
                        LanguageManager.class})

public class RegisterUserBeanTest {

    // Create new Bean for testing
    private RegisterUserBean registerUserBean; 
    
    private SessionUserBean sessionUser;
    
    private LanguageManager languageManager;
    
    // Mock FacesContext and ExternalContext and MailBean.
    @Mock
    private MailBean mailBean;
    
    @Mock
    FacesContext facesContext;
    
    @Mock
    ExternalContext externalContext;
    
    // Create captor for the FacesMessages.
    ArgumentCaptor<String> clientIdCaptor;
    ArgumentCaptor<FacesMessage> facesMessageCaptor;
    
    // Create RequestParameterMap
    private Map<String, String> requestParameterMap;
    
    private Connection connection;
    private FacesMessage captured;
    
    private String correctEmail;
    private String emailAlreadyInUse;
    private String correctVeriString;
    private String wrongVeriString;
    
    private int generatedUserID;
    
    @Before
    public void setup() {
        // Mock FacesContext statically .
        PowerMockito.mockStatic(FacesContext.class);
        
        // Specify what should be returned if it's ask for the instance of the
        // FacesContext or the ExternalContext.
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(
                                                                 facesContext);

        Mockito.when(facesContext.getExternalContext()).thenReturn(
                                                              externalContext);
        
        // Create RequestParameterMap.
        requestParameterMap = new HashMap<String, String>();
        Mockito.when(externalContext.getRequestParameterMap()).thenReturn(
                                                          requestParameterMap);
        
        // Mock the Connection class statically.
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);          
        Mockito.when(Connection.create()).thenReturn(connection);
        
        sessionUser = new SessionUserBean();
        sessionUser.setLanguage(Language.DE);
                
        //Mock LanguageManagerCreator
        PowerMockito.mockStatic(LanguageManager.class);
        languageManager = mock(LanguageManager.class);
 
        Mockito.when(LanguageManager.getInstance()).thenReturn(languageManager);
        
        Mockito.when(LanguageManager.getInstance().
                getProperty("registerUserBean.facesMessage.EmailExisting", 
                                                                Language.DE)).
                thenReturn("E-Mail existiert bereits!");
        
        Mockito.when(LanguageManager.getInstance().
                getProperty("registerUserBean.facesMessage."
                                                    + "SuccessfulRegistration", 
                                                                Language.DE)).
                thenReturn("Sie haben sich erfolgreich im System registriert. "
                            + "Bitte bestätigen Sie den Aktivierungslink aus der "
                            + "Verifizierungsmail!");
        
        Mockito.when(LanguageManager.getInstance().
                getProperty("registerUserBean.facesMessage.AGB", Language.DE)).
                thenReturn("Bitte AGBs bestätigen!");
         
        // Mock the database class statically.
        PowerMockito.mockStatic(UserDAO.class);
        
        // Specify what the methods of the database should return by specific 
        // requests.
        correctEmail = "katharina_hoelzl@web.de";
        Mockito.when(UserDAO.emailExists(connection, correctEmail)).
                                                            thenReturn(false);
        
        emailAlreadyInUse = "fuchs_tobias@web.de";
        Mockito.when(UserDAO.emailExists(connection, emailAlreadyInUse)).
                                                            thenReturn(true);
        
        correctVeriString = "1234567890";
        Mockito.when(UserDAO.verifyUser(connection, correctVeriString)).
                                                            thenReturn(true);
        
        wrongVeriString = "0987654321";
        Mockito.when(UserDAO.verifyUser(connection, wrongVeriString)).
                                                            thenReturn(false);
        
        Mockito.when(UserDAO.createUser(connection, eq(any(User.class)), 
                  anyString(), anyString())).thenReturn(this.correctVeriString);
        
        generatedUserID = 1;
        Mockito.when(UserDAO.getUserID(eq(connection), anyString())).
                                                   thenReturn(generatedUserID);
        
        // Initialize the captor for the FacesMessages.
        clientIdCaptor = ArgumentCaptor.forClass(String.class);
        facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
    }
    
    /**
     * Actual test method
     */
    @Test
    public void test() {
        requestParameterMap.put("veri", wrongVeriString);

        // Initialize new test bean.
        registerUserBean = new RegisterUserBean();
      
        requestParameterMap.clear();
        requestParameterMap.put("veri", correctVeriString);        
        
        registerUserBean = new RegisterUserBean();
        
        
       
        registerUserBean.setMailBean(mailBean);
               
        User user = new User();
        user.setFirstname("Katharina");
        user.setLastname("Hölzl");
        user.setEmail(emailAlreadyInUse);
        user.setUsername("Kathi1");
        
        Address address = new Address();
        address.setZipCode(94081);
        address.setCity("Fürstenzell");
        address.setCountry("Deutschland");
        
        user.setAddress(address);
        
        registerUserBean.setSaluString("ms.");
        registerUserBean.setRegisterPassword("Test123#");
        registerUserBean.setRegisterConfirmPassword("Test123#");       
        
        registerUserBean.setUserToRegistrate(user);
        
        registerUserBean.setSessionUser(sessionUser);
        
        // Check AGB
        assertEquals(registerUserBean.registerUser(), 
                    "/facelets/open/authenticate.xhtml?faces-redirect=false");        

        // Check FacesMessage 
        verify(facesContext, times(1)).addMessage(clientIdCaptor.capture(), 
                                                facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Bitte AGBs bestätigen!", captured.getSummary());
        
        registerUserBean.setAgbAccepted(true);
        
        // Check if mail exists
        assertEquals(registerUserBean.registerUser(), 
                     "/facelets/open/authenticate.xhtml?faces-redirect=false");
        
        // Check FacesMessage 
        verify(facesContext, times(2)).addMessage(clientIdCaptor.capture(), 
                                                 facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("E-Mail existiert bereits!", captured.getSummary());
        
        user.setEmail(correctEmail);
        
        // Test with correct mail
        assertEquals(registerUserBean.registerUser(), 
                              "/facelets/open/index.xhtml?faces-redirect=true");
        
        // Check FacesMessage
        verify(facesContext, times(3)).addMessage(clientIdCaptor.capture(), 
                                                  facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Sie haben sich erfolgreich im System registriert. "
                            + "Bitte bestätigen Sie den Aktivierungslink aus der "
                            + "Verifizierungsmail!", captured.getSummary());
    }

}
