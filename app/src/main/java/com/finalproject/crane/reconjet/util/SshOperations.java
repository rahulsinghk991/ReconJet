package com.finalproject.crane.reconjet.util;

import com.finalproject.crane.reconjet.core.Consts;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Created by crane on 14/07/16.
 */
public class SshOperations {
    //ssh remote connection
    private String SSH_USER = Consts.SSH_USER;
    private String SSH_HOST = Consts.SSH_HOST;
    private int SSH_PORT = Consts.SSH_PORT;
    private String SSH_PASSWORD = Consts.SSH_PASSWORD;

    // forwarding ports
    private static final String REMOTE_HOST = Consts.REMOTE_HOST;  //mongodb host
    private static final Integer LOCAL_PORT = Consts.LOCAL_PORT;         //local port
    private static final Integer REMOTE_PORT = Consts.REMOTE_PORT;       //mongodb port

    public void connect(){
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        Session SSH_SESSION = null;
        try {
            SSH_SESSION = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
            SSH_SESSION.setPassword(SSH_PASSWORD);
            SSH_SESSION.setConfig(config);
            SSH_SESSION.connect();
            SSH_SESSION.setPortForwardingL(LOCAL_PORT, REMOTE_HOST, REMOTE_PORT);
        } catch (JSchException e) {
            e.printStackTrace();
        }

        System.out.println(SSH_SESSION.getServerVersion());
    }

}
