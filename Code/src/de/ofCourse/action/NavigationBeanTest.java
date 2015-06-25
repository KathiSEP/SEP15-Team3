package de.ofCourse.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.powermock.modules.junit4.PowerMockRunner;

import de.ofCourse.model.Language;

/**
 * This is a test class for the Navigation Bean. It tests if the user is being successfully logged out
 * and if the language is set correct.
 * 
 * @author Ricky Strohmeier
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class})
public class NavigationBeanTest {

    private NavigationBean navigationBean;

    private Language SAMPLE_LANGUAGE = Language.EN;

    private int SAMPLE_ID = 10000;

    @Mock
    private SessionUserBean sessionUser;

    @Mock
    private FacesContext fc;

    @Mock
    private ExternalContext ec;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setup() {
        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(fc);

        PowerMockito.mockStatic(ExternalContext.class);
        when(fc.getExternalContext()).thenReturn(ec);

        navigationBean = new NavigationBean();

        sessionUser = new SessionUserBean();
        sessionUser.setUserID(SAMPLE_ID);
        sessionUser.setLanguage(SAMPLE_LANGUAGE);
        navigationBean.setSessionUser(sessionUser);
    }

    /**
     * Tests if the logout is working correct.
     */
    @Test
    public void testLogout() {
        assertEquals(SAMPLE_ID, navigationBean.getSessionUser().getUserID());
        assertEquals("/facelets/open/index.xhtml?faces-redirect=true", navigationBean.logout());
        assertEquals(0, navigationBean.getSessionUser().getUserID());
    }

    /**
     * Tests if the language is set correct.
     */
    @Test
    public void testSetLanguage() {
        assertEquals(Language.EN, navigationBean.getSessionUser().getLanguage());

        navigationBean.getSessionUser().setLanguage(Language.DE);
        assertFalse(SAMPLE_LANGUAGE.equals(navigationBean.getSessionUser().getLanguage()));

        navigationBean.getSessionUser().setLanguage(Language.BAY);
        assertFalse(SAMPLE_LANGUAGE.equals(navigationBean.getSessionUser().getLanguage()));
    }

}
