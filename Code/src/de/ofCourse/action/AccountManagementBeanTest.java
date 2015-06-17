package de.ofCourse.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.system.Connection;
import de.ofCourse.model.User;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class, UserDAO.class, PaginationData.class, FacesMessage.class, FacesMessageCreator.class, InvalidDBTransferException.class, FacesContext.class})
public class AccountManagementBeanTest {
    
    // Neue Testbean anlegen.
    private AccountManagementBean accountManagementBean;
    
    // Benötigte Attribute der Testbean anlegen.
    private PaginationData pagination;
    private List<User> notAdminActivatedUsers;
    private Connection connection;
    
    // FacesContext und ExternalContext hermocken.
    @Mock
    FacesContext facesContext;
    
    @Mock
    ExternalContext externalContext;
    
    // Captor für die FacesMessages erstellen.
    ArgumentCaptor<String> clientIdCaptor;
    ArgumentCaptor<FacesMessage> facesMessageCaptor;
    private FacesMessage captured;
    
    // RequestParameterMap erstellen (wird hier aber nicht benötigt)
    private Map<String, String> requestParameterMap;
    
    /**
     * Vorbereitung für den Test.
     */
    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        // FacesContext statisch mocken.
        PowerMockito.mockStatic(FacesContext.class);
        
        // Angeben, was zurückgegeben werden soll, wenn nach der Instanz des FacesContext oder ExternalContext gefragt wird.
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        
        // RequestParameterMap erstellen (wird hier nicht benötigt)
        requestParameterMap = new HashMap<String, String>();
        Mockito.when(externalContext.getRequestParameterMap()).thenReturn(requestParameterMap);
        
        // Connection Klasse statisch mocken.
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);          
        Mockito.when(Connection.create()).thenReturn(connection);
                
        // Datenbankklasse statisch mocken.
        PowerMockito.mockStatic(UserDAO.class);
        
        // Festlegen, was die Datenbankmethoden bei bestimmten Aufrufen zurückgeben sollen.
        Mockito.when(UserDAO.AdminActivateUsers(eq(connection), anyObject())).thenReturn(true);

        Mockito.when(UserDAO.getNumberOfNotAdminActivatedUsers(connection)).thenReturn(2);

        // Rückgabeliste initialisieren und belegen.
        notAdminActivatedUsers = new ArrayList<User>();
        
        User user1 = new User();
        user1.setUserId(1);
        user1.setUsername("Kathi");
        user1.setFirstname("Kathi");
        user1.setLastname("Hölzl");
        user1.setEmail("katharina_hoelzl@web.de");
        user1.setDateOfBirth(new Date(1993, 5, 29));
        notAdminActivatedUsers.add(user1);
        
        User user2 = new User();
        user2.setUserId(2);
        user2.setUsername("Tobi");
        user2.setFirstname("Tobias");
        user2.setLastname("Fuchs");
        user2.setEmail("tobias.fuchs@web.de");
        user2.setDateOfBirth(new Date(1993, 12, 24));  
        notAdminActivatedUsers.add(user2);
        
        pagination = new PaginationData();
        pagination.setCurrentPageNumber(0);
        pagination.setElementsPerPage(10);
        pagination.setNumberOfPages(1);
        pagination.setSortAsc(true);
        pagination.setSortColumn("nickname");
        
        Mockito.when(UserDAO.getNotAdminActivatedUsers(connection, pagination)).thenReturn(notAdminActivatedUsers);
        
        // Captor für die FacesMessages initialisieren.
        clientIdCaptor = ArgumentCaptor.forClass(String.class);
        facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
    }
    
    /**
     * Eigentliche Testmethode.
     */
    @Test
    public void test() {
        // Neue Testbean initialisieren.
        accountManagementBean = new AccountManagementBean();
        
        // Benötigte Attribute setzen.
        DataModel<User> dataModelUserList = new ListDataModel<User>();
        dataModelUserList.setWrappedData(notAdminActivatedUsers);
        
        accountManagementBean.setUsers(dataModelUserList);
        accountManagementBean.setPagination(pagination);
        
        // Überprüfen, ob diese richtig gesetzt wurden.
        assertEquals(dataModelUserList, accountManagementBean.getUsers());
        assertEquals(pagination, accountManagementBean.getPagination());
        
        // Zu testende Methode aufrufen.
        accountManagementBean.activateAccounts();
        
        // FacesMessage prüfen
        verify(facesContext, times(1)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Keine Benutzer ausgewählt!", captured.getSummary());
        
        // Benutzer auswählen.
        notAdminActivatedUsers.get(0).setSelected(true);
        notAdminActivatedUsers.get(1).setSelected(true);
        dataModelUserList.setWrappedData(notAdminActivatedUsers);
        
        // Attribute erneut setzen.
        accountManagementBean.setUsers(dataModelUserList);
                
        // Zu testende Methode erneut aufrufen.
        accountManagementBean.activateAccounts();
        
        // FacesMessage prüfen
        verify(facesContext, times(2)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Benutzer erfolgreich aktiviert!", captured.getSummary());

    }
}
