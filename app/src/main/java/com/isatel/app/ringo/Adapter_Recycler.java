package com.isatel.app.ringo;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.ArrayList;


//*************************************************************** THIS CLASS IS THE ADAPTER OF RECYCLERVIEWS
public class Adapter_Recycler extends RecyclerView.Adapter<Adapter_Recycler.ViewHolder> {
    public static boolean checked;
    public Boolean nextItemIsClicked = false;
    public RadioButton selectedRadio;
    private ImageView lastClicked;
    private OnItemListener onItemListener;
    private Context context;
    private ArrayList<Struct> structs;
    private boolean isGrid;
    private int Tab;
    private Struct selectedGroupPosition;
    private Boolean x;
    private Boolean m;
    private RadioButton lastCheckedRadio;
    private ImageView playing = null;

    public Adapter_Recycler(Context context, ArrayList<Struct> structs, OnItemListener onItemListener, int Tab, boolean isGrid) {
        this.onItemListener = onItemListener;
        this.context = context;
        this.structs = structs;
        this.isGrid = isGrid;
        this.Tab = Tab;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //CHOOSE WITCH XML (CARD LAYOUT) TO SHOW (INFLATE) FOR EATCH RECYCLERVIEW
        View view = null;
        if (Tab == 1) {
            view = inflater.inflate(R.layout.list_item_music, parent, false);
        }
        if (Tab == 2) {
            view = inflater.inflate(R.layout.list_item_cat, parent, false);
        }
        if (Tab == 3) {
            view = inflater.inflate(R.layout.list_item_bank, parent, false);
        }
        if (Tab == 4) {
            view = inflater.inflate(R.layout.list_item_archive, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //DEFINING VIEWS FOR EATCH RECYCLER VIEW
        Typeface typeFace_Regular = Typeface.createFromAsset(G.context.getAssets(), "iran_sans_regular.ttf");
        Typeface typeFace_Medium = Typeface.createFromAsset(G.context.getAssets(), "iran_sans_medium.ttf");


        if (Tab == 1) {
            if (G.isLimited) {
                holder.lock.setVisibility(View.VISIBLE);
            } else {
                holder.lock.setVisibility(View.GONE);

            }

            if (structs.get(position).state) {
                holder.Download.setVisibility(View.GONE);
            } else {
                holder.Download.setVisibility(View.VISIBLE);

            }
            holder.Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (G.isLimited) {
                        onItemListener.onItemLock(position);

                    } else {
                        onItemListener.onItemSelect(position);
                    }
                }
            });
            holder.Play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (G.isLimited) {
                        onItemListener.onItemLock(position);

                    } else {
                        try {
                            if (lastClicked != holder.Play) {
                                lastClicked.setImageResource(R.mipmap.play);
                                if (lastClicked.getTag().toString().equals("enable")) {
                                    onItemListener.onItemClick(position);
                                }
                                nextItemIsClicked = true;
                            }
                        } catch (Exception e) {

                        }
                        lastClicked = holder.Play;

                        if (holder.Play.getTag().toString().equals("enable")) {
                            holder.Play.setImageResource(R.mipmap.play);
                            holder.Play.setTag("disable");
                        } else {
                            holder.Play.setTag("enable");
                            holder.Play.setImageResource(R.mipmap.pause);
                        }
                        if (nextItemIsClicked) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onItemListener.onItemClick(position);
                                }
                            }, 100);
                            nextItemIsClicked = false;
                        } else if (!nextItemIsClicked) {
                            onItemListener.onItemClick(position);
                        }
                    }
                }
            });
            holder.Title.setText(structs.get(position).strTitle);
            holder.Artist.setText(structs.get(position).strArtist);
            holder.Title.setTypeface(typeFace_Medium);
            holder.Artist.setTypeface(typeFace_Medium);
            if (structs.get(position).isPlaying) {
                holder.Play.setImageResource(R.drawable.pause);
            } else {
                holder.Play.setImageResource(R.drawable.play2);

            }
        }
        if (Tab == 2) {
            holder.Title.setText(structs.get(position).strTitle);
//            holder.radio.setChecked(structs.get(position).state);
            holder.radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        selectedRadio.setChecked(false);
                    } catch (Exception e) {
                        Log.d("ssss", String.valueOf(e));
                    }
                    selectedRadio = holder.radio;
                    holder.radio.setChecked(true);
                    onItemListener.onItemClick(position);

                }
            });
        }
        if (Tab == 3) {

            if (G.isLimited) {
                holder.lock.setVisibility(View.VISIBLE);
            } else {
                holder.lock.setVisibility(View.GONE);

            }

            if (structs.get(position).state) {
                holder.Download.setVisibility(View.GONE);
            } else {
                holder.Download.setVisibility(View.VISIBLE);

            }
            holder.Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (G.isLimited) {
                        onItemListener.onItemLock(position);

                    } else {
                        onItemListener.onItemSelect(position);
                    }
                }
            });
            holder.Play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (G.isLimited) {
                        onItemListener.onItemLock(position);

                    } else {
                        try {
                            if (lastClicked != holder.Play) {
                                lastClicked.setImageResource(R.drawable.play2);
                                if (lastClicked.getTag().toString().equals("enable")) {
                                    onItemListener.onItemClick(position);
                                }
                                nextItemIsClicked = true;
                            }
                        } catch (Exception e) {

                        }
                        lastClicked = holder.Play;

                        if (holder.Play.getTag().toString().equals("enable")) {
                            holder.Play.setImageResource(R.drawable.play2);
                            holder.Play.setTag("disable");
                            playing = null;
                        } else {
                            holder.Play.setTag("enable");
                            holder.Play.setImageResource(R.mipmap.pause);
                            playing = holder.Play;
                        }
                        if (nextItemIsClicked) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onItemListener.onItemClick(position);
                                }
                            }, 100);
                            nextItemIsClicked = false;
                        } else if (!nextItemIsClicked) {
                            onItemListener.onItemClick(position);
                        }
                    }
                }
            });
            holder.Title.setText(structs.get(position).strTitle);
            holder.Artist.setText(structs.get(position).strArtist);
            holder.Title.setTypeface(typeFace_Regular);
            holder.Artist.setTypeface(typeFace_Medium);
            if (structs.get(position).isPlaying) {
//                Log.d("sasaas", "play clicked");
                holder.Play.setImageResource(R.mipmap.pause);
                playing = holder.Play;
                structs.get(position).isPlaying = false;
            } else {
//                Log.d("sasaas", "pause clicked");
                holder.Play.setImageResource(R.drawable.play2);
//                playing = null;
//                structs.get(position).isPlaying=true;
            }

