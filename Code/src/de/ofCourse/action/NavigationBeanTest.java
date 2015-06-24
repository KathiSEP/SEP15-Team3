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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FacesContext.class })
public class NavigationBeanTest {

    private NavigationBean bean;
    @Mock
    private FacesContext fc;
    @Mock
    private ExternalContext ec;

    @Before
    public void setup() {
        bean = new NavigationBean();

        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(fc);

        when(fc.getExternalContext()).thenReturn(ec);
    }

    @Test
    public void testLogout() {
        assertEquals("/facelets/open/index.xhtml?faces-redirect=true", bean.logout());
    }

}
