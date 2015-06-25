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
    }

    /**
     * Tests if the logout is working correct.
     */
    @Test
    public void testLogout() {
        assertEquals("/facelets/open/index.xhtml?faces-redirect=true", navigationBean.logout());
    }

    /**
     * Tests if the language is set correct.
     */
    @Test
    public void testSetLanguage() {
        sessionUser = new SessionUserBean();

        sessionUser.setLanguage(SAMPLE_LANGUAGE);
        navigationBean.setSessionUser(sessionUser);
        assertEquals(Language.EN, navigationBean.getSessionUser().getLanguage());

        sessionUser.setLanguage(Language.DE);
        navigationBean.setSessionUser(sessionUser);
        assertFalse(SAMPLE_LANGUAGE.equals(navigationBean.getSessionUser().getLanguage()));
    }

}
