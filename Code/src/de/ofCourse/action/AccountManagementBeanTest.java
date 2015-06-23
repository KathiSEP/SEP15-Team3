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

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.system.Connection;
import de.ofCourse.model.User;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class, UserDAO.class, PaginationData.class, FacesMessage.class, FacesMessageCreator.class, InvalidDBTransferException.class, FacesContext.class})
public class AccountManagementBeanTest {
    
    // Create new Bean for testing
    private AccountManagementBean accountManagementBean;
    
    // Create necessary attributes of the test bean
    private PaginationData pagination;
    private List<User> notAdminActivatedUsers;
    private Connection connection;
    
    // Mock FacesContext and ExternalContext .
    @Mock
    FacesContext facesContext;
    
    @Mock
    ExternalContext externalContext;
    
    // Create captor for the FacesMessages.
    ArgumentCaptor<String> clientIdCaptor;
    ArgumentCaptor<FacesMessage> facesMessageCaptor;
    private FacesMessage captured;
    
    // Create RequestParameterMap
    private Map<String, String> requestParameterMap;
    
    /**
     * Preparations for the test
     */
    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        // Mock FacesContext statically .
        PowerMockito.mockStatic(FacesContext.class);
        
        // Specify what should be returned if it's ask for the instance of the
        // FacesContext or the ExternalContext.
        Mockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        
        // Create RequestParameterMap.
        requestParameterMap = new HashMap<String, String>();
        Mockito.when(externalContext.getRequestParameterMap()).thenReturn(requestParameterMap);
        
        // Mock the Connection class statically.
        PowerMockito.mockStatic(Connection.class);
        connection = mock(Connection.class);          
        Mockito.when(Connection.create()).thenReturn(connection);
                
        // Moch the database class statically.
        PowerMockito.mockStatic(UserDAO.class);
        
        // Specify what the methods of the database should return by specific requests.
        Mockito.when(UserDAO.AdminActivateUsers(eq(connection), anyObject())).thenReturn(true);

        Mockito.when(UserDAO.getNumberOfNotAdminActivatedUsers(connection)).thenReturn(2);

        // Initialize and fill the return list.
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
        pagination.setSortDirection(SortDirection.ASC);
        pagination.setSortColumn(SortColumn.NICKNAME);
        
        Mockito.when(UserDAO.getNotAdminActivatedUsers(connection, pagination)).thenReturn(notAdminActivatedUsers);
        
        // Initialize the captor for the FacesMessages.
        clientIdCaptor = ArgumentCaptor.forClass(String.class);
        facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
    }
    
    /**
     * Eigentliche Testmethode.
     */
    @Test
    public void test() {
        // Initialize new test bean.
        accountManagementBean = new AccountManagementBean();
        
        // Set necessary attributes.
        DataModel<User> dataModelUserList = new ListDataModel<User>();
        dataModelUserList.setWrappedData(notAdminActivatedUsers);
        
        accountManagementBean.setUsers(dataModelUserList);
        accountManagementBean.setPagination(pagination);
        
        // Check if the attribute setting was correct.
        assertEquals(dataModelUserList, accountManagementBean.getUsers());
        assertEquals(pagination, accountManagementBean.getPagination());
        
        // Call the method for testing.
        accountManagementBean.activateAccounts();
        
        // Check FacesMessage.
        verify(facesContext, times(1)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Keine Benutzer ausgewählt!", captured.getSummary());
        
        // Select user.
        notAdminActivatedUsers.get(0).setSelected(true);
        notAdminActivatedUsers.get(1).setSelected(true);
        dataModelUserList.setWrappedData(notAdminActivatedUsers);
        
        // Set attributes new.
        accountManagementBean.setUsers(dataModelUserList);
                
        // Call the method for testing again.
        accountManagementBean.activateAccounts();
        
        // Check FacesMessage.
        verify(facesContext, times(2)).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());
        assertNull(clientIdCaptor.getValue());
        captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("Benutzer erfolgreich aktiviert!", captured.getSummary());

    }
}
