package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.data_classes.CastDetailsData;
import tech.salroid.filmy.data_classes.SimilarMoviesData;
import tech.salroid.filmy.fragment.SimilarFragment;

/**
 * Created by Home on 11/1/2016.
 */

public class SimilarMovieActivityAdapter extends RecyclerView.Adapter<SimilarMovieActivityAdapter.Ho> {

    private final Boolean ret_size;
    private List<SimilarMoviesData> similar = new ArrayList<>();
    private Context context;
    private SimilarMovieActivityAdapter.ClickListener clickListener;


    public SimilarMovieActivityAdapter(Context context, List<SimilarMoviesData> similar, Boolean size) {

        this.context = context;
        this.similar = similar;
        this.ret_size = size;

    }


    @Override
    public Ho onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.similar_custom_row, parent, false);
        return new Ho(view);
    }

    @Override
    public void onBindViewHolder(Ho holder, int position) {

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


