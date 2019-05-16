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
import tech.salroid.filmy.data_classes.CharacterDetailsData;
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
    private Context con;
    private List<CharacterDetailsData> ch;
    private ClickListener clickListener;

    public CharacterDetailsActivityAdapter(Context context, List<CharacterDetailsData> ch, Boolean size) {
        inflater = LayoutInflater.from(context);
        con = context;
        this.ch = ch;
        this.ret_size = size;
    }


    @Override
    public Fo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.char_custom_row, parent, false);

        return new Fo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Fo holder, int position) {

        String m_name, m_profile, m_desc;
        // String m_id;

        m_name = ch.get(position).getChar_movie();
        m_profile = ch.get(position).getCharmovie_img();
        m_desc = ch.get(position).getChar_role();
        //m_id = ch.get(position).getChar_id();

        // Log.d("webi","charAdapter"+m_id);

        holder.mov_name.setText(m_name);
        holder.mov_char.setText(m_desc);

        try {
            Glide.with(con).load(m_profile).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(holder.mov_img);
        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override

    public int getItemCount() {
        if (ret_size)
            return (ch.size() >= 5) ? 5 : ch.size();

        else
            return ch.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void itemClicked(CharacterDetailsData setterGetter, int position);

    }

    class Fo extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster)
        CircularImageView mov_img;
        @BindView(R.id.movie_name)
        TextView mov_name;
        @BindView(R.id.movie_description)
        TextView mov_char;

        Fo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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

}
