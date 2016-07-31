package tech.salroid.filmy.CustomAdapter;

/**
 * Created by Home on 7/30/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.R;



/**
 * Created by Home on 7/20/2016.
 */
public class SavedMoviesAdapter extends RecyclerView.Adapter<SavedMoviesAdapter.Vh> {

    private final LayoutInflater inflater;
    Context context;
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
        Vh holder = new Vh(view);

        return holder;
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
        Glide.with(context).load(movie_poster).into(holder.poster);


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


    class Vh extends RecyclerView.ViewHolder {

        TextView title;
        ImageView poster;
        FrameLayout main;

        public Vh(View itemView) {

            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            main = (FrameLayout) itemView.findViewById(R.id.main);


            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dataCursor.moveToPosition(getPosition());

                    int movie_id_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID);
                    int movie_title_index = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE);

                    if (clickListener != null) {
                        clickListener.itemClicked(dataCursor.getString(movie_id_index),dataCursor.getString(movie_title_index));
                    }

                }
            });

            main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    dataCursor.moveToPosition(getPosition());


                    if (longclickListener != null) {
                        longclickListener.itemLongClicked(dataCursor,getPosition());
                    }

                    return true;
                }
            });

            }
        }



    public interface ClickListener {

        public void itemClicked(String id,String title);

    }

    public interface LongClickListener{
        public void itemLongClicked(Cursor cursor,int position);

    }

    public void setLongClickListener(LongClickListener longclickListener) {
        this.longclickListener = longclickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
