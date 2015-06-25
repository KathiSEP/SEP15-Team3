/**
 * This package provides utility functionality for the ofCourse system.
 */
package de.ofCourse.utilities;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Provides the service of filtering to enable UTF-8 encoding to prevent
 * problems with character presentation.<br>
 * This class implements the interface <code>Filter</code>.
 * <p>
 * The <code>EncodingFilter</code> needs to be registered in the web.xml file.
 * 
 * @author Sebastian
 *
 */
public class EncodingFilter implements Filter {

    private String encoding = "utf-8";
    /**
     * Takes the filter out of service.
     *
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
	// brauchen wir nicht

    }

    /**
     * Selects the character encoding and sets it to be used to interpret
     * request parameters for this request.
     * 
     * @param request
     *            the servlet request
     * @param response
     *            the servlet response
     * @param chain
     *            the filter chain
     * 
     * @throws IOException
     *             if a input/output exception occurs
     * @throws ServletException
     *             if a servlet exception occurs
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        chain.doFilter(request, response);

    }

    /**
     * Places the filter into service.
     * 
     * @throws ServletException
     *             if a servlet exception occurs
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    String encodingParam = arg0.getInitParameter("encoding");
    
    if(encodingParam != null){
        encoding = encodingParam;
    }

    }

}
