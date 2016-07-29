package tech.salroid.filmy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by R Ankit on 29-07-2016.
 */

public class FullReadFragment extends Fragment implements View.OnClickListener {

    TextView title,desc;
    String titleValue,descValue;
    ImageView crossButton;
    private int dyn_Value;
    private RelativeLayout fragment_desc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.read_full_layout,container,false);

        title = (TextView) view.findViewById(R.id.textViewTitle);
        desc = (TextView) view.findViewById(R.id.textViewDesc);
        crossButton = (ImageView) view.findViewById(R.id.cross);
        fragment_desc=(RelativeLayout)view.findViewById(R.id.fragment_desc);
        crossButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleValue = getArguments().getString("title"," ");
        descValue = getArguments().getString("desc"," ");
        dyn_Value=getArguments().getInt("dynam",0);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText(titleValue);
        desc.setText(descValue);
        fragment_desc.setBackgroundColor(dyn_Value);
    }

    @Override
    public void onClick(View view) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
