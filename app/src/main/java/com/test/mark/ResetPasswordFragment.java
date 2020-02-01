package com.test.mark;


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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {


    private EditText phone;
    private Button resetPassword;
    private TextView goBack, noticeForgotPassword, titleForgotPassword;
    FrameLayout parentFrameLayout;
    ProgressBar progressBar;

    //TODO : OTP verification part

    private EditText txtOTP;
    private Button confirmOTPButton;
    private ProgressBar confirmOTPProgressBar;

    private TextView noticeNewPassword;
    private EditText txtNewPassword;
    private EditText txtConfirmNewPassword;
    private Button saveNewPasswordButton;
    private ProgressBar newPasswordProgressBar;

    private ImageView done;
    private TextView email_sent;

    private FirebaseAuth firebaseAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        goBack  = view.findViewById(R.id.go_back);
        phone  = view.findViewById(R.id.phone_forgot_password);
        resetPassword  = view.findViewById(R.id.reset_password_button);

        progressBar  = view.findViewById(R.id.reset_password_progress_bar);
        done = view.findViewById(R.id.email_sent_icon);
        email_sent = view.findViewById(R.id.txt_email_sent);

        noticeForgotPassword = view.findViewById(R.id.forgot_password_notice);
        titleForgotPassword = view.findViewById(R.id.forgot_password_title);

        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);

        // TODO: OTP part

        txtOTP = view.findViewById(R.id.reset_password_txt_otp);
        confirmOTPButton = view.findViewById(R.id.reset_password_confirm_otp_button);
        confirmOTPProgressBar = view.findViewById(R.id.reset_password_progress_bar);

        noticeNewPassword = view.findViewById(R.id.reset_password_new_password_notice);
        txtNewPassword = view.findViewById(R.id.reset_password_new_password_txt);
        txtConfirmNewPassword = view.findViewById(R.id.reset_password_confirm_new_password_txt);
        saveNewPasswordButton = view.findViewById(R.id.reset_save_new_password_button);
        newPasswordProgressBar = view.findViewById(R.id.reset_password_new_password_progress_bar);





        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //go-back textView task
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        //sign-in button task
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmail();

            }
        });

        //Confirm otp button task
        confirmOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkOTPAndSetNewPassword();
            }
        });

        saveNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: check the tasks here or call some function if required
                setFragment(new SignInFragment());
            }
        });
    }

    private void checkOTPAndSetNewPassword() {

        if (txtOTP.getText().toString().length() != 0)
        {
            confirmOTPProgressBar.setVisibility(View.VISIBLE);
            confirmOTPButton.setEnabled(false);
            resetPassword.setTextColor(Color.argb(60, 255, 255, 255));
            done.setVisibility(View.INVISIBLE);
            email_sent.setVisibility(View.INVISIBLE);

            //TODO : Send OTP back to server and get verification status


            //if successful

            noticeForgotPassword.setVisibility(View.INVISIBLE);
            titleForgotPassword.setVisibility(View.INVISIBLE);
            txtOTP.setVisibility(View.INVISIBLE);
            confirmOTPButton.setVisibility(View.INVISIBLE);


            confirmOTPProgressBar.setVisibility(View.INVISIBLE);

            noticeNewPassword.setVisibility(View.VISIBLE);
            txtNewPassword.setVisibility(View.VISIBLE);
            txtConfirmNewPassword.setVisibility(View.VISIBLE);
            saveNewPasswordButton.setVisibility(View.VISIBLE);




            //setFragment(new SignInFragment());



        }
        else {
            txtOTP.setError("Invalid OTP");
        }
    }

    private void checkEmail() {
        if (phone.getText().toString().length() >= 7) {

            progressBar.setVisibility(View.VISIBLE);
            resetPassword.setEnabled(false);
            resetPassword.setTextColor(Color.argb(60, 255, 255, 255));

            firebaseAuth.sendPasswordResetEmail(phone.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                progressBar.setVisibility(View.INVISIBLE);
                                phone.setVisibility(View.INVISIBLE);
                                resetPassword.setVisibility(View.INVISIBLE);

                                txtOTP.setVisibility(View.VISIBLE);
                                done.setVisibility(View.VISIBLE);
                                email_sent.setVisibility(View.VISIBLE);
                                confirmOTPButton.setVisibility(View.VISIBLE);       // OTP button enabled

                                confirmOTPProgressBar.setVisibility(View.INVISIBLE);



                                //Toast.makeText(getActivity(), "Email sent successfully!", Toast.LENGTH_SHORT).show();

                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                resetPassword.setEnabled(true);
                                resetPassword.setTextColor(Color.rgb(255, 255, 255));


                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }else {
            phone.setError("Invalid Mobile/Phone!");
        }
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

}
