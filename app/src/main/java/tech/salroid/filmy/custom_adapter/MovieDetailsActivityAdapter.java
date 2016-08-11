package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.R;
import tech.salroid.filmy.data_classes.MovieDetailsData;

public class MovieDetailsActivityAdapter extends RecyclerView.Adapter<MovieDetailsActivityAdapter.Ho> {

    private final Boolean ret_size;
    private List<MovieDetailsData> cast = new ArrayList<>();
    private Context context;
    private ClickListener clickListener;

    public MovieDetailsActivityAdapter(Context context, List<MovieDetailsData> cast, Boolean size) {

        this.context = context;
        this.cast = cast;
        this.ret_size = size;

    }

    @Override
    public Ho onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.cast_custom_row, parent, false);
        Ho holder = new Ho(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Ho holder, int position) {

        String ct_name = cast.get(position).getCast_name();
        String ct_desc = cast.get(position).getCast_character();
        String ct_profile = cast.get(position).getCast_profile();
        String ct_id = cast.get(position).getCast_id();

        holder.cast_name.setText(ct_name);
        holder.cast_description.setText(ct_desc);

        Glide.with(context)
                .load(ct_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(holder.cast_poster);
    }


    @Override
    public int getItemCount() {

        if (ret_size)
            return (cast.size() >= 5) ? 5 : cast.size();

        else
            return cast.size();

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void itemClicked(MovieDetailsData setterGetter, int position);

    }

    class Ho extends RecyclerView.ViewHolder {

        TextView cast_name, cast_description;
        CircularImageView cast_poster;

        Ho(View itemView) {

            super(itemView);

            cast_name = (TextView) itemView.findViewById(R.id.cast_name);
            cast_description = (TextView) itemView.findViewById(R.id.cast_description);
            cast_poster = (CircularImageView) itemView.findViewById(R.id.cast_poster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(cast.get(getPosition()), getPosition());
                    }
                }
            });
        }
    }


}