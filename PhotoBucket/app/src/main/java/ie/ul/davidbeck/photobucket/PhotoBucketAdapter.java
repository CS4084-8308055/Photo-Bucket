package ie.ul.davidbeck.photobucket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PhotoBucketAdapter extends RecyclerView.Adapter<PhotoBucketAdapter.PhotoBucketViewHolder> {

    private List<DocumentSnapshot> mPhotoSnapshots = new ArrayList<>();

    public PhotoBucketAdapter(){
        CollectionReference photosCollectionRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH);
        photosCollectionRef.orderBy(Constants.KEY_CREATED, Query.Direction.DESCENDING).limit(50)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(Constants.TAG, "Listening failed!");
                    return;
                }
                mPhotoSnapshots = documentSnapshots.getDocuments();
                notifyDataSetChanged();
            }
        });
    }
    @NonNull
    @Override
    public PhotoBucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photobucket_itemview, parent, false);
        return new PhotoBucketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoBucketViewHolder photoBucketViewHolder, int i) {
        DocumentSnapshot ds = mPhotoSnapshots.get(i);
        String caption = (String)ds.get(Constants.KEY_CAPTION);
        photoBucketViewHolder.mCaptionTextView.setText(caption);
    }

    @Override
    public int getItemCount() {
        return mPhotoSnapshots.size();
    }

    class PhotoBucketViewHolder extends RecyclerView.ViewHolder {

        private TextView mCaptionTextView;
        public PhotoBucketViewHolder(@NonNull final View itemView) {
            super(itemView);
            mCaptionTextView = itemView.findViewById(R.id.itemview_caption);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context c = itemView.getContext();
                    DocumentSnapshot ds = mPhotoSnapshots.get(getAdapterPosition());
                    Intent intent = new Intent(c, PhotoBucketDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_DOC_ID, ds.getId());
                    c.startActivity(intent);
                }
            });
        }
    }
}
