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
import tech.salroid.filmy.data_classes.WatchlistData;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.Dh> {

    private final LayoutInflater inflater;
    private List<WatchlistData> data;
    private Context fro;
    private ClickListener clickListener;
    private String fav_title, fav_id, fav_poster;
    private WatchlistAdapter.LongClickListener longClickListener;

    public WatchlistAdapter(Context context, List<WatchlistData> data) {
        inflater = LayoutInflater.from(context);
        fro = context;
        this.data = data;
    }

    @Override
    public Dh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new Dh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistAdapter.Dh holder, int position) {

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

            main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (longClickListener != null) {
                        longClickListener.itemLongClicked(data.get(getPosition()), getPosition());
                    }

                    return true;
                }
            });
        }
    }
}
