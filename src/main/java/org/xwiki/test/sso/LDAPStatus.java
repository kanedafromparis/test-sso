/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xwiki.test.sso;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSocketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Provider;
import java.security.Security;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 *
 * @author csabourdin
 */
public class LDAPStatus extends HttpServlet {

    String xwikicfgPath = "/usr/local/xwiki/WEB-INF/xwiki.cfg";
    Configuration conf = null;

    Logger log = Logger.getLogger(LDAPStatus.class.getName());

    FastDateFormat format = FastDateFormat.getDateInstance(FastDateFormat.FULL);
    HashMap templog = new HashMap();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        if (config.getInitParameter("xwiki.cfg") != null) {
            xwikicfgPath = config.getInitParameter("xwiki.cfg");
        }

        try {
            conf = new PropertiesConfiguration(xwikicfgPath);
        } catch (ConfigurationException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }

    }

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

            int ldapPort = conf.getInt("xwiki.authentication.ldap.port", 389);
            boolean useSSL = false;
            //never used String ldapHost = conf.getString("xwiki.authentication.ldap.server", "127.0.0.1");

            // allow to use the given user and password also as the LDAP bind user and password
            String tmpldap_server = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_server");

            //String tmpldap_base_DN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_base_DN");
            String tmpBinDN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_DN");
            String tmpldap_bind_pass = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_pass");

            String[] ldap_server = StringUtils.split(tmpldap_server, "|");
            //String[] ldap_base_DN = tmpldap_base_DN.replace(" ", "").split("|");
            String[] BinDN = StringUtils.split(tmpBinDN, "|");
            String[] ldap_bind_pass = StringUtils.split(tmpldap_bind_pass, "|");
            if ((BinDN.length == ldap_bind_pass.length) && (ldap_server.length == ldap_bind_pass.length)) {
                // Everythnig is ok
            } else {
                throw new ServletException("xwiki.authentication.trustedldap.remoteUserMapping parameter don't have the same size, configuration is wrong");
            }
            String keyStore = null;
            if ("1".equals(conf.getString("xwiki.authentication.ldap.ssl", "0"))) {
                keyStore = conf.getString("xwiki.authentication.ldap.ssl.keystore", "");
                useSSL = true;
                log.log(Level.FINEST, "Connecting to LDAP using SSL");
            }

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>LDAP Status</title>");
            out.println("</head>");
            out.println("<body>");
            HashMap locallog = new HashMap();
            for (int i = 0; i < ldap_server.length; i++) {
                String userDomain = StringUtils.split(ldap_server[i], "=")[0];
                String userDomainLDAPServer = StringUtils.split(ldap_server[i], "=")[1];
                String userDomainBinDN = StringUtils.split(BinDN[i], "=")[1];
                String userDomainldap_bind_pass = StringUtils.split(ldap_bind_pass[i], "=")[1];
                locallog = new HashMap();
                locallog.put("timestamp", Calendar.getInstance());
                try {
                    out.println("<h3> <a href='/test-sso/LDAPStatus?" + userDomain + "=true'>" + userDomain + ":" + userDomainLDAPServer + "</a>");

                    if (StringUtils.equals("true", request.getParameter(userDomain))) {

                        if (this.open(userDomainLDAPServer, ldapPort, userDomainBinDN, userDomainldap_bind_pass, keyStore, useSSL)) {
                            out.println(" - OK");
                            locallog.put("status", "OK");
                        } else {
                            out.println(" - NOK");
                            locallog.put("status", "NOK");
                        }
                    }
                    out.println("</h3>");
                    out.println("<p> ");
                    HashMap datavar = (HashMap) templog.get(userDomain);
                    if (datavar != null && datavar.get("status") != null) {
                        out.println("<div>" + format.format(datavar.get("timestamp")) + " - ");
                        out.println("" + datavar.get("status") + "</div>");
                    }
                    out.println("</p>");
                } catch (UnsupportedEncodingException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                } catch (LDAPException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                } catch (java.io.IOException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                } catch (ClassNotFoundException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                } catch (InstantiationException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                } catch (IllegalAccessException ex) {
                    this.displayExceptionMessage(userDomain, userDomainLDAPServer, locallog, ex, out);
                }
                templog.put(userDomain, locallog);
            }
            out.println("<p><a href='/test-sso/LDAPStatus'>Check LDAP Status</a>");
            out.println("<br><a href='/test-sso'>Test SSO Status</a>");
            out.println("<br><a href='/test-sso/GetHeaders'>Check Header</a></p>");
            out.println("<br>This Web Application is for debug only, it is not build for intensive use</p>");
            out.println("</body>");
            out.println("</html>");
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
        return "This Servlet show the LDAP status of the status of LDAP server that XWiki is supossed to contact";
    }// </editor-fold>

    public boolean open(String ldapHost, int ldapPort, String loginDN, String password, String pathToKeys, boolean ssl)
            throws UnsupportedEncodingException, LDAPException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        int port = ldapPort;
        LDAPConnection connection = null;
        if (port <= 0) {
            port = ssl ? LDAPConnection.DEFAULT_SSL_PORT : LDAPConnection.DEFAULT_PORT;
        }

        try {
            if (ssl) {

                // Dynamically set JSSE as a security provider
                Provider provider;

                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                String className = conf.getString("xwiki.authentication.ldap.ssl.secure_provider", "com.sun.net.ssl.internal.ssl.Provider");

                provider = (java.security.Provider) cl.loadClass(className).newInstance();

                Security.addProvider(provider);

                if (pathToKeys != null && pathToKeys.length() > 0) {
                    // Dynamically set the property that JSSE uses to identify
                    // the keystore that holds trusted root certificates

                    System.setProperty("javax.net.ssl.trustStore", pathToKeys);
                    // obviously unnecessary: sun default pwd = "changeit"
                    // System.setProperty("javax.net.ssl.trustStorePassword", sslpwd);
                }

                LDAPSocketFactory ssf = new LDAPJSSESecureSocketFactory();

                // Set the socket factory as the default for all future connections
                // LDAPConnection.setSocketFactory(ssf);
                // Note: the socket factory can also be passed in as a parameter
                // to the constructor to set it for this connection only.
                connection = new LDAPConnection(ssf);
            } else {
                connection = new LDAPConnection();
            }

            log.log(Level.FINER, "Connection to LDAP server [{0}:{1}]", new Object[]{ldapHost, port});

            // connect to the server
            connection.connect(ldapHost, port);

            // set referral following
            LDAPSearchConstraints constraints = new LDAPSearchConstraints(connection.getConstraints());
            constraints.setTimeLimit(conf.getInt("xwiki.authentication.ldap.timeout", 500));
            constraints.setMaxResults(conf.getInt("xwiki.authentication.ldap.maxresults", 10));
            connection.setConstraints(constraints);

            // bind
            log.log(Level.FINER, "Binding to LDAP server with credentials login=[{}]", new Object[]{loginDN});

            // authenticate to the server
            connection.bind(LDAPConnection.LDAP_V3, loginDN, password.getBytes("UTF8"));

        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (LDAPException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return true;
    }

    private void displayExceptionMessage(String userDomain, String userDomainLDAPServer, HashMap smalllog, Exception ex, PrintWriter out) {
        log.log(Level.SEVERE, null, ex);
        out.println("<h3> " + userDomain + ":" + userDomainLDAPServer + " - NOK </h3>");
        out.println("<p>" + ex.toString() + "</p>");
        smalllog.put("status", ex.toString());

    }

}
