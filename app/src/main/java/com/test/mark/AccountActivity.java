package com.test.mark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class AccountActivity extends AppCompatActivity {

    private FrameLayout frameLayout;    //to display sign-in and-sign-up fragment
    public static boolean onResetPasswordFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        //sets fragment to frame layout by invoking setFragment() function
        frameLayout = findViewById(R.id.register_frameLayout);
        setDefaultFragment(new SignInFragment());


    }

    private void setDefaultFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);

        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (onResetPasswordFragment) {

                onResetPasswordFragment = false;        //so that user doesn't keep looping on login screen after pressing back each time
                setFragment(new SignInFragment());

                return false;

            }

        }

        return super.onKeyDown(keyCode, event);
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }
}
