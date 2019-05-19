package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.database.Cursor;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.database.FilmContract;

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

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.Vh> {

    private final LayoutInflater inflater;
    private Context context;
    private ClickListener clickListener;
    private Cursor dataCursor;

    public MainActivityAdapter(Context context, Cursor cursor) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.dataCursor = cursor;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

        String movie_title, movie_poster;
        String imdb_id;
        int movie_year;

        dataCursor.moveToPosition(position);

        int id_index = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
        int title_index = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);
        int poster_index = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK);
        int year_index = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR);

        movie_title = dataCursor.getString(title_index);
        movie_poster = dataCursor.getString(poster_index);
        imdb_id = dataCursor.getString(id_index);
        movie_year = dataCursor.getInt(year_index);


        holder.title.setText(movie_title + " / " + movie_year);
        //holder.year.setText(String.valueOf(movie_year));

        try {
            Glide.with(context)
                    .load(movie_poster)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.poster);
        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }

    }


    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

        public Cursor swapCursor(Cursor cursor) {
            if (dataCursor == cursor) {
                return null;
            }
            Cursor oldCursor = dataCursor;
            this.dataCursor = cursor;
            if (cursor != null) {
                this.notifyDataSetChanged();
            }
            return oldCursor;
        }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public interface ClickListener {

        void itemClicked(Cursor cursor);

    }

    class Vh extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.main)
        FrameLayout main;

        // TextView year;

        Vh(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //year = (TextView) itemView.findViewById(R.id.movie_year);

            main.setOnClickListener(view -> {
                dataCursor.moveToPosition(getPosition());
                if (clickListener != null) {
                    clickListener.itemClicked(dataCursor);
                }

            });
        }
    }

}
