package de.ofCourse.system;

import static org.junit.Assert.*;

import de.ofCourse.system.Connection;
import org.junit.Test;

public class ConnectionTest {
    
    Connection conn = new Connection();

    @Test
    public void testGetConn() {
        assertNotNull(conn.getConn());;
    }

    @Test
    public void testStart() {
        fail("Not yet implemented");
    }

    @Test
    public void testCommit() {
        fail("Not yet implemented");
    }

    @Test
    public void testRollback() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetConnection() {
        fail("Not yet implemented");
    }

    @Test
    public void testReleaseConnection() {
        fail("Not yet implemented");
    }

}
