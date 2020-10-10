package tech.salroid.filmy.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.data_classes.CastDetailsData;

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

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.Ho> {

    private final Boolean ret_size;
    private List<CastDetailsData> castList = new ArrayList<>();
    private Context context;
    private ClickListener clickListener;

    public CastAdapter(Context context, List<CastDetailsData> castList, Boolean size) {

        this.context = context;
        this.castList = castList;
        this.ret_size = size;

    }

    @Override
    public Ho onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.cast_custom_row, parent, false);
        return new Ho(view);
    }

    @Override
    public void onBindViewHolder(Ho holder, int position) {

        String castName = castList.get(position).getCastName();
        String castCharacter = castList.get(position).getCastCharacter();
        String castDisplayProfile = castList.get(position).getCastDisplayProfile();
        String castId = castList.get(position).getCastId();

        holder.castName.setText(castName);
        holder.castCharacter.setText(castCharacter);

        try {
            Glide.with(context)
                    .load(castDisplayProfile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.castDisplayProfile);
        } catch (Exception e) {
        }

    }

    @Override
    public int getItemCount() {
        if (ret_size) {
            return Math.min(castList.size(), 5);
        } else {
            return castList.size();
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void itemClicked(CastDetailsData setterGetter, int position, View view);

    }

    class Ho extends RecyclerView.ViewHolder {

        @BindView(R.id.cast_name)
        TextView castName;
        @BindView(R.id.cast_description)
        TextView castCharacter;
        @BindView(R.id.cast_poster)
        CircularImageView castDisplayProfile;

        Ho(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                if (clickListener != null) {
                    clickListener.itemClicked(castList.get(getPosition()), getPosition(), view);
                }
            });
        }
    }
}