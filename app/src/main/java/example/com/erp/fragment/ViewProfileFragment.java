package example.com.erp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import example.com.erp.R;
import example.com.erp.model.userInfo;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class ViewProfileFragment extends Fragment {
    //Wigdet
    View rootView;
    private TextView txtFirmName, txtPersonName, txtStreetNo, txtStreetName, txtLocation, txtAddress, txtPincode,
            txtGst, txtCity;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_view_profile, container, false);
        progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        init();
        return rootView;
    }

    private void init() {
        txtFirmName = rootView.findViewById(R.id.txt_firm_name);
        txtPersonName = rootView.findViewById(R.id.txt_person_name);
        txtStreetNo = rootView.findViewById(R.id.txt_street_no);
        txtStreetName = rootView.findViewById(R.id.txt_street_name);
        txtLocation = rootView.findViewById(R.id.txt_location);
        txtAddress = rootView.findViewById(R.id.txt_address);
        txtPincode = rootView.findViewById(R.id.txt_pincode);
        txtGst = rootView.findViewById(R.id.txt_gst);
        txtCity = rootView.findViewById(R.id.txt_city);

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInformation();
    }

    private void getUserInformation() {
        showProgress();
        RPC.getUserInformation(SharedPreference.getString(Constants.MobileNumber), SharedPreference.getString(Constants.Fcm), 0.0, 0.0, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                userInfo user = (userInfo) object;
                Log.e("JsonData", new Gson().toJson(user));
                loadUserDetails(user);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(getActivity(), "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(userInfo user) {
        if (user == null)
            return;

        txtFirmName.setText(user.getUser_full_name());
        txtPersonName.setText(user.getUser_person_name());
        txtStreetNo.setText(user.getUser_street());
        txtStreetName.setText(user.getUser_street_name());
        txtLocation.setText(user.getUser_locality());
        txtAddress.setText(user.getUser_address());
        txtPincode.setText(user.getUser_pin());
        txtGst.setText(user.getGst());
        txtCity.setText(user.getUser_city());
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }
}