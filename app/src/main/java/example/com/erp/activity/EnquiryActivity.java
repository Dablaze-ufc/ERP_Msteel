package example.com.erp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import example.com.erp.BuildConfig;
import example.com.erp.R;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class EnquiryActivity extends AppCompatActivity {

    final int PERMISSION_FOR_IMAGE_GALARY = 100;
    final int PERMISSION_FOR_IMAGE_CAMERA = 101;
    final int PERMISSION_FOR_READ_WRITE = 102;

    EditText enquiryEditTxt;
    TextView uploadTxt;
    ImageView imageView;
    LinearLayout loaderLinearLayout;
    Button sendBtn;

    String imageFilePath, selectedImageStr = "";

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String filePath;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }

    // TODO :- Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);

        registerIds();
    }

    private void registerIds() {
        enquiryEditTxt = findViewById(R.id.enquiryEditTxt);
        uploadTxt = findViewById(R.id.uploadTxt);
        imageView = findViewById(R.id.imageView);
        loaderLinearLayout = findViewById(R.id.loaderLinearLayout);
        sendBtn = findViewById(R.id.sendBtn);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                dialog.dismiss();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void takePhotoFromCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("Error");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, PERMISSION_FOR_IMAGE_CAMERA);
            }
        }
    }

    public void choosePhotoFromGallary() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PERMISSION_FOR_IMAGE_GALARY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName /* prefix */, ".jpg"/* suffix */, storageDir /* directory */);

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    // TODO :- Button click

    private String getCurrentDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
        return formatter.format(todayDate);
    }

    public void clickToBack(View view) {
        finish();
    }

    public void clickToSelectImage(View view) {
        if (isReadStoragePermissionGranted()) {
            showPictureDialog();
        }
    }

    public void clickToSend(View view) {
        if (enquiryEditTxt.getText().toString().trim().equals("") && selectedImageStr.equals("")) {
            Toast.makeText(EnquiryActivity.this, "Please fill at least one field", Toast.LENGTH_SHORT).show();
        } else {
            loaderLinearLayout.setVisibility(View.VISIBLE);
            sendBtn.setEnabled(false);
            serviceCallToRegisterUser();
        }
    }

    // TODO :- Permission
    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "Permission is granted1");
                showPictureDialog();
                return true;
            } else {
                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FOR_READ_WRITE);
                return false;
            }
        } else {
            Log.v("Permission", "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_IMAGE_GALARY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromGallary();
                } else {
                    System.out.println("Permission denite");
                }
                return;
            }

            case PERMISSION_FOR_READ_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPictureDialog();
                } else {
                    System.out.println("Permission denite");
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_FOR_IMAGE_GALARY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        selectedImageStr = data.getData().toString();
                        imageView.setImageURI(data.getData());
                        uploadTxt.setVisibility(View.GONE);
                        break;
                }
                break;

            case PERMISSION_FOR_IMAGE_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri selectedImage = Uri.parse(imageFilePath);
                        selectedImageStr = "file:///" + selectedImage.toString();
                        imageView.setImageURI(Uri.parse(imageFilePath));
                        uploadTxt.setVisibility(View.GONE);
                        break;
                }
                break;
        }
    }

    // TODO :- Api
    public void serviceCallToRegisterUser() {
        AsyncHttpClient client = new AsyncHttpClient();
        //client.setResponseTimeout(100000);
        RequestParams param = new RequestParams();
        File myFile = new File(getRealPathFromURI(EnquiryActivity.this, Uri.parse(selectedImageStr)));

        try {
            param.put("transaction_date", getCurrentDate());
            param.put("user_id", SharedPreference.getString(Constants.UserId));
            param.put("enquiry_details", enquiryEditTxt.getText().toString());
            param.put("enquiry_file", myFile, "image/*");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(ApiClient.BASE_URL + "api/customer/order_enquiry_submit.php", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                loaderLinearLayout.setVisibility(View.GONE);
                String response = new String(responseBody, StandardCharsets.UTF_8);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    response = jsonObject.getString("message");
                    Toast.makeText(EnquiryActivity.this, response, Toast.LENGTH_SHORT).show();
                    // sendBtn.setEnabled(true);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loaderLinearLayout.setVisibility(View.GONE);
                sendBtn.setEnabled(true);
                String response = null;
                try {
                    response = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(response);
                    response = jsonObject.getString("message");
                    Toast.makeText(EnquiryActivity.this, response, Toast.LENGTH_SHORT).show();
                    sendBtn.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
