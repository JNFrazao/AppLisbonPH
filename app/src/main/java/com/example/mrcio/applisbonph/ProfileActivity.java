package com.example.mrcio.applisbonph;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ProfileActivity extends AppCompatActivity {

    DBHelper mydb;
    final Context context = this;

    TextView displayUser;

    EditText username;
    EditText email;
    EditText oldPassword;
    EditText newPassword;
    EditText confirmNewPassword;
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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mydb = new DBHelper(this);

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
        final Logger log = Logger.getLogger(ProfileActivity.class);

        final Cursor userLogin;
        Bundle extras = getIntent().getExtras();
        final int userId = extras.getInt("userId");

        displayUser = (TextView)findViewById(R.id.etUser);
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
        oldPassword = (EditText)findViewById(R.id.etOldPassword);
        newPassword = (EditText)findViewById(R.id.etNewPassword);
        confirmNewPassword = (EditText)findViewById(R.id.etConfirmNewPassword);

        enterButton = (Button)findViewById(R.id.enter);
        cancelButton = (Button)findViewById(R.id.cancel);

        userLogin = mydb.getUserById(userId);
        userLogin.moveToFirst();
        String userName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_USERNAME));
        String userEmail = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_EMAIL));
        String userCompleteName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_COMPLETE_NAME));
        String userInvoiceName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_INVOICE_NAME));
        String userCertificationName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_CERTIFICATE_NAME));
        String userPhone = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_PHONE));
        String userCitizenCard = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_CITIZEN_CARD));
        String userWorkbooklet = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_WORK_BOOKLET));
        String userNif = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_NIF));
        String userAddress = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_ADDRESS));
        String userZipCode = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_POSTAL_CODE));
        final String password = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_PASSWORD));

        displayUser.setText((CharSequence) "Editar Perfil de " + userName);
        username.setText((CharSequence)userName);
        email.setText((CharSequence)userEmail);
        completeName.setText((CharSequence)userCompleteName);
        invoiceName.setText((CharSequence)userInvoiceName);
        certificateName.setText((CharSequence)userCertificationName);
        phone.setText((CharSequence)userPhone);
        citizenCard.setText((CharSequence)userCitizenCard);
        workBooklet.setText((CharSequence)userWorkbooklet);
        nif.setText((CharSequence)userNif);
        address.setText((CharSequence)userAddress);
        zipCode.setText((CharSequence)userZipCode);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean validateEmptyFields = verifyEmptyFields(username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                        invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                        nif.getText().toString(), address.getText().toString(), zipCode.getText().toString());
                if (!validateEmptyFields) {
                    Toast.makeText(getApplicationContext(), "Apenas o campo Nº Carteira Profissional é opcional", Toast.LENGTH_SHORT).show();
                    return;
                }

                CharSequence inputEmail = email.getText().toString();
                Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputEmail);
                if (!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), "Endereço de email inválido", Toast.LENGTH_SHORT).show();
                    userLogin.close();
                    return;
                }

                if (!oldPassword.getText().toString().equals(password)) {
                    Toast.makeText(getApplicationContext(), "Password actual errada", Toast.LENGTH_SHORT).show();
                    userLogin.close();
                    return;
                }

                if (oldPassword.getText().toString() != null && !"".equals(oldPassword.getText().toString())
                    && newPassword.getText().toString() != null && !"".equals(newPassword.getText().toString())
                    && !newPassword.getText().toString().equals(confirmNewPassword.getText().toString()) ) {

                    Toast.makeText(getApplicationContext(), "Password confirmada diferente da nova password introduzida", Toast.LENGTH_SHORT).show();
                    userLogin.close();
                    return;
                }

                boolean result;

                try{

                    if(newPassword.getText().toString() != null && !"".equals(newPassword.getText().toString())){
                        result = mydb.updateUser(userId, username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                                invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                                workBooklet.getText().toString(), nif.getText().toString(), address.getText().toString(), zipCode.getText().toString(),
                                newPassword.getText().toString());
                    } else {
                        result = mydb.updateUser(userId, username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                                invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                                workBooklet.getText().toString(), nif.getText().toString(), address.getText().toString(), zipCode.getText().toString(),
                                oldPassword.getText().toString());
                    }

                    if (result) {

                        userLogin.close();
                        sendRegisterEmail(username.getText().toString(), email.getText().toString(), completeName.getText().toString(),
                                invoiceName.getText().toString(), certificateName.getText().toString(), phone.getText().toString(), citizenCard.getText().toString(),
                                workBooklet.getText().toString(), nif.getText().toString(), address.getText().toString(), zipCode.getText().toString());

                    } else {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro ao editar o utilizador", Toast.LENGTH_SHORT).show();
                        finish();
                        userLogin.close();
                    }

                } catch (Exception e){
                    log.error("Erro ao actualizar user: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro ao editar o utilizador", Toast.LENGTH_SHORT).show();
                    finish();
                    userLogin.close();
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
        finish();
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

    private void sendRegisterEmail(String userName, String userEmail, String completeName, String invoiceName, String certificateName,
                                   String phone, String citizenCard, String workBooklet, String nif, String address, String zipCode){

        String[] toArr = new String[]{"joaonunofrazao@hotmail.com"};
        //Google SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "AppLisbon");
        //Send Pulse SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "fbN3EYiMtqi");

        //SMTP2GO
        final Mail m = new Mail("appLisbonTest@gmail.com", "5qhWvWv2y552");
        m.setTo(toArr);
        m.setFrom("AppLisbonPH");
        m.setSubject("Actualização de dados do User " + userName);

        StringBuilder body = new StringBuilder("O Utilizador <b>" + userName + "</b> atualizou os seus dados na AppLisbonPH:");
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

            Logger log = Logger.getLogger(ProfileActivity.class);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(ProfileActivity.this);
                mProgressDialog.setTitle("Envio de Actualização de Perfil");
                mProgressDialog.setMessage("A enviar actualização...");
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
                if(!result)
                    Toast.makeText(context, "Não foi possivel enviar actualização de perfil de Utilizador. " +
                            "Verifique a ligação à Internet ou tente mais tarde", Toast.LENGTH_LONG).show();
                else{
                    Toast.makeText(context, "Utilizador " + username.getText().toString() + " actualizado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }.execute();
    }
}
