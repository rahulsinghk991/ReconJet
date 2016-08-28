package com.finalproject.crane.reconjet.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.finalproject.crane.reconjet.R;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class testSshActivity extends AppCompatActivity {

    private String SSH_USER = "coyl4";
    private String SSH_HOST = "monster.lboro.ac.uk";
    private int SSH_PORT = 587;
    private String SSH_PASSWORD = "rz5WYv35";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ssh);
        new ConnectSsh().execute();

    }

    public class ConnectSsh extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session SSH_SESSION = null;
            try {
                SSH_SESSION = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
            } catch (JSchException e) {
                e.printStackTrace();
            }
            SSH_SESSION.setPassword(SSH_PASSWORD);
            SSH_SESSION.setConfig(config);
            try {
                SSH_SESSION.connect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
            System.out.println(SSH_SESSION.getServerVersion());
            return null;
        }
    }

}
