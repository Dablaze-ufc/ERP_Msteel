package example.com.erp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import example.com.erp.R;
import example.com.erp.activity.MainActivity;
import example.com.erp.activity.SubItemActivity;
import example.com.erp.adapter.SlidingImageAdapter;
import example.com.erp.adapter.TilesListAdapter;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.BaseRespose;
import example.com.erp.model.Group;
import example.com.erp.model.GroupBulletin;
import example.com.erp.model.ImageModel;
import example.com.erp.model.Message;
import example.com.erp.model.Result;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {
    String TAG = "ErrorLoadingData";
    TextView view_transaction, balance_p, balanceView;
    View rootView;
    RecyclerView recyclerView;
    String flag = "FragmentPage";
    private ViewPager viewPager;
    private int currentpages = 0;
    private int NUM_PAGES = 0;
    private int count = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    private TilesListAdapter groupAdapter;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<String> groupsName = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Message message;
    private TextView bulletinText;
    private SlidingImageAdapter slidingImageAdapter;
    private Handler delayhandler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_home, container, false);
        initViews();
        init();
        getViewPagerImages();
        getBulletinData();
        bulletinText = rootView.findViewById(R.id.text_bulletin);
        return rootView;
    }

    private void initViews() {
        imageModelArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        view_transaction = rootView.findViewById(R.id.tran);
        balance_p = rootView.findViewById(R.id.Update_balance);
        balanceView = rootView.findViewById(R.id.balance);
        recyclerView = rootView.findViewById(R.id.group_recyclerview);
        delayhandler = new Handler();

        getGroupList();
        groupAdapter = new TilesListAdapter(groupsName);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(groupAdapter);
        setViewPager();
        setListener();
    }

    private void setListener() {
        groupAdapter.setAnInterface(new CommonClicks() {
            @Override
            public void onItemClick(Object obj) {
                getActivity().startActivity(new Intent(getActivity(), SubItemActivity.class).putExtra("id", groups.get((Integer) obj).getId()).putExtra("title", groups.get((Integer) obj).getGs_group_name()));
            }

            @Override
            public void onChartClick(Object obj) {
                Log.e("Android->>", "onChartClick: work");
            }
        });
    }

    private void setViewPager() {
        viewPager = rootView.findViewById(R.id.pager);
        slidingImageAdapter = new SlidingImageAdapter(imageModelArrayList, getActivity(), flag);
        viewPager.setAdapter(slidingImageAdapter);
        CirclePageIndicator indicator = rootView.findViewById(R.id.idicator);
        indicator.setViewPager(viewPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentpages == NUM_PAGES) {
                    currentpages = 0;
                }
                viewPager.setCurrentItem(currentpages++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (delayhandler != null) {
                    delayhandler.post(update);
                }
            }
        }, 10000, 10000);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentpages = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateValue(String id) {
        progressDialog.setMessage("please wait..");
        progressDialog.show();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiService.updateBalance(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Message message = response.body();
                    if (message.getSuccess().equals("2")) {
                        Result result = message.getResult();
                        try {
                            balanceView.setText(result.getBalance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), (CharSequence) response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getContext(), "data cannot parse..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("Balance", balanceView.getText().toString().trim());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        try {
            String balance = savedInstanceState.getString("Balance");
            balanceView.setText(balance);
        } catch (Exception e) {

        }

    }

    //   Api call for the bulletin data
    private void getBulletinData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseRespose> bulletinCall = apiInterface.getGroupBulletin();
        bulletinCall.enqueue(new Callback<BaseRespose>() {
            @Override
            public void onResponse(Call<BaseRespose> call, Response<BaseRespose> response) {
                BaseRespose responseFromApi = response.body();
                String name = "";
                String trending = "\u2B06";//up
                String longStrings = "";
                ArrayList<String> newStrings = new ArrayList<>();
                ArrayList<String> rate = new ArrayList<>();
                List<GroupBulletin> bulletin = responseFromApi.getGroupBulletin();
                for (GroupBulletin singleBulletin : bulletin) {
                    rate.add(singleBulletin.getRate() + ",");
//                    newStrings.add(singleBulletin.getName() +":" + "  "+singleBulletin.getRate()+"  " + singleBulletin.getTrending() + "  "+" ");
                    newStrings.add(singleBulletin.getName() + ":" + " " + singleBulletin.getRate() + " " + singleBulletin.getTrending() + "  " + ",");
                }
                Log.d(TAG, "onResponse: " + newStrings);

                for (String nameNew : newStrings) {
                    longStrings += nameNew;
                    Log.d(TAG, "onResponse: " + nameNew);
                }
                String[][] replacements = {{"up", "\u2B06"}, {"down", "\u2B07"}};


                String strOutput = longStrings;
                for (String[] replacement : replacements) {
                    strOutput = strOutput.replace(replacement[0], replacement[1]);
                }


                SpannableStringBuilder builder = new SpannableStringBuilder();
                String strArray[] = strOutput.split(",");
                for (int i = 0; i < strArray.length; i++) {
                    if (strArray[i].contains("\u2B07")) {
//                        int e = Integer.parseInt(strArray[i].replaceAll("[\\D]", ""));
//                        String rate4 = String.valueOf(e);
                        SpannableString redString = new SpannableString(strArray[i]);
                        redString.setSpan(new ForegroundColorSpan(Color.RED), strArray[i].length() - 5, strArray[i].length() - 3, 0);
                        builder.append(redString);
                    } else if (strArray[i].contains("\u2B06")) {
                        SpannableString greenString = new SpannableString(strArray[i]);
                        greenString.setSpan(new ForegroundColorSpan(Color.GREEN), strArray[i].length() - 5, strArray[i].length() - 3, 0);
                        builder.append(greenString);

                        {
                            builder.append(strArray[i]);
                        }
                    }
                }


                bulletinText.setText(builder, TextView.BufferType.SPANNABLE);
                Log.d(TAG, "onResponse: " + builder.toString());


                bulletinText.setSelected(true);


            }


            @Override
            public void onFailure(Call<BaseRespose> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());

            }
        });
    }

    private void getGroupList() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String id = "";
        Call<Message> call = apiInterface.getGroupDetail(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    count = 1;
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("2")) {
                            groups.addAll(message.getGroup_name());
                            groupsName.clear();
                            for (Group group : groups) {
                                groupsName.add(group.getGs_group_name());
                            }
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.getInstance(), R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            groupAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.getInstance(), message.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(MainActivity.getInstance(), "Check network connection..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.getInstance(), "data cannot parse..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getViewPagerImages() {
        RPC.getBannerImages(new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                message = (Message) object;
                if (message != null) {
                    if (message.getSuccess().equals("1")) {
                        if (message.getBanner() != null && message.getBanner().size() > 0) {
                            imageModelArrayList = message.getBanner();
                            NUM_PAGES = imageModelArrayList.size();
                            slidingImageAdapter = new SlidingImageAdapter(imageModelArrayList, MainActivity.getInstance(), flag);
                            viewPager.setAdapter(slidingImageAdapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), message.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (delayhandler != null) {
            delayhandler.removeCallbacks(null);
            delayhandler = null;
        }
    }
}
