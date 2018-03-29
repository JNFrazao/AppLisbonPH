package com.example.mrcio.applisbonph;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.DBHelper;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import email.Mail;
import util.StringUtils;

public class RegisterActivity extends AppCompatActivity {

    DBHelper mydb;
    final Context context = this;

    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;
    EditText completeName;
    EditText invoiceName;
    EditText certificateName;
    EditText phone;
    EditText citizenCard;
    EditText workBooklet;
    EditText nif;
    EditText address;
    EditText zipCode;

    Button enterButton;
    Button cancelButton;

    ProgressDialog mProgressDialog;

    public static final String EMAIL_REGEX = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Logs
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                + File.separator + "AppLisbonPh" + File.separator + "logs"
                + File.separator + "AppLisbonPhLog.txt");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
        final Logger log = Logger.getLogger(RegisterActivity.class);

        mydb = new DBHelper(this);

        username = (EditText)findViewById(R.id.etUsername);
        email = (EditText)findViewById(R.id.etEmail);
        completeName = (EditText)findViewById(R.id.etName);
        invoiceName = (EditText)findViewById(R.id.etInvoiceName);
        certificateName = (EditText)findViewById(R.id.etCertificateName);
        phone = (EditText)findViewById(R.id.etPhone);
        citizenCard = (EditText)findViewById(R.id.etCitizenCard);
        workBooklet = (EditText)findViewById(R.id.etWorkBooklet);
        nif = (EditText)findViewById(R.id.etNif);
        address = (EditText)findViewById(R.id.etAddress);
        zipCode = (EditText)findViewById(R.id.etZipCode);
        password = (EditText)findViewById(R.id.etPassword);
        confirmPassword = (EditText)findViewById(R.id.etConfirmPassword);

        enterButton = (Button)findViewById(R.id.enter);
        cancelButton = (Button)findViewById(R.id.cancel);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean validateEmptyFields = verifyEmptyFields(username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                        invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                        nif.getText().toString(), address.getText().toString(), zipCode.getText().toString());
                if(!validateEmptyFields){
                    Toast.makeText(getApplicationContext(), "Apenas o campo Nº Carteira Profissional é opcional",Toast.LENGTH_SHORT).show();
                    return;
                }

                CharSequence inputEmail = email.getText().toString();
                Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputEmail);
                if(!matcher.matches()){
                    Toast.makeText(getApplicationContext(), "Endereço de email inválido",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Password confirmada diferente da password introduzida",Toast.LENGTH_SHORT).show();
                    return;
                }

                try{
                    Cursor user = mydb.getUserByName(username.getText().toString());
                    user.moveToFirst();

                    if(user.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "Utilizador " + username.getText().toString() + " já existe", Toast.LENGTH_SHORT).show();
                        user.close();
                        return;
                    }

                    sendRegisterEmail(username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                            invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                            workBooklet.getText().toString(), nif.getText().toString(), address.getText().toString(), zipCode.getText().toString(), user);

                } catch (Exception e) {
                    log.error("Erro ao criar user: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro ao criar o utilizador",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Tem a certeza que pretende sair do registo de utilizador?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterActivity.this.finish();
                        Intent loginPage = new Intent(context, LoginActivity.class);
                        finish();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void sendRegisterEmail(final String userName, final String userEmail , final String completeName, final String invoiceName, final String certificateName,
                                   final String phone, final String citizenCard, final String workBooklet, final String nif, final String address, final String zipCode,
                                   final Cursor user){

        String[] toArr = new String[]{"joaonunofrazao@hotmail.com"};
        //Google SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "AppLisbon");
        //Send Pulse SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "fbN3EYiMtqi");

        //SMTP2GO
        final Mail m = new Mail("appLisbonTest@gmail.com", "5qhWvWv2y552");
        m.setTo(toArr);
        m.setFrom("AppLisbonPH");
        m.setSubject("Inscrição User " + userName);

        boolean result = false;

        StringBuilder body = new StringBuilder("O Utilizador <b>" + userName + "</b> inscreveu-se com os seguintes dados na AppLisbonPH:");
        body.append("<br/>");
        body.append("<br/>");
        body.append("<b>Nome Completo:</b> " + completeName);
        body.append("<br/>");
        body.append("<b>Endereco de email utilizado:</b> " + userEmail);
        body.append("<br/>");
        body.append("<b>Nome para Fatura:</b> " + invoiceName + ".");
        body.append("<br/>");
        body.append("<b>Nome para Certificado:</b> " + certificateName);
        body.append("<br/>");
        body.append("<b>Num. de Telefone:</b> " + phone);
        body.append("<br/>");
        body.append("<b>Num. de Cartao do Cidadao:</b> " + citizenCard);
        body.append("<br/>");
        body.append("<b>Num. de Carteira Profissional:</b> " + workBooklet);
        body.append("<br/>");
        body.append("<b>NIF:</b> " + nif);
        body.append("<br/>");
        body.append("<b>Morada:</b> " + address);
        body.append("<br/>");
        body.append("<b>Codigo Postal:</b> " + zipCode);
        body.append("<br/>");
        body.append("<br/>");
        body.append("Atenciosamente,");
        body.append("<br/>");
        body.append("AppLisbonPH");

        m.setBody(body.toString());

        new AsyncTask<Void, Void, Void>() {
            boolean result;

            Logger log = Logger.getLogger(RegisterActivity.class);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(RegisterActivity.this);
                mProgressDialog.setTitle("Registo de Utilizador");
                mProgressDialog.setMessage("A registar o utilizador...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    result = m.send();
                } catch (Exception e) {
                    log.error("Failed to Sent email: " + e.getMessage());
                    result = false;
                } finally {
                    mProgressDialog.dismiss();
                }
            return null;}

            @Override
            protected void onPostExecute(Void v){
                if(!result){
                    Toast.makeText(context, "Não foi possivel registar o utilizador. " +
                            "Verifique a ligação à Internet ou tente mais tarde", Toast.LENGTH_LONG).show();
                }
                else {

                    boolean result = mydb.insertUser(userName, userEmail, completeName,
                            invoiceName, certificateName, phone, citizenCard,
                            workBooklet, nif, address, zipCode, password.getText().toString());

                    if(result){
                        user.close();
                        Toast.makeText(getApplicationContext(), "Utilizador "+username.getText().toString()+" criado com sucesso",Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao criar o utilizador",Toast.LENGTH_SHORT).show();
                        user.close();
                    }
                }

            }

        }.execute();

    }

    private boolean verifyEmptyFields(String username, String email, String completeName, String invoiceName, String certificateName, String phone,
                                      String citizenCard, String nif, String address, String zipCode){

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(email) || StringUtils.isEmpty(completeName) || StringUtils.isEmpty(invoiceName)
                || StringUtils.isEmpty(certificateName) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(citizenCard) || StringUtils.isEmpty(nif)
                || StringUtils.isEmpty(address) || StringUtils.isEmpty(zipCode)) {
            return false;
        }
        else {
            return true;
        }
    }
}
