package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;

/**
 * Created by salroid on 4/16/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VH> {

    private String[] trailers;
    private String[] trailers_name;
    private Context context;

    public TrailerAdapter(String[] trailers, String[] trailers_name, Context context) {
        this.trailers = trailers;
        this.trailers_name = trailers_name;
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.single_trailer_view, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        String trailer_id = trailers[position];
        String name = trailers_name[position];
        String trailer_link = context.getResources().getString(R.string.trailer_link_prefix) + trailer_id;
        String trailer_thumbnail = context.getResources().getString(R.string.trailer_img_prefix) + trailer_id
                + context.getResources().getString(R.string.trailer_img_suffix);

        try {
            Glide.with(context)
                    .load(trailer_thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.youtube_thumbnail);
        } catch (Exception e) {
        }

        holder.trailerTitle.setText(name);


    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }


    public class VH extends RecyclerView.ViewHolder {

        @BindView(R.id.detail_youtube)
        ImageView youtube_thumbnail;
        @BindView(R.id.trailerTitle)
        TextView trailerTitle;

        VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
