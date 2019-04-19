package com.isatel.app.ringo;



//************************ CLICK LISTENER FOR RECYCLERVIEW ITEMS

public interface OnItemListener {
    public void onItemSelect(int position);

    public void onItemClick(int position);
    public void onItemLock(int position);
}
