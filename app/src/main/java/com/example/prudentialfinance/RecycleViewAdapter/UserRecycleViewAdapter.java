package com.example.prudentialfinance.RecycleViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prudentialfinance.API.HTTPService;
import com.example.prudentialfinance.Helpers.Helper;
import com.example.prudentialfinance.Model.User;
import com.example.prudentialfinance.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserRecycleViewAdapter extends RecyclerView.Adapter<UserRecycleViewAdapter.ViewHolder> {
    private ArrayList<User> objects;
    private Context context;


    public UserRecycleViewAdapter(Context context, ArrayList<User> objects) {
            this.objects = objects;
            this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.user_management_element, parent, false);
            return new ViewHolder(view, parent);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull UserRecycleViewAdapter.ViewHolder holder, int position) {
            User entry = objects.get(position);

            holder.user_name.setText(entry.getFirstname() + " " + entry.getLastname());
            holder.user_email.setText(entry.getEmail());
            if(entry.getIs_active()){
                holder.user_active.setImageResource(R.drawable.ic_baseline_admin_panel_settings_24);
            }else{
                holder.user_active.setImageResource(R.drawable.ic_baseline_close_24);
            }

            Picasso
                .get()
                .load(HTTPService.UPLOADS_URL + "/"+ entry.getAvatar())
                .fit()
                .placeholder(R.drawable.someone)
                .error(R.drawable.someone)
                .transform(Helper.getRoundedTransformationBuilder1())
                .into(holder.user_avatar);


            Context parentContext = holder.parent.getContext();
            holder.user_layout.setOnClickListener(view1 -> {

            });
    }

    @Override
    public int getItemCount() {
            return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView user_avatar, user_active;
        private TextView user_name, user_email;
        private LinearLayout user_layout;
        private ViewGroup parent;

        public ViewHolder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);
            setControl(itemView);
            this.parent = parent;
        }

        private void setControl(View itemView)
        {
            user_avatar = itemView.findViewById(R.id.user_avatar);
            user_active = itemView.findViewById(R.id.user_active);
            user_name = itemView.findViewById(R.id.user_name);
            user_email = itemView.findViewById(R.id.user_email);
            user_layout = itemView.findViewById(R.id.user_layout);
        }
    }
}
