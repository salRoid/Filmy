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
import tech.salroid.filmy.data.CrewMemberDetailsData;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.Ho> {

    private final Boolean ret_size;
    private List<CrewMemberDetailsData> crewList;
    private Context context;
    private ClickListener clickListener;

    public CrewAdapter(Context context, List<CrewMemberDetailsData> crewList, Boolean size) {
        this.context = context;
        this.crewList = crewList;
        this.ret_size = size;
    }

    @Override
    public Ho onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.crew_custom_row, parent, false);
        return new Ho(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Ho holder, int position) {

        String id = crewList.get(position).getCrewMemberId();
        String name = crewList.get(position).getCrewMemberName();
        String job = crewList.get(position).getCrewMemberJob();
        String displayProfile = crewList.get(position).getCrewMemberProfile();

        holder.crewMemberName.setText(name);
        holder.crewMemberJob.setText(job);

        try {
            Glide.with(context)
                    .load(displayProfile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.crewMemberProfile);
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        if (ret_size) {
            return (crewList.size() >= 5) ? 5 : crewList.size();
        } else {
            return crewList.size();
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemClicked(CrewMemberDetailsData setterGetter, int position, View view);
    }

    class Ho extends RecyclerView.ViewHolder {

        @BindView(R.id.crew_name)
        TextView crewMemberName;
        @BindView(R.id.crew_description)
        TextView crewMemberJob;
        @BindView(R.id.crew_poster)
        CircularImageView crewMemberProfile;

        Ho(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(crewList.get(getPosition()), getPosition(), view);
                    }
                }
            });
        }
    }
}