package com.example.saswat.mobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,Usrname;     //hit option + enter if you on mac , for windows hit ctrl + enter
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private RadioButton selectedDesign;
    private RadioGroup radioGrp;
    private String doctorName="",docPhone="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.registered_btn);
        btnSignUp = (Button) findViewById(R.id.register_btn);
        inputEmail = (EditText) findViewById(R.id.signUpEmail);
        inputPassword = (EditText) findViewById(R.id.signUppwd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.pwd_reset_btn);
        Usrname=(EditText) findViewById(R.id.userName);
        radioGrp=(RadioGroup)findViewById(R.id.designation);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, PwdResetActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGrp.getCheckedRadioButtonId();
                selectedDesign=(RadioButton)findViewById(selectedId) ;
                final  String email = inputEmail.getText().toString().trim();
               final String password = inputPassword.getText().toString().trim();
               final String designation=selectedDesign.getText().toString().trim();
               final String fullname=Usrname.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                if(designation.equalsIgnoreCase("Patient")) {
                   AlertDialog.Builder altBuiler = new AlertDialog.Builder(UserSignUpActivity.this);
                   View altview = getLayoutInflater().inflate(R.layout.doctor_info, null);
                   final EditText docName = (EditText) altview.findViewById(R.id.doctorName);
                   final EditText docPh = (EditText) altview.findViewById(R.id.docPhone);
                   Button save = (Button) altview.findViewById(R.id.saveDocDetails);

                   save.setOnClickListener(new View.OnClickListener() {

                       @Override
                       public void onClick(View view) {


                           doctorName=docName.getText().toString().trim();
                           docPhone=docPh.getText().toString().trim();

                           if (TextUtils.isEmpty(doctorName)) {
                               Toast.makeText(getApplicationContext(), "Enter Doctor Name!", Toast.LENGTH_SHORT).show();
                               return;
                           }

                           if (TextUtils.isEmpty(docPhone)) {
                               Toast.makeText(getApplicationContext(), "Enter Doctor Phone!", Toast.LENGTH_SHORT).show();
                               return;
                           }


                           auth.createUserWithEmailAndPassword(email, password)
                                   .addOnCompleteListener(UserSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                       @Override
                                       public void onComplete(@NonNull Task<AuthResult> task) {
                                           Toast.makeText(UserSignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                           progressBar.setVisibility(View.GONE);
                                           if (!task.isSuccessful()) {
                                               Toast.makeText(UserSignUpActivity.this, "Authentication failed." + task.getException(),
                                                       Toast.LENGTH_SHORT).show();
                                           } else {

                                               RegisteredUser regUser = new RegisteredUser(fullname,email,designation,doctorName,docPhone);

                                               FirebaseDatabase.getInstance().getReference("Users")
                                                       .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                       .setValue(regUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       progressBar.setVisibility(View.GONE);
                                                       if (task.isSuccessful()) {
                                                           Toast.makeText(UserSignUpActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                                           startActivity(new Intent(UserSignUpActivity.this, MainActivity.class));
                                                           finish();
                                                       } else {
                                                           Toast.makeText(UserSignUpActivity.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                                       }
                                                   }
                                               });


                                           }
                                       }
                                   });
                       }
                   });
                   altBuiler.setView(altview);
                   AlertDialog dailog=altBuiler.create();
                   dailog.show();
               }else{
                   auth.createUserWithEmailAndPassword(email, password)
                           .addOnCompleteListener(UserSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   Toast.makeText(UserSignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.GONE);
                                   if (!task.isSuccessful()) {
                                       Toast.makeText(UserSignUpActivity.this, "Authentication failed." + task.getException(),
                                               Toast.LENGTH_SHORT).show();
                                   } else {

                                       RegisteredUser regUser = new RegisteredUser(fullname,email,designation,doctorName,docPhone);

                                       FirebaseDatabase.getInstance().getReference("Users")
                                               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                               .setValue(regUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               progressBar.setVisibility(View.GONE);
                                               if (task.isSuccessful()) {
                                                   Toast.makeText(UserSignUpActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                                   startActivity(new Intent(UserSignUpActivity.this, MainActivity.class));
                                                   finish();
                                               } else {
                                                   Toast.makeText(UserSignUpActivity.this, getString(R.string.registration_failure), Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       });


                                   }
                               }
                           });
                                                      }
                //create user
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}