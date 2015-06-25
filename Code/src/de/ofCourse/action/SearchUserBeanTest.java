/**
 * 
 */
package de.ofCourse.action;


import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;




import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;



import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * @author Sebastian Schwarz
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Transaction.class, Connection.class, UserDAO.class,
        PaginationData.class })
public class SearchUserBeanTest {

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    
    private ResultSet resultSet = null;

    // RequestparameterMap
    private Map<String, String> pm;

    private Connection conn;

    private java.sql.Connection sqlConnection;

    private ArrayList<User> searchResult = new ArrayList<User>();
    private ArrayList<User> searchResult2 = new ArrayList<User>();
    private PaginationData pagination;

    private SearchUserBean bean;

    @Before
    public void setup() throws SQLException {

        // Mock the connection
        PowerMockito.mockStatic(Connection.class);
        conn = mock(Connection.class);
        Mockito.when(Connection.create()).thenReturn(conn);

        // PowerMockito.mockStatic(java.sql.Connection.class);
        // sqlConnection = mock(java.sql.Connection.class);

        // Mock UnitDAO
        PowerMockito.mockStatic(UserDAO.class);

        bean = new SearchUserBean();

        user1 = new User();
        user1.setUserId(1);
        user1.setFirstname("Sebastian");
        user1.setLastname("Schwarz");
        user1.setDateOfBirth(new Date(1990, 2, 8));
        user1.setEmail("sebastian@nrschwarz.de");

        user2 = new User();
        user2.setUserId(2);
        user2.setFirstname("Ricky");
        user2.setLastname("Strohmeier");
        user2.setDateOfBirth(new Date(1993, 6, 15));
        user2.setEmail("ricky@nichtdieRichtigemail.de");

        user3 = new User();
        user3.setUserId(3);
        user3.setFirstname("Patrick");
        user3.setLastname("Cretu");
        user3.setDateOfBirth(new Date(1990, 1, 30));
        user3.setEmail("patrick@nichtdieRichtigemail.de");

        user4 = new User();
        user4.setUserId(4);
        user4.setFirstname("Tobi");
        user4.setLastname("Fuchs");
        user4.setDateOfBirth(new Date(1993, 6, 23));
        user4.setEmail("tobi@nichtdieRichtigemail.de");

        user5 = new User();
        user5.setUserId(5);
        user5.setFirstname("Kathi");
        user5.setLastname("Hoetzl");
        user5.setDateOfBirth(new Date(1992, 1, 30));
        user5.setEmail("kathi@nichtdieRichtigemail.de");

        searchResult.add(user1);
        searchResult.add(user2);
        searchResult.add(user3);
        searchResult.add(user4);
        searchResult.add(user5);

        searchResult2.add(user1);
        searchResult2.add(user2);

        pagination = new PaginationData();
        pagination.setCurrentPageNumber(0);
        pagination.setElementsPerPage(10);

        
        bean.init();

        bean.setPagination(pagination);
        bean.setOrderParam("name");
        bean.setSearchParam("name");
        bean.setSearchString("all");

        Mockito.when(UserDAO.getNumberOfUsersWithThisName(conn, "name"))
                .thenReturn(searchResult2.size());
        System.out.println("TEst");
        Mockito.when(
                UserDAO.getUsers(conn, pagination, bean.getSearchParam(),
                        bean.getSearchString())).thenReturn(searchResult);

        Mockito.when(
                UserDAO.getUsers(conn, pagination, bean.getSearchParam(),
                        bean.getSearchString())).thenReturn(searchResult);
        System.out.println("TEst");
//        Mockito.when(
//                UserDAO.getAllUsers(conn, pagination, "testSQLBefehl" , bean
//                        .getPagination().getElementsPerPage(), 0)).thenReturn(
//                searchResult);
//        
//        Mockito.when(
//                UserDAO.getUsers(conn, pagination, "testSQLBefehl" , bean.getSearchString(), bean
//                        .getPagination().getElementsPerPage(), 0)).thenReturn(
//                searchResult);
//        
//        Mockito.when(UserDAO.getResult(resultSet)).thenReturn(searchResult2);
//        System.out.println("TEst");
//        Mockito.when(UserDAO.getOrderParam(bean.getOrderParam())).thenReturn("ASC");
        
    }

    @Test
    public void testGetResultArray() {

        
        System.out.println("TEstStart");
        bean.search();
        System.out.println("TEst");
        PowerMockito.verifyStatic();
        
        PowerMockito.verifyStatic();
        Mockito.when(UserDAO.getNumberOfUsersWithThisName(conn, "name"));
        
        Mockito.when(UserDAO.getUsers(conn, pagination, bean.getSearchParam(),
                bean.getSearchString()));

        

    }

}
