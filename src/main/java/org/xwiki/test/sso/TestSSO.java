/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xwiki.test.sso;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author csabourdin
 */
public class TestSSO extends HttpServlet {

    Hashtable accesses = new Hashtable();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            String remoteUser = request.getRemoteUser();

            if (remoteUser == null) {
                out.println("Welcome!");
            } else {
                out.println("Welcome, " + remoteUser + "!");
                Date lastAccess = (Date) accesses.get(remoteUser);
                if (lastAccess == null) {
                    out.println("This is your first visit!");
                } else {
                    out.println("Your last visit was " + accesses.get(remoteUser));
                }

                out.println("<p><a href='/test-sso/LDAPStatus'>Check LDAP Status</a>");
                out.println("<br><a href='/test-sso/index.html'>Test SSO Status</a>");
                out.println("<br><a href='/test-sso/GetHeaders'>Check Header</a></p>");

                accesses.put(remoteUser, new Date());
            }
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
