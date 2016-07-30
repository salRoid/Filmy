package tech.salroid.filmy.CustomAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.DataClasses.CharacterDetailsData;
import tech.salroid.filmy.R;
import tech.salroid.filmy.DataClasses.MovieData;

/**
 * Created by Home on 7/22/2016.
 */
public class CharacterDetailsActivityAdapter extends RecyclerView.Adapter<CharacterDetailsActivityAdapter.Fo> {

    private final LayoutInflater inflater;
    private final Boolean ret_size;
    Context con;
    private ClickListener clickListener;

    List<CharacterDetailsData> ch = new ArrayList<>();

    public CharacterDetailsActivityAdapter(Context context, List<CharacterDetailsData> ch, Boolean size) {
        inflater = LayoutInflater.from(context);
        con = context;
        this.ch = ch;
        this.ret_size = size;

    }


    @Override
    public Fo onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.char_custom_row, parent, false);
        Fo holder = new Fo(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(Fo holder, int position) {

        String m_name, m_profile, m_desc, m_id;

        m_name = ch.get(position).getChar_movie();
        m_profile = ch.get(position).getCharmovie_img();
        m_desc = ch.get(position).getChar_role();
        m_id = ch.get(position).getChar_id();

        // Log.d("webi","charAdapter"+m_id);

        holder.mov_name.setText(m_name);
        holder.mov_char.setText(m_desc);
        Glide.with(con).load(m_profile).fitCenter().into(holder.mov_img);

    }

    @Override
    public int getItemCount() {
        if (ret_size)
            return (ch.size() >= 5) ? 5 : ch.size();

        else
            return ch.size();
    }

    class Fo extends RecyclerView.ViewHolder {

        TextView mov_char, mov_name;
        CircularImageView mov_img;

        public Fo(View itemView) {

            super(itemView);

            mov_img = (CircularImageView) itemView.findViewById(R.id.movie_poster);
            mov_name = (TextView) itemView.findViewById(R.id.movie_name);
            mov_char = (TextView) itemView.findViewById(R.id.movie_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(ch.get(getPosition()), getPosition());
                    }
                }
            });

        }
    }

    public interface ClickListener {

        public void itemClicked(CharacterDetailsData setterGetter, int position);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
