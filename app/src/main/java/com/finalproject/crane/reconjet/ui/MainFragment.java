package com.finalproject.crane.reconjet.ui;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.crane.reconjet.R;
import com.finalproject.crane.reconjet.dao.DbOperations;

public class MainFragment extends Fragment {
    TextView age_tv;
    TextView height_tv;
    TextView weight_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        age_tv = (TextView)rootView.findViewById(R.id.age);
        height_tv = (TextView)rootView.findViewById(R.id.height);
        weight_tv = (TextView)rootView.findViewById(R.id.weight);
        return rootView;
    }
    /* OnCreate function (nolonger needed)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.Fragment_main);
        age_tv = (TextView)findViewById(R.id.age);
        height_tv = (TextView)findViewById(R.id.height);
        weight_tv = (TextView)findViewById(R.id.weight);

        new getInfoDB().execute();
    }
    */

    /*button on click functions (nolonger needed)
    public void startRunHandler(View view){
        Intent intent = new Intent(getContext(),RunActivity.class);
        startActivity(intent);
    }

    public void startCycleHandler(View view){
        Intent intent = new Intent(getContext(),CycleActivity.class);
        startActivity(intent);
    }
    */

    private class getInfoDB extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            DbOperations dbOperations = new DbOperations();
            return dbOperations.testConnect();
        }

        @Override
        protected void onPostExecute(String result) {
            //age_tv.setText(age_tv.getText().toString()+20);
            height_tv.setText(height_tv.getText().toString()+175+"cm");
            weight_tv.setText(weight_tv.getText().toString()+70+"kg");
            if (result.equals("")) Toast.makeText(getContext(),"Connection failed",Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(),"Connection ok",Toast.LENGTH_SHORT).show();
            //super.onPostExecute(result);
        }

    }
}
