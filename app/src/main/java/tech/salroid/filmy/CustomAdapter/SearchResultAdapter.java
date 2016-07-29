package tech.salroid.filmy.CustomAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import tech.salroid.filmy.DataClasses.SearchData;
import tech.salroid.filmy.R;

/**
 * Created by Home on 7/27/2016.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.Dh> {

    private final LayoutInflater inflater;
    List<SearchData> data = new ArrayList<>();
    Context fro;
    private ClickListener clickListener;
    private String query_name,query_type,query_poster, query_id,query_date,query_extra;


    public SearchResultAdapter(Context context ,List<SearchData> data) {
        inflater=LayoutInflater.from(context);
        fro=context;
        this.data=data;

    }

    @Override
    public Dh onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_single_search_result,parent,false);
        Dh holder= new Dh(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(Dh holder, int position) {

        query_name = data.get(position).getMovie();
        query_id=data.get(position).getId();
        query_poster=data.get(position).getPoster();
        query_type=data.get(position).getType();
        query_date=data.get(position).getDate();
        query_extra=data.get(position).getExtra();

        holder.movie_name.setText(query_name);
        holder.movie_date.setText(query_date);
        holder.movie_extra.setText(query_extra);
        Glide.with(fro).load(query_poster).into(holder.movie_poster);

    }

    @Override
    public int getItemCount() {

        return data.size();
    }



    class Dh extends RecyclerView.ViewHolder{

        TextView movie_name,movie_date,movie_extra;
        ImageView movie_poster;

        public Dh(View itemView) {



            super(itemView);

            movie_name  = (TextView) itemView.findViewById(R.id.result_title);
            movie_poster=(ImageView)itemView.findViewById(R.id.poster);
            movie_date=(TextView)itemView.findViewById(R.id.result_date);
            movie_extra=(TextView)itemView.findViewById(R.id.result_extra);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(clickListener!=null){
                        clickListener.itemClicked(data.get(getPosition()),getPosition());
                    }

                }
            });
        }
    }

    public interface ClickListener{

       public void itemClicked(SearchData searchData ,int position);

    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }



}