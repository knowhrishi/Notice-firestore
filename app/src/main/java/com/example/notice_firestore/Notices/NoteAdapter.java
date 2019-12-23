package com.example.notice_firestore.Notices;

import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notice_firestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {

        holder.textViewContent.setText(Html.fromHtml(model.getContent()));
        holder.textViewAuthorname.setText(model.getAuthor());
        holder.timestamp.setText(model.getTimestamp());


        if(model.getUrl().equals("empty")){
            holder.imageView.setVisibility(View.GONE);
        }else {
            Picasso.get()
                    .load(model.getUrl())
//                .placeholder(R.drawable.placeholder)   // optional
//                .error(R.drawable.error)      // optional
//                .resize(400,400)                        // optional
                    .into(holder.imageView);
        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notice, parent, false);

        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        public TextView textViewContent, textViewAuthorname, timestamp, priority;
        public ImageView imageView;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewAuthorname = (TextView)itemView.findViewById(R.id.name);
            timestamp = (TextView)itemView.findViewById(R.id.noticetimestamp);
            priority = (TextView)itemView.findViewById(R.id.priority);
        }
    }
}
