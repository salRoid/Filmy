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
import tech.salroid.filmy.data_classes.SearchData;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.Dh> {

    private final LayoutInflater inflater;
    private List<SearchData> data;
    private Context fro;
    private ClickListener clickListener;


    public SearchResultAdapter(Context context, List<SearchData> data) {
        inflater = LayoutInflater.from(context);
        fro = context;
        this.data = data;
    }

    @Override
    public Dh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_search, parent, false);
        return new Dh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Dh holder, int position) {

        String query_name = data.get(position).getMovie();
        String query_id = data.get(position).getId();
        String query_poster = data.get(position).getPoster();
        String query_type = data.get(position).getType();
        String query_date = data.get(position).getDate();
        String query_extra = data.get(position).getExtra();

        holder.movie_name.setText(query_name);

        if (!query_date.equals("null"))
            holder.date.setText(query_date);
        else {
            holder.date.setVisibility(View.INVISIBLE);
        }

        try {
            Glide.with(fro).load(query_poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.movie_poster);
        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemClicked(SearchData searchData, int position);
    }

    class Dh extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView movie_name;
        @BindView(R.id.poster)
        ImageView movie_poster;
        @BindView(R.id.date)
        TextView date;
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
