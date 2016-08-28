package com.finalproject.crane.reconjet.ui;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.finalproject.crane.reconjet.dao.DbOperations;
import com.finalproject.crane.reconjet.R;
import com.finalproject.crane.reconjet.util.SshOperations;

public class LoginActivity extends AppCompatActivity {
    AppCompatActivity activity;
    private EditText username_et;
    private EditText password_et;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_et = (EditText)findViewById(R.id.user_name);
        password_et= (EditText)findViewById(R.id.password);
        new ConnectSsh().execute();
        activity = this;
    }

    public void loginOnClickHandler(View view){
        System.out.println("???????????");
        username = username_et.getText().toString();
        password = password_et.getText().toString();
        new logInDb().execute(username, password);
    }


    private class logInDb extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            DbOperations dbOperations = new DbOperations();
            return dbOperations.login(strings[0],strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("true")){
                Toast.makeText(getBaseContext(),"true",Toast.LENGTH_SHORT).show();
                SharedPreferences mySharedPreferences= getSharedPreferences("user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("username",username);
                editor.putString("password",password);
                editor.commit();
                Intent intent = new Intent(getBaseContext(),DrawerActivity.class);
                activity.finish();
                startActivity(intent);
            }
            else if(s.equals("false")) {
                Toast.makeText(getBaseContext(),"usr name or pwd wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ConnectSsh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SshOperations sshOperations = new SshOperations();
            sshOperations.connect();
            return null;
        }
    }
}
