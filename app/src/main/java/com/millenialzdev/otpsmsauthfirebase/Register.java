package com.millenialzdev.otpsmsauthfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private EditText etUsername, etNoHp, etPassword;
    private Button btnRegister, btnKembali;

    private ProgressBar bar;
    private EditText etKodeOtp;
    private Button btnVerif;
    private LinearLayout llOtp;

    private FirebaseAuth mAuth;

    private String verifikasiId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etNoHp = findViewById(R.id.etNoHp);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnKembali = findViewById(R.id.btnKembali);

        bar = findViewById(R.id.bar);
        etKodeOtp = findViewById(R.id.etKodeOtp);
        btnVerif = findViewById(R.id.btnVerif);
        llOtp = findViewById(R.id.llOtp);

        mAuth = FirebaseAuth.getInstance();

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String noHp = etNoHp.getText().toString();
                String password = etPassword.getText().toString();

                if (!(username.isEmpty() || noHp.isEmpty() || password.isEmpty())){

                    kodeOTP(noHp);
                    bar.setVisibility(View.VISIBLE);
                    llOtp.setVisibility(View.VISIBLE);
                    reset();
                }else{
                    Toast.makeText(getApplicationContext(), "Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = etKodeOtp.getText().toString();

                if (code.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Kode OTP Salah", Toast.LENGTH_SHORT).show();
                }else{
                    verifKodeOTP(code);
                }

            }
        });

    }

    private void verifKodeOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifikasiId, code);
        loginByCredetial(credential);
    }

    private void loginByCredetial(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                    }
                });
    }

    private void kodeOTP(String noHp) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+62" + noHp)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);

            verifikasiId = s;
            Toast.makeText(getApplicationContext(), "Mengirim Kode OTP", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.VISIBLE);
        }
    };

    private void reset() {
        etUsername.setText("");
        etNoHp.setText("");
        etPassword.setText("");
    }
}