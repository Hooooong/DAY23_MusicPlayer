package com.hooooong.musicplayer.view.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooooong.musicplayer.R;
import com.hooooong.musicplayer.view.main.MusicFragment.OnListFragmentInteractionListener;
import com.hooooong.musicplayer.view.main.adapter.model.Music;

import java.util.List;

public class MyMusicRecyclerViewAdapter extends RecyclerView.Adapter<MyMusicRecyclerViewAdapter.ViewHolder> {

    private final List<Music.Item> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMusicRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = listener.getList();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).title);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.getList(holder.mItem);

                    /*Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(textId, "textId");
                    pairs[1] = new Pair<View, String>(textName, "textName");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(), pairs);
                    //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(), textName, "textName");

                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("textId", textId.getText());
                    intent.putExtra("textName", textName.getText());
                    options.toBundle();*/

                    mListener.openPlayer(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Music.Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
