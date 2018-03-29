package com.example.mrcio.applisbonph;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import database.DBHelper;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import email.Mail;

/**
 * Created by NB19194 on 06/05/2016.
 */
public class EventActivity extends AppCompatActivity {

    Bundle extras;
    int imageId;
    int userId;
    String eventName;

    final Context context = this;
    DBHelper mydb;

    ProgressDialog mProgressDialog;

    ImageView eventImage;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
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

        final Cursor userLogin;
        mydb = new DBHelper(this);

        extras = getIntent().getExtras();
        imageId = extras.getInt("imageId");
        userId = extras.getInt("userId");
        eventName = extras.getString("eventName");

        userLogin = mydb.getUserById(userId);
        userLogin.moveToFirst();

        //User data
        final String userName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_USERNAME));
        final String userEmail = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_EMAIL));
        final String userCompleteName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_COMPLETE_NAME));
        final String userInvoiceName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_INVOICE_NAME));
        final String userCertificationName = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_CERTIFICATE_NAME));
        final String userPhone = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_PHONE));
        final String userCitizenCard = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_CITIZEN_CARD));
        final String userWorkbooklet = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_WORK_BOOKLET));
        final String userNif = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_NIF));
        final String userAddress = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_ADDRESS));
        final String userZipCode = userLogin.getString(userLogin.getColumnIndex(DBHelper.USERS_COLUMN_POSTAL_CODE));

        eventImage = (ImageView)findViewById(R.id.eventImage);
        eventImage.setImageResource(imageId);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Tem a certeza que pretende enviar a inscrição para o evento " + eventName + "?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendRegisterEmail(userName, userEmail, userCompleteName, userInvoiceName, userCertificationName, userPhone, userCitizenCard, userWorkbooklet,
                                        userNif, userAddress, userZipCode);

                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
    }

    private void sendRegisterEmail(String userName, String userEmail ,String completeName, String invoiceName, String certificateName,
                                   String phone, String citizenCard, String workBooklet, String nif, String address, String zipCode){

        String[] toArr = new String[]{"joaonunofrazao@hotmail.com"};
        //Google SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "AppLisbon");
        //Send Pulse SMTP: final Mail m = new Mail("appLisbonTest@gmail.com", "fbN3EYiMtqi");

        //SMTP2GO
        final Mail m = new Mail("appLisbonTest@gmail.com", "5qhWvWv2y552");

        m.setTo(toArr);
        m.setFrom("appLisbonTest@gmail.com");
        m.setSubject("Inscrição User " + userName + " no Evento " + eventName);

        StringBuilder body = new StringBuilder("O Utilizador <b>" + userName + "</b> inscreveu-se no evento <b>" + eventName + "</b>, com os seguintes dados:");
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

            Logger log = Logger.getLogger(EventActivity.class);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(EventActivity.this);
                mProgressDialog.setTitle("Envio de Inscriçao");
                mProgressDialog.setMessage("A submeter inscrição...");
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
                    Toast.makeText(context, "Não foi possivel realizar a inscrição no evento. " +
                            "Verifique a ligação à Internet ou tente mais tarde", Toast.LENGTH_LONG).show();
                else{
                    Toast.makeText(context, "Inscrição realizada com sucesso", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

        }.execute();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
