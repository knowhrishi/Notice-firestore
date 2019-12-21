package com.example.notice_firestore.Notices;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notice_firestore.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyNoticeAdapter extends RecyclerView.Adapter<MyNoticeAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads;

    public MyNoticeAdapter(Context context, List<Upload> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notice, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);

        holder.textViewContent.setText(Html.fromHtml(upload.getContent()));
        holder.textViewAuthorname.setText(upload.getAuthor());
        holder.timestamp.setText(upload.getTimestamp());

//        Glide.with(context).load(upload.getUrl()).into(holder.imageView);

//
        if(upload.getUrl().equals("empty")){
            holder.imageView.setVisibility(View.GONE);
        }else {
            Picasso.get()
                    .load(upload.getUrl())
//                .placeholder(R.drawable.placeholder)   // optional
//                .error(R.drawable.error)      // optional
//                .resize(400,400)                        // optional
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewContent;
        public ImageView imageView;
        public TextView textViewAuthorname, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewAuthorname = (TextView)itemView.findViewById(R.id.name);
            timestamp = (TextView)itemView.findViewById(R.id.noticetimestamp);
        }
    }
}