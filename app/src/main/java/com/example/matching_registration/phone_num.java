package com.example.matching_registration;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class phone_num extends Fragment {

    private View view;
    private EditText editTextPhoneNo;
    private EditText editTextVerificationCode;
    private Button sendSMS;
    //    private Button verify;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private boolean isVerificationSuccessful = false;
    private MenuItem menuItem;

    // Required empty public constructor
    public phone_num() {
    }


    public interface VerificationListener {
        void onVerificationResult(boolean isSuccessful);
    }

//    public boolean isViewCreated() {
//        return isViewCreated;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.phone_num, container, false);
        sendSMS = view.findViewById(R.id.sendSMS);
        // Set up any necessary UI components or logic for the second page
        editTextPhoneNo = view.findViewById(R.id.editTextPhoneNo); // get phone number
        editTextVerificationCode = view.findViewById(R.id.editTextVerificationCode);
        mAuth = FirebaseAuth.getInstance();
        menuItem = view.findViewById(R.id.menu_next);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.e(TAG, "Successfully verified");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "Failed to verify: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        mVerificationId = verificationId;
                    }
                };


        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //function of send SMS should be around here
                //
                String phoneNumber = "+852 " + editTextPhoneNo.getText().toString();

                // Start phone number verification
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(getActivity())  // Activity (for callback binding)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        sendSMS.setEnabled(false); // Disable the button
                        sendSMS.setText("Resend in " + millisUntilFinished / 1000 + "s"); // Update text every second
                    }

                    public void onFinish() {
                        sendSMS.setText("Resend SMS"); // After 60 seconds, reset button text
                        sendSMS.setEnabled(true); // Enable the button
                    }
                }.start();
            }
        });


        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editTextVerificationCode = view.findViewById(R.id.editTextVerificationCode);
                String code = editTextVerificationCode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.e(TAG, "Sign in with credential successful");
                                    isVerificationSuccessful = true;
                                } else {
                                    Log.e(TAG, "Sign in with credential failed");
                                }
                            }
                        });
                return false;
            }
        });

//        verify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editTextVerificationCode = view.findViewById(R.id.editTextVerificationCode);
//                String code = editTextVerificationCode.getText().toString();
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//                mAuth.signInWithCredential(credential)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    Log.e(TAG, "Sign in with credential successful");
//                                    isVerificationSuccessful = true;
//                                } else {
//                                    Log.e(TAG, "Sign in with credential failed");
//
//                                }
//                            }
//                        });
//            }
//        });

        return view;
    }
    public void onMenuNextClicked() {
        editTextVerificationCode = view.findViewById(R.id.editTextVerificationCode);
        String code = editTextVerificationCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Sign in with credential successful");
                            isVerificationSuccessful = true;
                        } else {
                            Log.e(TAG, "Sign in with credential failed");
                        }
                    }
                });
    }


    public EditText getEditTextVerificationCode() {
        return editTextVerificationCode;
    }

//    public void isVerificationSuccessful(VerificationListener listener) {
//        String code = getEditTextVerificationCode().getText().toString();
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.e(TAG, "Sign in with credential successful");
////                            setVerificationStatus(true);
//                            listener.onVerificationResult(true);
//                        } else {
//                            Log.e(TAG, "Sign in with credential failed");
////                            setVerificationStatus(false);
//                            listener.onVerificationResult(false);
//                        }
//                    }
//                });
//    }


//    @Override
//    public void onDestroyView() { // when turn to another page
//        // Could be changed to when a button clicked
//        // NOTE: Lack OTP check
//        super.onDestroyView();
//        String phoneNo = editTextPhoneNo.getText().toString();
//        sendPhoneNo(phoneNo);
//    }

    /**
     * Push phone_no (json) to server.
     * * Not finished yet.
     */
//    private void sendPhoneNo(String phoneNo) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("phoneNo", phoneNo);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//
//        Request request = new Request.Builder() // build request
//                .url("http://TBD.com/tbd") // *TBD
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() { // enqueue
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful())
//                    throw new IOException("Unexpected code " + response);
//            }
//        });
//    }
}