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
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class, UserDAO.class, FacesMessage.class, MailBean.class, FacesMessageCreator.class, InvalidDBTransferException.class, FacesContext.class})
public class RegisterUserBeanTest {

    private RegisterUserBean registerUserBean; 
    
    @Mock
    private MailBean mailBean;
    
    @Mock
    FacesContext facesContext;
    
    @Mock
    ExternalContext externalContext;
    
    ArgumentCaptor<String> clientIdCaptor;
    ArgumentCaptor<FacesMessage> facesMessageCaptor;
    
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
        PowerMockito.mockStatic(FacesContext.class);
        
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        
        requestParameterMap = new HashMap<String, String>();
        Mockito.when(externalContext.getRequestParameterMap()).thenReturn(requestParameterMap);
        
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);          
        Mockito.when(Connection.create()).thenReturn(connection);
                
        PowerMockito.mockStatic(UserDAO.class);
        
        correctEmail = "katharina_hoelzl@web.de";
        Mockito.when(UserDAO.emailExists(connection, correctEmail)).thenReturn(false);
        
        emailAlreadyInUse = "fuchs_tobias@web.de";
        Mockito.when(UserDAO.emailExists(connection, emailAlreadyInUse)).thenReturn(true);
        
        correctVeriString = "1234567890";
        Mockito.when(UserDAO.verifyUser(connection, correctVeriString)).thenReturn(true);
        
        wrongVeriString = "0987654321";
        Mockito.when(UserDAO.verifyUser(connection, wrongVeriString)).thenReturn(false);
        
        Mockito.when(UserDAO.createUser(connection, eq(any(User.class)), anyString(), anyString())).thenReturn(this.correctVeriString);
        
        generatedUserID = 1;
        Mockito.when(UserDAO.getUserID(eq(connection), anyString())).thenReturn(generatedUserID);
        
        clientIdCaptor = ArgumentCaptor.forClass(String.class);
        facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
    }
    
    @Test
    public void test() {
        requestParameterMap.put("veri", wrongVeriString);

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
        
        // Test auf AGB
        assertEquals(registerUserBean.registerUser(), "/facelets/open/authenticate.xhtml?faces-redirect=false");        

        // FacesMessage prüfen
        verify(facesContext, times(1)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Bitte AGBs bestätigen!", captured.getSummary());
        
        registerUserBean.setAgbAccepted(true);
        
        // Test auf existierende Email
        assertEquals(registerUserBean.registerUser(), "/facelets/open/authenticate.xhtml?faces-redirect=false");
        
        // FacesMessage prüfen
        verify(facesContext, times(2)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("E-Mail existiert bereits!", captured.getSummary());
        
        user.setEmail(correctEmail);
        
        // Test mit korrekter Email
        assertEquals(registerUserBean.registerUser(), "/facelets/open/index.xhtml?faces-redirect=true");
        
        // FacesMessage prüfen
        verify(facesContext, times(3)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Sie haben sich erfolgreich im System registriert. "
                            + "Bitte bestätigen Sie den Aktivierungslink aus der "
                            + "Verifizierungsmail!", captured.getSummary());
    }

}
