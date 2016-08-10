package tech.salroid.filmy.Database;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.HashMap;
import tech.salroid.filmy.R;

/**
 * Created by R Ankit on 06-08-2016.
 */

public class OfflineMovies {

    private Context context;
    private FrameLayout main_content;

    public OfflineMovies(Context context, FrameLayout main_content){

        this.context = context;
        this.main_content = main_content;

    }

    public void saveMovie(HashMap<String,String> movieMap,String movie_id,String movie_id_final) {

        if (movieMap != null && !movieMap.isEmpty()) {


            final ContentValues saveValues = new ContentValues();

            saveValues.put(FilmContract.SaveEntry.SAVE_ID, movie_id_final);
            saveValues.put(FilmContract.SaveEntry.SAVE_TITLE, movieMap.get("title"));
            saveValues.put(FilmContract.SaveEntry.SAVE_TAGLINE, movieMap.get("tagline"));
            saveValues.put(FilmContract.SaveEntry.SAVE_DESCRIPTION, movieMap.get("overview"));
            saveValues.put(FilmContract.SaveEntry.SAVE_BANNER, movieMap.get("banner"));
            saveValues.put(FilmContract.SaveEntry.SAVE_TRAILER, movieMap.get("trailer"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RATING, movieMap.get("rating"));
            saveValues.put(FilmContract.SaveEntry.SAVE_YEAR, movieMap.get("year"));
            saveValues.put(FilmContract.SaveEntry.SAVE_POSTER_LINK, movieMap.get("poster"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RUNTIME, movieMap.get("runtime"));
            saveValues.put(FilmContract.SaveEntry.SAVE_CERTIFICATION, movieMap.get("certification"));
            saveValues.put(FilmContract.SaveEntry.SAVE_LANGUAGE, movieMap.get("language"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RELEASED, movieMap.get("released"));


            final String selection =
                    FilmContract.SaveEntry.TABLE_NAME +
                            "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";
            final String[] selectionArgs = {movie_id};

            //  boolean deletePermission = false;
            Cursor alreadyCursor = context.getContentResolver().query(FilmContract.SaveEntry.CONTENT_URI, null, selection, selectionArgs, null);

            if (alreadyCursor.moveToFirst()) {
                //Already present in databse
                Snackbar.make(main_content, "Already present in database", Snackbar.LENGTH_SHORT).show();

            } else {

                final Cursor returnedCursor = context.getContentResolver().query(FilmContract.SaveEntry.CONTENT_URI, null, null, null, null);


                if (returnedCursor.moveToFirst() && returnedCursor.getCount() == 10) {
                    //No space to fill more. Have to delete oldest entry to save this Agree?

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove");
                    alertDialog.setIcon(R.drawable.ic_delete_sweep_black_24dp);

                    final TextView input = new TextView(context);
                    FrameLayout container = new FrameLayout(context);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(96, 48, 96, 48);
                    input.setLayoutParams(params);

                    input.setText("Save Limit reached , want to remove the oldest movie and save this one ?");
                    input.setTextColor(Color.parseColor("#303030"));

                    container.addView(input);


                    alertDialog.setView(container);
                    alertDialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry._ID + " = ? ";

                                    returnedCursor.moveToFirst();

                                    //Log.d(LOG_TAG, "This is the last index value which is going to be deleted "+returnedCursor.getInt(0));

                                    final String[] deletionArgs = {String.valueOf(returnedCursor.getInt(0))};


                                    long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

                                    if (deletion_id != -1) {

                                        // Log.d(LOG_TAG, "We deleted this row" + deletion_id);

                                        Uri uri = context.getContentResolver().insert(FilmContract.SaveEntry.CONTENT_URI, saveValues);

                                        long movieRowId = ContentUris.parseId(uri);

                                        if (movieRowId != -1) {
                                            //inserted
                                            Snackbar.make(main_content, "Movie Saved", Snackbar.LENGTH_SHORT).show();

                                        } else {

                                            // Log.d(LOG_TAG, "row not Inserted in database");
                                        }

                                    } else {

                                        //delete was unsuccessful
                                    }

                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                } else {

                    Uri uri = context.getContentResolver().insert(FilmContract.SaveEntry.CONTENT_URI, saveValues);

                    long movieRowId = ContentUris.parseId(uri);

                    if (movieRowId != -1) {

                        Snackbar.make(main_content, "Movie Saved", Snackbar.LENGTH_SHORT).show();

                        // Toast.makeText(MovieDetailsActivity.this, "Movie Inserted", Toast.LENGTH_SHORT).show();

                    } else {

                        Snackbar.make(main_content, "Movie Not Saved", Snackbar.LENGTH_SHORT).show();

                    }
                }
            }
        }
    }

}