//            if (playing != null) {
////                Log.d("running", "not null");
//                if (playing == holder.Play) {
//                    holder.Play.setImageResource(R.mipmap.pause);
//                } else {
//                    holder.Play.setImageResource(R.drawable.play2);
//                }
//            } else {
//                holder.Play.setImageResource(R.drawable.play2);
//            }
        }
        if (Tab == 4) {

//            holder.setIsRecyclable(false);
//            if (G.isLimited) {
//                holder.lock.setVisibility(View.VISIBLE);
//            } else {
//                holder.lock.setVisibility(View.GONE);
//
//            }
            holder.Play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (lastClicked != holder.Play) {
                            if (lastClicked.getTag().toString().equals("enable")) {
                                onItemListener.onItemClick(position);
                                Log.d("logging", "first done");
                            }
                            nextItemIsClicked = true;
                        }
                    } catch (Exception e) {

                    }
                    lastClicked = holder.Play;

                    if (holder.Play.getTag().toString().equals("enable")) {
                        holder.Play.setTag("disable");
                    } else {
                        holder.Play.setTag("enable");
                    }
                    if (nextItemIsClicked) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onItemListener.onItemClick(position);
                                Log.d("logging", "second done");
                            }
                        }, 100);
                        nextItemIsClicked = false;
                    } else if (!nextItemIsClicked) {
                        onItemListener.onItemClick(position);
                        Log.d("logging", "third done");
                    }
                }
            });
            holder.Title.setText(structs.get(position).strTitle);
            holder.Artist.setText(structs.get(position).strArtist);
            holder.Title.setTypeface(typeFace_Medium);
            holder.Artist.setTypeface(typeFace_Medium);
            if (structs.get(position).isPlaying) {
                holder.Play.setImageResource(R.drawable.pause);
            } else {
                holder.Play.setImageResource(R.drawable.play2);

            }
        }
    }


    @Override
    public int getItemCount() {
        return structs.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        public TextView Title;
        public TextView Artist;
        public ImageView Play;
        public ImageView lock;
        public ImageView Download;
        public LinearLayout Item;
        public RadioButton radio;

        public ViewHolder(View itemView) {
            super(itemView);
            Title = (TextView) itemView.findViewById(R.id.row_title);
            Artist = (TextView) itemView.findViewById(R.id.row_artist);
            Play = (ImageView) itemView.findViewById(R.id.row_options_button);
            lock = (ImageView) itemView.findViewById(R.id.lock);
            Download = (ImageView) itemView.findViewById(R.id.row_options_download);
            Item = (LinearLayout) itemView.findViewById(R.id.music_item);
            radio = (RadioButton) itemView.findViewById(R.id.radio);


        }
    }

}
