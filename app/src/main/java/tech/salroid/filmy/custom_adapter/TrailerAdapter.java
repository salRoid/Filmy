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

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;

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

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VH> {

    private String[] trailers;
    private String[] trailers_name;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public TrailerAdapter(String[] trailers, String[] trailers_name, Context context) {
        this.trailers = trailers;
        this.trailers_name = trailers_name;
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.single_trailer_view, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        String trailer_id = trailers[position];
        String name = trailers_name[position];
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class VH extends RecyclerView.ViewHolder {

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

                    if (onItemClickListener!=null)
                        onItemClickListener.itemClicked(trailers[getAdapterPosition()]);
                }
            });
        }
    }

   public interface OnItemClickListener {
        void itemClicked(String trailerId);
    }


}
