package com.devops;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HelloWorldServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private HelloWorldServlet servlet;

    @Before
    public void setUp() {
        // Initialize the mocks
        MockitoAnnotations.initMocks(this);
        // Create a new instance of the servlet
        servlet = new HelloWorldServlet();
    }

    @Test
    public void testHelloWorldServlet() throws IOException {
        // Mock the response to capture the output
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // Call the servlet's doGet() method (which is triggered on GET requests)
        servlet.doGet(request, response);

        // Verify that the response output contains the expected text
        verify(writer).write("<h1>Welcome to the DevOps Web Application!</h1>");
    }
}
