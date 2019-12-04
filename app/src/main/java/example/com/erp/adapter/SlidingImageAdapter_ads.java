package example.com.erp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.model.ImageModel_ads;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SlidingImageAdapter_ads extends PagerAdapter {

    private ArrayList<ImageModel_ads> modelArrayList_ads;
    private LayoutInflater layoutInflater;
    private Context context;
    private String flag;

    public SlidingImageAdapter_ads(ArrayList<ImageModel_ads> imageModelArrayList, Context applicationContext, String flag) {
        this.modelArrayList_ads = imageModelArrayList;
        this.context = applicationContext;
        this.flag = flag;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return modelArrayList_ads.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final ImageModel_ads imageModel = modelArrayList_ads.get(position);
        View view = layoutInflater.inflate(R.layout.slidingimagelayout, null);
        final ImageView imageView = view.findViewById(R.id.images);
        try {
            Picasso.with(context)
                    .load(imageModel.getAd_image_small())
                    /*.placeholder(R.drawable.splash_logo)
                    .error(R.drawable.splash_logo)*/
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            AlertDialog alertDialog;
            Context mContext = context;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dilog_image, null);
            ImageView imageView1 = (ImageView) layout.findViewById(R.id.image_1);
            try {
                Picasso.with(context)
                        .load(imageModel.getAd_image_big())
                        /*.placeholder(R.drawable.splash_logo)
                        .error(R.drawable.splash_logo)*/
                        .into(imageView1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            builder = new AlertDialog.Builder(mContext);
            builder.setView(layout);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            //alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();
        });
        container.addView(view, 0);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
