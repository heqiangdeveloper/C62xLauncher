package com.chinatsp.widgetcards.home;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerCreator;
import com.chinatsp.widgetcards.R;

public class HomeDrawerCardViewHolder extends RecyclerView.ViewHolder {

    public HomeDrawerCardViewHolder(@NonNull View itemView) {
        super(itemView);
        DrawerCreator drawerCreator = new DrawerCreator(itemView.findViewById(R.id.rcvDrawerContent));
        drawerCreator.initDrawerRcv();
    }
}
