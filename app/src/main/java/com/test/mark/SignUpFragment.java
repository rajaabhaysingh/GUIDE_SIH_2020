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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private TextView signIn;
    FrameLayout parentFrameLayout;

    //secret key string
    private String secretKey;


    private EditText phone, fullName, password, confirmPassword;
    private ImageButton closeButton;
    private Button signUpButton;
    private ProgressBar progressBar;

    // TODO : OTP Variables

    private Button confirmOTPButton;
    private ProgressBar confirmOTPProgressBar;
    private EditText otpNumber;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //TODO : Collecting OTP Variables

        confirmOTPButton = view.findViewById(R.id.sign_up_page_otp_confirm_button);
        confirmOTPProgressBar = view.findViewById(R.id.sign_up_page_otp_progress_bar);
        otpNumber = view.findViewById(R.id.sign_up_page_txt_otp);

        //collecting form details variables
        phone = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);

        closeButton = view.findViewById(R.id.sign_up_close_button);
        signUpButton = view.findViewById(R.id.sign_up_button);

        progressBar = view.findViewById(R.id.sign_up_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signIn  = view.findViewById(R.id.txt_sign_in);

        if (signIn == null)
            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();

        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //bottom sign-in listener
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        //close button function
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });


        //set button:enabled mode false before using these functions
        //validate login form field input
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

        fullName.addTextChangedListener(new TextWatcher() {
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
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
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

        //sign-up button task
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailAndPassword();

            }
        });
    }

    private void checkEmailAndPassword() {

        if (fullName.getText().toString().length() >= 4) {

            if (phone.getText().toString().length() == 10) {

                if(!password.getText().toString().equals("") && password.getText().toString().length() >= 8) {

                    if (!confirmPassword.getText().toString().equals("")) {

                        if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                            progressBar.setVisibility(View.VISIBLE);    //so that user doesn't click SIGN-UP button multiple times and send data multiple times
                            signUpButton.setEnabled(false);
                            signUpButton.setTextColor(Color.argb(60, 255, 255, 255));

                            //TODO : Sign Up Function

                            firebaseAuth.createUserWithEmailAndPassword(phone.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {

                                                Map<Object, String> userdata = new HashMap<>();
                                                userdata.put("fullname", fullName.getText().toString());

                                                firebaseFirestore.collection("USERS")
                                                        .add(userdata)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if (task.isSuccessful()) {

                                                                    phone.setVisibility(View.INVISIBLE);
                                                                    fullName.setVisibility(View.INVISIBLE);
                                                                    password.setVisibility(View.INVISIBLE);
                                                                    confirmPassword.setVisibility(View.INVISIBLE);
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    signUpButton.setVisibility(View.INVISIBLE);

                                                                    confirmOTPProgressBar.setVisibility(View.VISIBLE);
                                                                    confirmOTPButton.setVisibility(View.VISIBLE);
                                                                    otpNumber.setVisibility(View.VISIBLE);

                                                                    //TODO: Uncomment main intent

                                                                    //mainIntent();

                                                                    Toast.makeText(getActivity(), "Sign-up successful", Toast.LENGTH_SHORT).show();

                                                                }else {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    signUpButton.setEnabled(true);
                                                                    signUpButton.setTextColor(Color.rgb(255, 255, 255));


                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                            }else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                signUpButton.setEnabled(true);
                                                signUpButton.setTextColor(Color.rgb(255, 255, 255));


                                                String error = task.getException().getMessage();
                                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });

                        }else {

                            confirmPassword.setError("Password doesn't match!");

                        }

                    }else {
                        confirmPassword.setError("Password doesn't match!");
                    }

                }else {
                    password.setError("At least 8 characters!");
                }

            }else {

                phone.setError("Invalid Mobile/Phone!");

            }

        }else {

            fullName.setError("Name can't be empty!");
        }
    }


    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    //set button:enabled mode false before using these functions
    /*private void checkInputs() {

        if (!TextUtils.isEmpty(email.getText())) {

            if (!TextUtils.isEmpty(fullName.getText())) {

                if ((!TextUtils.isEmpty(password.getText())) && password.length() >= 8) {

                    if (!TextUtils.isEmpty(confirmPassword.getText())) {

                        signUpButton.setEnabled(true);
                        signUpButton.setTextColor(Color.rgb(255, 255, 255));

                    }else {

                        signUpButton.setEnabled(false);
                        signUpButton.setTextColor(Color.argb(60, 255, 255, 255));

                    }

                }else {

                    signUpButton.setEnabled(false);
                    signUpButton.setTextColor(Color.argb(60, 255, 255, 255));

                }

            }else {

                signUpButton.setEnabled(false);
                signUpButton.setTextColor(Color.argb(60, 255, 255, 255));

            }

        }else {

            signUpButton.setEnabled(false);
            signUpButton.setTextColor(Color.argb(60, 255, 255, 255));

        }
    }*/


    private void mainIntent() {

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        Toast.makeText(getContext(), "Sign in Successful", Toast.LENGTH_SHORT).show();
        getActivity().finish();

    }

}
