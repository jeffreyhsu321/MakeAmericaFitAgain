package com.notfound.makeamericafitagain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<Meal> mDataset;

    String image_name;

    TextView tv_food1;
    TextView tv_food2;
    TextView tv_food3;
    TextView tv_food4;
    TextView tv_food5;
    ImageView iv_food1;
    ImageView iv_food2;
    ImageView iv_food3;
    ImageView iv_food4;
    ImageView iv_food5;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference storageRef;
    DatabaseReference refRoot;
    DatabaseReference refUser;

    int counter = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public MyViewHolder(CardView cd) {
            super(cd);
            mCardView = cd;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(List<Meal> list_meals) {
        mDataset = list_meals;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refUser = refRoot.child(mUser.getUid());
        storageRef = getInstance().getReference();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        CardView cd = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_meal, parent, false);

        MyViewHolder vh = new MyViewHolder(cd);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        tv_food1 = holder.mCardView.findViewById(R.id.tv_food1);
        tv_food1.setText(mDataset.get(position).getFood(0).getName());
        tv_food2 = holder.mCardView.findViewById(R.id.tv_food2);
        tv_food2.setText(mDataset.get(position).getFood(1).getName());
        tv_food3 = holder.mCardView.findViewById(R.id.tv_food3);
        tv_food3.setText(mDataset.get(position).getFood(2).getName());
        tv_food4 = holder.mCardView.findViewById(R.id.tv_food4);
        tv_food4.setText(mDataset.get(position).getFood(3).getName());
        tv_food5 = holder.mCardView.findViewById(R.id.tv_food5);
        tv_food5.setText(mDataset.get(position).getFood(4).getName());

        String image_name = mDataset.get(position).getImage_name();
        switch(position){
            case 0:
                iv_food1 = holder.mCardView.findViewById(R.id.iv_food);
                break;
            case 1:
                iv_food2 = holder.mCardView.findViewById(R.id.iv_food);
                break;
            case 2:
                iv_food3 = holder.mCardView.findViewById(R.id.iv_food);
                break;
            case 3:
                iv_food4 = holder.mCardView.findViewById(R.id.iv_food);
                break;
            case 4:
                iv_food5 = holder.mCardView.findViewById(R.id.iv_food);
                break;
            default:
                break;
        }

        //retrieve image
        StorageReference imageRef = storageRef.child("images/" + image_name);

        try{
            final File imageFile = File.createTempFile("images", "jpg");

            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //SET IMAGEVIEW
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                    counter++;
                    switch(counter){
                        case 1:
                            iv_food1.setImageBitmap(imageBitmap);
                            break;
                        case 2:
                            iv_food2.setImageBitmap(imageBitmap);
                            break;
                        case 3:
                            iv_food3.setImageBitmap(imageBitmap);
                            break;
                        case 4:
                            iv_food4.setImageBitmap(imageBitmap);
                            break;
                        case 5:
                            iv_food5.setImageBitmap(imageBitmap);
                            break;
                        default:
                            break;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch(IOException ie) {}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
