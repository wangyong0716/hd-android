package fm.dian.hdui.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hddata.business.model.HDBanner;
import fm.dian.hdui.app.HDApp;

@SuppressWarnings("deprecation")
public class HDTab1TopImageAdapter extends BaseAdapter {
    Context context;
    List<HDBanner> dataList = new ArrayList<HDBanner>();
    DisplayMetrics metric = new DisplayMetrics();


    public HDTab1TopImageAdapter(Context context) {
        this.context = context;
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
    }

    public void resetData(List<HDBanner> banners) {
        this.dataList = banners;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HDBanner hdBanner = dataList.get(position);

//		final RelativeLayout rl_content = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_splash_activity_image_adapter, null);
//		ImageView imageView = (ImageView) rl_content.findViewById(R.id.image);
//		
//		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//		HDApp.getInstance().imageLoader.displayImage(hdBanner.image, imageView);
//		return rl_content;
//		-------------
        ImageView iv = new ImageView(context);
        iv.setScaleType(ScaleType.CENTER_CROP);
        double h = ((metric.widthPixels / 16.0) * 3.0f);
        iv.setLayoutParams(new Gallery.LayoutParams(metric.widthPixels, (int) h));

        iv.setPadding(0, 0, 0, 0);
        if (hdBanner.image != null && !"".equals(hdBanner.image)) {
            try {
                HDApp.getInstance().imageLoader.displayImage(hdBanner.image, iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return iv;
//		------------------
    }


}
