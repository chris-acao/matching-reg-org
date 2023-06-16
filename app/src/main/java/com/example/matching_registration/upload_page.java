
package com.example.matching_registration;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class upload_page extends Fragment {
    TextView stucard;
    Button uploadBtn;
    private ProgressDialog progressDialog;

    private String fname;
    private String lname;
    private String sid;
    private String school;
    private Year expiry;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private Uri imageUri;

    public upload_page() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upload, container, false);


        stucard = rootView.findViewById(R.id.student_card_info);
        String defaulttext = "Student ID Card Info\nName: \nSID:\nSchool Name: \nExpiry year: \n";
        SpannableString stu_info = new SpannableString(defaulttext);
        stu_info.setSpan(new StyleSpan(Typeface.BOLD), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stu_info.setSpan(new RelativeSizeSpan(1.5f), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stucard.setText(stu_info);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        uploadBtn = rootView.findViewById(R.id.btnUpload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        return rootView;
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            startImageCapture();
        }
    }

    private void startImageCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(), "com.example.myapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            return imageFile;
        } catch (IOException e) {
            Log.e("CaptureImage", "Failed to create image file: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                startImageCapture();
            } else {
                Toast.makeText(requireContext(), "Permissions denied. Unable to capture image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            processImage();
        }
    }

    private void processImage() {
        try {

            InputImage image = InputImage.fromFilePath(requireContext(), imageUri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override

                        /**
                         * Parse the effective text from the photo
                         */

                        public void onSuccess(Text result) { // To be modified
                            String detectedText = result.getText();

                            Log.e(TAG, "onSuccess: " + detectedText);
                            ArrayList<String> info = CardParse.parse(detectedText);
                            if (info != null && info.size() >= 3) {
                                Log.i(TAG, "onSuccess: " + "Name:\t\t" + info.get(0));
                                Log.i(TAG, "onSuccess: " + "SID:\t\t\t" + info.get(1));
                                Log.i(TAG, "onSuccess: " + "Exp Year:\t" + info.get(2));
                                Log.i(TAG, "onSuccess: " + "University:\t" + info.get(3));
                                String InfoText = ("Student ID Card Info\nName: "+ info.get(0)+"\nSID:"+info.get(1)+"\nSchool Name: "+info.get(3)+"\nExpiry year: "+info.get(2));
                                SpannableString stu_info = new SpannableString(InfoText);
                                stu_info.setSpan(new StyleSpan(Typeface.BOLD), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                stu_info.setSpan(new RelativeSizeSpan(1.5f), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                stucard.setText(stu_info);
                            }
                            else {
                                Toast.makeText(getContext(), "Unable to extract info, Please try again",Toast.LENGTH_LONG).show();
                                Log.e(TAG,"onSuccess: NULL");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TextRecognition", "Failed to process image: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            Log.e("ProcessImage", "Failed to load image: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss(); // Dismiss progress dialog when fragment is destroyed.
        // Do the sendDataToServer
        sendDataToServer();
    }


    /**
     * Push stu_card info (json) to the server.
     * Not finished yet.
     */
    private void sendDataToServer() {

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();

        try {
            json.put("fname", fname);
            json.put("lname", lname);
            json.put("sid", sid);
            json.put("school", school);
            json.put("expiry", expiry);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://TBD.com/tbd") // *TBD
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("Success", "Data sent to the server successfully");
                } else {
                    Log.e("ServerError", "Failed to send data");
                }
            }
        });
    }

}