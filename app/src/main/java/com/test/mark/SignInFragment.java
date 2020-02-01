package com.test.mark;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    private TextView signUpNow;
    private TextView forgotPassword;
    FrameLayout parentFrameLayout;

    private EditText phone;
    private EditText password;

    private ImageButton closeButton;
    private Button signInButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private String phonePattern = "[0-9]";


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        signUpNow  = view.findViewById(R.id.txt_sign_up_now);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);


        phone = view.findViewById(R.id.sign_in_phone);
        password = view.findViewById(R.id.sign_in_password);
        progressBar = view.findViewById(R.id.sign_in_progress_bar);

        closeButton = view.findViewById(R.id.sign_in_close_button);
        signInButton = view.findViewById(R.id.sign_in_button);

        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //sign-up button function
        signUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        //close button function
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        //forgot password function
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountActivity.onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });

        //set button:enabled mode false before using these functions
        /*email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //checkInputs();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //checkInputs();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        //sign-in button task
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailAndPassword();

            }
        });

    }

    //set button:enabled mode false before using these functions
    /*private void checkInputs() {

        if (!TextUtils.isEmpty(email.getText())) {

            if (!TextUtils.isEmpty(password.getText())) {

                signInButton.setEnabled(true);
                signInButton.setTextColor(Color.rgb(255, 255, 255));

            }else {

                signInButton.setEnabled(false);
                signInButton.setTextColor(Color.argb(60, 255, 255, 255));

            }

        }else {

            signInButton.setEnabled(false);
            signInButton.setTextColor(Color.argb(60, 255, 255, 255));

        }
    }*/

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    private void checkEmailAndPassword() {

        if ( (phone.getText().length() >= 7)) {

            if (password.getText().toString().length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                signInButton.setEnabled(false);
                signInButton.setTextColor(Color.argb(60, 255, 255, 255));

                //TODO : LOGIN VERIFICATION

                firebaseAuth.signInWithEmailAndPassword(phone.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    mainIntent();

                                    Toast.makeText(getActivity(), "Log-in successful", Toast.LENGTH_SHORT).show();

                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInButton.setEnabled(true);
                                    signInButton.setTextColor(Color.rgb(255, 255, 255));


                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }else {
                password.setError("Password can't be empty!");
            }

        }else {

            phone.setError("Invalid Mobile/Phone!");

        }
    }

    private void mainIntent() {

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        //Toast.makeText(getContext(), "Sign in Successful", Toast.LENGTH_SHORT).show();
        getActivity().finish();

    }

}
