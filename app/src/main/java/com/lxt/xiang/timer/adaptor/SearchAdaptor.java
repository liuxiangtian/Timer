package com.lxt.xiang.timer.adaptor;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.VH> {

    private static final int TITLE_ROOT = 1;
    private static final int TITLE_ALBUM = 2;
    private static final int TITLE_ARTIST = 3;
    private static final int TITLE_TRACK = 4;
    private static final int CONTENT_ALBUM = 5;
    private static final int CONTENT_ARTIST = 6;
    private static final int CONTENT_TRACK = 7;

    private List<Track> tracks;
    private List<Album> albums;
    private List<Artist> artists;
    private OnItemClickListener mOnItemClickListener;

    public SearchAdaptor() {
        if (tracks == null) this.tracks = new ArrayList<>();
        if (albums == null) this.albums = new ArrayList<>();
        if (artists == null) this.artists = new ArrayList<>();
    }

    public SearchAdaptor(List<Track> tracks, List<Album> albums, List<Artist> artists) {
        if (tracks == null) this.tracks = new ArrayList<>();
        if (albums == null) this.albums = new ArrayList<>();
        if (artists == null) this.artists = new ArrayList<>();
        this.tracks = tracks;
        this.albums = albums;
        this.artists = artists;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TITLE_ROOT || viewType == TITLE_ALBUM || viewType == TITLE_ARTIST || viewType == TITLE_TRACK) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TitleHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_standant_list, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int type = getItemViewType(position);
        if (type == TITLE_ALBUM) {
            ((TitleHolder) holder).titleText.setText("ALBUM: "+albums.size());
        } else if (type == TITLE_ARTIST) {
            ((TitleHolder) holder).titleText.setText("ARTIST: "+artists.size());
        } else if (type == TITLE_TRACK) {
            ((TitleHolder) holder).titleText.setText("TRACK: "+tracks.size());
        } else if (type == TITLE_ROOT) {
            StringBuilder builder = new StringBuilder();
            builder.append("专辑数:").append(albums.size())
                    .append("  歌手数:").append(artists.size())
                    .append("  歌曲数:").append(tracks.size());
            ((TitleHolder) holder).titleText.setText(builder.toString());
        } else if (type == CONTENT_ALBUM) {
            ItemHolder itemHolder = (ItemHolder) holder;
            Album album = albums.get(position - 2);
            itemHolder.titleText.setText(album.getAlbum());
            itemHolder.contentText.setText(album.getAlbumArt() + " - " + album.getTrackNum() + " tracks");
            BitmapUtil.loadBitmap(itemHolder.albumArt, album.getId());
        } else if (type == CONTENT_ARTIST) {
            ItemHolder itemHolder = (ItemHolder) holder;
            Artist artist = artists.get(position - 3);
            BitmapUtil.loadBitmapByArtistId(itemHolder.albumArt, artist.getId());
            itemHolder.titleText.setText(artist.getArtist());
            itemHolder.contentText.setText(artist.getAlbumNum() + " albums - " + artist.getTrackNum() + " tracks");
        } else if (type == CONTENT_TRACK) {
            ItemHolder itemHolder = (ItemHolder) holder;
            Track track = tracks.get(position - 4);
            BitmapUtil.loadBitmap(itemHolder.albumArt, track.getAlbumId());
            itemHolder.titleText.setText(track.getTitle());
            itemHolder.contentText.setText(track.getAlbum() + " - " + track.getArtist());
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size() + albums.size() + artists.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TITLE_ROOT;
        if (position == 1) return TITLE_ALBUM;
        if (position == albums.size() + 2) return TITLE_ARTIST;
        if (position == albums.size() + artists.size() + 3) return TITLE_TRACK;
        if (position > 1 && position < albums.size() + 2) return CONTENT_ALBUM;
        if (position > albums.size() + 2 && position < albums.size() + artists.size() + 3) return CONTENT_ARTIST;
        return CONTENT_TRACK;
    }

    public void replaceData(List<Track> tracks, List<Album> albums, List<Artist> artists) {
        if (tracks != null) {
            this.tracks = tracks;
        }
        if (albums != null) {
            this.albums = albums;
        }
        if (artists != null) {
            this.artists = artists;
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Track track);
        void onItemClick(Album album);
        void onItemClick(Artist artist);
    }

    public class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }

    public class TitleHolder extends VH {

        public TextView titleText;

        public TitleHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(android.R.id.text1);

        }
    }

    public class ItemHolder extends VH implements View.OnClickListener{

        @Bind(R.id.album_art)
        ImageView albumArt;
        @Bind(R.id.icon)
        MaterialIconView icon;
        @Bind(R.id.track_title)
        TextView titleText;
        @Bind(R.id.track_artist)
        TextView contentText;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            icon.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int type = SearchAdaptor.this.getItemViewType(position);
            if (mOnItemClickListener == null) {
                return;
            }
            if(type==CONTENT_ALBUM){
                Album album = albums.get(position - 2);
                mOnItemClickListener.onItemClick(album);
            } else if(type==CONTENT_ARTIST){
                Artist artist = artists.get(position - 3);
                mOnItemClickListener.onItemClick(artist);
            } else if(type==CONTENT_TRACK){
                Track track = tracks.get(position - 4);
                mOnItemClickListener.onItemClick(track);
            }
        }
    }

}

