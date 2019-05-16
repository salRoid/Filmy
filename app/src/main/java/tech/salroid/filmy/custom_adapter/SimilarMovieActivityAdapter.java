package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import tech.salroid.filmy.data_classes.SimilarMoviesData;

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

public class SimilarMovieActivityAdapter extends RecyclerView.Adapter<SimilarMovieActivityAdapter.Ho> {

    private List<SimilarMoviesData> similar;
    private Context context;
    private SimilarMovieActivityAdapter.ClickListener clickListener;


    public SimilarMovieActivityAdapter(Context context, List<SimilarMoviesData> similar, Boolean size) {
        this.context = context;
        this.similar = similar;
    }


    @Override
    public Ho onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.similar_custom_row, parent, false);
        return new Ho(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Ho holder, int position) {

        String similar_title = similar.get(position).getMovie_title();
        String similar_banner = similar.get(position).getMovie_banner();
        String similar_id = similar.get(position).getMovie_id();

        holder.title.setText(similar_title);
        try {
            Glide.with(context)
                    .load(similar_banner)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.poster);
        } catch (Exception e){
        }
    }


    @Override
    public int getItemCount() {
            return similar.size();

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }




    public interface ClickListener {

        void itemClicked(SimilarMoviesData setterGetter, int position, View view);

    }

    class Ho extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.poster)
        ImageView poster;

        Ho(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(similar.get(getPosition()), getPosition(),view);
                    }
                }
            });
        }
    }


}


