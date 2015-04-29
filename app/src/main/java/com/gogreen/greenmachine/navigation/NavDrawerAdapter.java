package com.gogreen.greenmachine.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gogreen.greenmachine.R;

/**
 * Created by jonathanlui on 4/28/15.
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private int profile;
    private String email;
    private Context context;

    public NavDrawerAdapter(String Titles[], int Icons[], String Name, String Email,
                            int Profile, Context passedContext) {
        this.mNavTitles = Titles;
        this.mIcons = Icons;
        this.name = Name;
        this.email = Email;
        this.profile = Profile;
        this.context = passedContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int holderId;

        private TextView textView;
        private ImageView imageView;
        private ImageView profile;
        private TextView name;
        private TextView email;
        private Context context;


        public ViewHolder(View itemView,int ViewType,Context c) {
            super(itemView);
            this.context = c;
            itemView.setClickable(true);
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setOnClickListener(this);

            if(ViewType == TYPE_ROW) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else{
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                holderId = 0;
            }
        }

        @Override
        public void onClick(View v) {
        }
    }

    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ROW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType, context);

            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType, context);
            return vhHeader;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
        if(holder.holderId == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {
            holder.profile.setImageResource(profile);
            holder.name.setText(name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ROW;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}