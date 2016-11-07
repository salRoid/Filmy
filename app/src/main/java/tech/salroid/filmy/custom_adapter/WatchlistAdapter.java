package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.data_classes.WatchlistData;

/**
 * Created by salroid on 11/7/2016.
 */

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.Dh> {

    private final LayoutInflater inflater;
    private List<WatchlistData> data = new ArrayList<>();
    private Context fro;
    private ClickListener clickListener;
    private String fav_title, fav_id, fav_poster;


    public WatchlistAdapter(Context context, List<WatchlistData> data) {
        inflater = LayoutInflater.from(context);
        fro = context;
        this.data = data;
    }

    @Override
    public Dh onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new Dh(view);
    }

    @Override
    public void onBindViewHolder(WatchlistAdapter.Dh holder, int position) {

        fav_title = data.get(position).getFav_title();
        fav_id = data.get(position).getFav_id();
        fav_poster = data.get(position).getFav_poster();

        holder.movie_name.setText(fav_title);

        try {
            Glide.with(fro).load(fav_poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.movie_poster);
        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public void setClickListener(WatchlistAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void itemClicked(WatchlistData favouriteData, int position);

    }

    class Dh extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView movie_name;
        @BindView(R.id.poster)
        ImageView movie_poster;
        @BindView(R.id.main)
        FrameLayout main;

        Dh(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (clickListener != null) {
                        clickListener.itemClicked(data.get(getPosition()), getPosition());
                    }

                }
            });
        }
    }
}
