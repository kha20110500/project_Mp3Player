package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.SongViewHolder> {
    private ArrayList<Song> mListSong;
    private ArrayList<String> mListSongId;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void setFilteredListSong(ArrayList<Song> filteredListSong, ArrayList<String> filteredListSongId) {
        this.mListSong = filteredListSong;
        this.mListSongId = filteredListSongId;
        notifyDataSetChanged();
    }

    public AdminSongAdapter(Context context, ArrayList<Song> mListSong, ArrayList<String> mListSongId) {
        this.mListSong = mListSong;
        this.context = context;
        this.mListSongId = mListSongId;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_admin, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = mListSong.get(position);
        if (song == null) {
            return;
        }

        holder.songName.setText(song.getName());
        StringBuilder relatedSingers = new StringBuilder();
        relatedSingers.append("");
        final boolean[] firstSinger = {true};

        List<String> getListSingerRelate = song.getRelatedSingers();
        for (String SingerID : getListSingerRelate) {
            readSongData(new FireStoreCallbackSinger() {
                @Override
                public void onCallback(Singer singer) {
                    if(firstSinger[0]) {
                        relatedSingers.append(singer.getName());
                        firstSinger[0] = false;
                    } else {
                        relatedSingers.append(", "+singer.getName());
                    }
                    holder.songSinger.setText(relatedSingers);
                }
            }, SingerID);
        }

        Glide.with(holder.songImg.getContext())
                .load(song.getImage())
                .placeholder(R.drawable.alec_album)
                .error(R.drawable.alec_album)
                .into(holder.songImg);

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, CRUDSongActivity.class);
//                context.startActivity(intent);
            }
        });
    }

    private void readSongData(FireStoreCallbackSinger fireStoreCallback, String SingerID) {
        db.collection("singers").document(SingerID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("FireStore Error", "Listen failed.", error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            fireStoreCallback.onCallback(value.toObject(Singer.class));
                        } else {
                            Log.d("Null document: ", "Current data: null");
                        }
                    }
                });
    }

    private interface FireStoreCallbackSinger {
        void onCallback(Singer singer);
    }

    @Override
    public int getItemCount() {
        if (mListSong != null) {
            return mListSong.size();
        }
        return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
        private ImageView songImg;
        private TextView songName;
        private TextView songSinger;
        private FrameLayout itemMusic;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImg = itemView.findViewById(R.id.song_img);
            songName = itemView.findViewById(R.id.song_name);
            songSinger = itemView.findViewById(R.id.song_Singer);
            //btnMore.setOnClickListener(this);
            itemMusic = itemView.findViewById(R.id.itemMusic);
        }
    }


}
