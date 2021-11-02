package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.data_classes.PersonMovieDetailsData;
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

public class CharacterDetailsActivityAdapter extends RecyclerView.Adapter<CharacterDetailsActivityAdapter.Fo> {

    private final LayoutInflater inflater;
    private final Boolean ret_size;
    private Context context;
    private List<PersonMovieDetailsData> moviesList;
    private ClickListener clickListener;

    public CharacterDetailsActivityAdapter(Context context, List<PersonMovieDetailsData> moviesList, Boolean size) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.moviesList = moviesList;
        this.ret_size = size;
    }

    @Override
    public Fo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.char_custom_row, parent, false);

        return new Fo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Fo holder, int position) {
        String movieName = moviesList.get(position).getMovieTitle();
        String moviePoster = moviesList.get(position).getMoviePoster();
        String rolePlayed = moviesList.get(position).getRolePlayed();

        holder.movieName.setText(movieName);
        holder.rolePlayed.setText(rolePlayed);

        try {
            Glide.with(context)
                    .load(moviePoster)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.moviePoster);
        } catch (Exception e) {
        }
    }

    @Override

    public int getItemCount() {
        if (ret_size)
            return (moviesList.size() >= 5) ? 5 : moviesList.size();

        else
            return moviesList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void itemClicked(PersonMovieDetailsData setterGetter, int position);

    }

    class Fo extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster)
        CircularImageView moviePoster;
        @BindView(R.id.movie_name)
        TextView movieName;
        @BindView(R.id.movie_role_played)
        TextView rolePlayed;

        Fo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(moviesList.get(getPosition()), getPosition());
                    }
                }
            });

        }
    }

}
