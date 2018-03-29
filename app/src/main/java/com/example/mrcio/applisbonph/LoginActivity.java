package com.example.mrcio.applisbonph;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import database.DBHelper;

public class LoginActivity extends Activity {

    DBHelper mydb;

    Button enterButton;
    Button registerButton;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Context context = this;
        mydb = new DBHelper(this);

        enterButton = (Button)findViewById(R.id.enter);
        registerButton = (Button)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.etUsername);
        password = (EditText)findViewById(R.id.etPassword);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("".equals(username.getText().toString()) || "".equals(password.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Utilizador e Password têm de ser preenchidos",Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor userLogin = mydb.getUserByName(username.getText().toString());
                userLogin.moveToFirst();

                if(userLogin.getCount() == 0){
                    Toast.makeText(getApplicationContext(), "Utilizador desconhecido",Toast.LENGTH_SHORT).show();
                    userLogin.close();
                    return;
                }

                String userPassword = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_PASSWORD));
                int userId = userLogin.getInt(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_ID));
                if(password.getText().toString().equals(userPassword)){
                    finish();
                    userLogin.close();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putInt("userId", userId);
                    Intent mainPage = new Intent(context, MainActivity.class);
                    mainPage.putExtras(dataBundle);
                    startActivity(mainPage);
                } else {
                    Toast.makeText(getApplicationContext(), "Password Errada",Toast.LENGTH_SHORT).show();
                    userLogin.close();
                    return;
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(context, RegisterActivity.class);
                startActivity(registerPage);
            }
        });
    }

}
