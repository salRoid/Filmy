package tech.salroid.filmy.custom_adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

public class SavedMoviesAdapter extends RecyclerView.Adapter<SavedMoviesAdapter.Vh> {

    private final LayoutInflater inflater;
    private Context context;
    private ClickListener clickListener;
    private Cursor dataCursor;
    private LongClickListener longclickListener;

    public SavedMoviesAdapter(Context context, Cursor cursor) {

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.dataCursor = cursor;

    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

        String movie_title, imdb_id, movie_poster;
        int movie_year;

        dataCursor.moveToPosition(position);

        int id_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID);
        int title_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE);
        int poster_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK);
        int year_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_YEAR);


        movie_title = dataCursor.getString(title_index);
        movie_poster = dataCursor.getString(poster_index);
        imdb_id = dataCursor.getString(id_index);
        movie_year = dataCursor.getInt(year_index);

        holder.title.setText(movie_title);

        try{
        Glide.with(context).load(movie_poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.poster);

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

    public void setLongClickListener(LongClickListener longclickListener) {
        this.longclickListener = longclickListener;
    }

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

    public interface ClickListener {

        void itemClicked(String id, String title);

    }

    public interface LongClickListener {
        void itemLongClicked(Cursor cursor, int position);

    }

    class Vh extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.main)
        FrameLayout main;


        Vh(View itemView) {

            super(itemView);
            ButterKnife.bind(this,itemView);

            ButterKnife.bind(this,itemView);

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dataCursor.moveToPosition(getPosition());

                    int movie_id_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID);
                    int movie_title_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE);

                    if (clickListener != null) {
                        clickListener.itemClicked(dataCursor.getString(movie_id_index),
                                dataCursor.getString(movie_title_index));
                    }

                }
            });

            main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    dataCursor.moveToPosition(getPosition());


                    if (longclickListener != null) {
                        longclickListener.itemLongClicked(dataCursor, getPosition());
                    }

                    return true;
                }
            });

        }
    }

}