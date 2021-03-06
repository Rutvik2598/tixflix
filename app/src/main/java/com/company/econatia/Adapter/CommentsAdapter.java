package com.company.econatia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.company.econatia.MainActivity;
import com.company.econatia.Model.Comments;
import com.company.econatia.Model.User;
import com.company.econatia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{


    private Context mContext;
    private List<Comments> mComment;
    private String postid;

    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comments> mComment , String postid) {
        this.mContext = mContext;
        this.postid = postid;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item , viewGroup , false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comments comments = mComment.get(i);

        viewHolder.comment.setText(comments.getComment());
        getUserInfo(viewHolder.image_profile , viewHolder.username , comments.getPublisher());

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MainActivity.class);
                ContextCompat.startForegroundService(mContext , intent);
                intent.putExtra("publisherid" , comments.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MainActivity.class);
                ContextCompat.startForegroundService(mContext , intent);
                intent.putExtra("publisherid" , comments.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comments.getPublisher().equals(firebaseUser.getUid())){

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete your comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid).child(comments.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(mContext , "Deleted!" , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username , comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment23);

        }
    }

    private void getUserInfo(final ImageView imageView , final TextView username , String publisherId){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
