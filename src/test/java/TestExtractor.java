/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author csabourdin
 */
public class TestExtractor {

    public TestExtractor() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void TestConf() {

        try {
            
            Configuration conf = new PropertiesConfiguration(System.getProperty("user.dir")+"/src/test/resources/xwiki.cfg");

            int ldapPort = conf.getInt("xwiki.authentication.ldap.port", 389);

            String tmpldap_server = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_server");
            //String tmpldap_base_DN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_base_DN");
            String tmpBinDN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_DN");
            String tmpldap_bind_pass = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_pass");
            String[] ldap_server = StringUtils.split(tmpldap_server,"|");
            //String[] ldap_base_DN = StringUtils.split(tmpldap_base_DN,"|");
            String[] BinDN = StringUtils.split(tmpBinDN,"|");
            String[] ldap_bind_pass = StringUtils.split(tmpldap_bind_pass,"|");
            //assertTrue(ldap_server.length == ldap_base_DN.length);
            assertTrue(BinDN.length == ldap_bind_pass.length);
            assertTrue(ldap_server.length == ldap_bind_pass.length);
            assertTrue(ldap_server.length == 6);
            assertTrue(ldapPort == 389);

            String className = conf.getString("xwiki.authentication.ldap.ssl.secure_provider", "com.sun.net.ssl.internal.ssl.Provider");
            assertEquals(className, "com.sun.net.ssl.internal.ssl.Provider");
            assertEquals(conf.getInt("xwiki.authentication.ldap.timeout", 500), 500);
            assertEquals(conf.getInt("xwiki.authentication.ldap.maxresults", 10), 10);
        } catch (ConfigurationException ex) {
            Logger.getLogger(TestExtractor.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getLocalizedMessage());
        }

    }
@Test
    public void testValue() {

        try {
            Configuration conf = new PropertiesConfiguration(System.getProperty("user.dir")+"/src/test/resources/xwiki.cfg");
            String tmpldap_server = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_server");
            //String tmpldap_base_DN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_base_DN");
            String tmpBinDN = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_DN");
            String tmpldap_bind_pass = conf.getString("xwiki.authentication.trustedldap.remoteUserMapping.ldap_bind_pass");
            String[] ldap_server = StringUtils.split(tmpldap_server,"|");
            //String[] ldap_base_DN = StringUtils.split(tmpldap_base_DN,"|");
            String[] BinDN = StringUtils.split(tmpBinDN,"|");
            String[] ldap_bind_pass = StringUtils.split(tmpldap_bind_pass,"|");
            
            
            int i = 0;
            String userDomain = StringUtils.split(ldap_server[i],"=")[0];
            String userDomainLDAPServer = StringUtils.split(ldap_server[i],"=")[1];
            // never used String userDomainLDAPBaseDN = ldap_base_DN[i].split("=")[1];
            String userDomainBinDN = StringUtils.split(BinDN[i],"=")[1];
            String userDomainldap_bind_pass = StringUtils.split(ldap_bind_pass[i],"=")[1];
            assertEquals(userDomain, "ZOZO.AD");
            assertEquals(userDomainLDAPServer, "10.0.0.1");
            assertEquals(userDomainBinDN, "connecuserssosw@ZOZO.AD");
            assertEquals(userDomainldap_bind_pass, "secret001");

            i = 3;
            userDomain = ldap_server[i].split("=")[0];
            userDomainLDAPServer = ldap_server[i].split("=")[1];
            // never used String userDomainLDAPBaseDN = ldap_base_DN[i].split("=")[1];
            userDomainBinDN = BinDN[i].split("=")[1];
            userDomainldap_bind_pass = ldap_bind_pass[i].split("=")[1];
            assertEquals(userDomain, "SOMEWHERE.INTERNAL");
            assertEquals(userDomainLDAPServer, "10.0.0.4");
            assertEquals(userDomainBinDN, "connecuserssoau@SOMEWHERE.INTERNAL");
            assertEquals(userDomainldap_bind_pass, "secret004");
        } catch (ConfigurationException ex) {
            Logger.getLogger(TestExtractor.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getLocalizedMessage());
        }

    }

}
