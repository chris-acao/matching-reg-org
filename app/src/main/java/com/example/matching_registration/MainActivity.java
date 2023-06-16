package com.example.matching_registration;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private NonSwipeableViewPager viewPager;
    private CustomPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        viewPager = findViewById(R.id.viewPager);
        viewPager.setSwipeEnabled(false); // Disable swiping between pages
        pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TextView stucard = findViewById(R.id.student_card_info);
        String defaultText = "Student ID Card \nName: \nChinese Name: \nSID:\nSchool Name: \nExpiry year: \n";
        SpannableString stuInfo = new SpannableString(defaultText);
        stuInfo.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stuInfo.setSpan(new RelativeSizeSpan(1.5f), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stucard.setText(stuInfo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_next) {
            int currentPage = viewPager.getCurrentItem();
            if (currentPage == 1) {
                Fragment currentFragment = pagerAdapter.getItem(currentPage);
                ((phone_num) currentFragment).onMenuNextClicked();
                viewPager.setCurrentItem(2, true);
                return true;
                // if verified successfully, go to next page
//                phone_num phoneNumFragment = (phone_num) pagerAdapter.getItem(currentPage);
//                EditText editTextVerificationCode = phoneNumFragment.getEditTextVerificationCode();

//                phone_num phoneNumFragment = (phone_num) pagerAdapter.getItem(1);
//                if (phoneNumFragment.isViewCreated()) {
//                    phoneNumFragment.isVerificationSuccessful(new phone_num.VerificationListener() {
//                        @Override
//                        public void onVerificationResult(boolean isSuccessful) {
//                            if (isSuccessful) {
//                                viewPager.setCurrentItem(2, true);
//                                Log.e(TAG, "onSuccess: " + "Verification successful");
//                            } else {
//                                Log.e(TAG, "onSuccess: " + "Verification failed");
//                                // turn the text box red
//                            }
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "onOptionsItemSelected: " + "View not created yet");
//                }
            }
            else if (currentPage != 2) {
                int nextPage = (currentPage + 1) % pagerAdapter.getCount();
                viewPager.setCurrentItem(nextPage, true);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}