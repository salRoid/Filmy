package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.data.WatchlistData;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.Dh> {

    private final LayoutInflater inflater;
    private List<WatchlistData> data;
    private Context context;
    private ClickListener clickListener;
    private String title, id, poster;
    private WatchlistAdapter.LongClickListener longClickListener;

    public WatchlistAdapter(Context context, List<WatchlistData> data) {
        inflater = LayoutInflater.from(context);
        context = context;
        this.data = data;
    }

    @Override
    public Dh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new Dh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistAdapter.Dh holder, int position) {

        title = data.get(position).getTitle();
        id = data.get(position).getId();
        poster = data.get(position).getPoster();

        holder.movie_name.setText(title);

        try {
            Glide.with(context).load(poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.movie_poster);
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

    public void setLongClickListener(WatchlistAdapter.LongClickListener clickListener) {
        this.longClickListener = clickListener;
    }

    public interface LongClickListener {

        void itemLongClicked(WatchlistData watchlistData, int position);

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

            main.setOnLongClickListener(view -> {
                if (longClickListener != null) {
                    longClickListener.itemLongClicked(data.get(getPosition()), getPosition());
                }
                return true;
            });
        }
    }
}
