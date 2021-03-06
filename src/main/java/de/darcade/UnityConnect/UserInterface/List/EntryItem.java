package de.darcade.UnityConnect.UserInterface.List;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.darcade.UnityConnect_tp.R;

public class EntryItem implements ListAdapter.Item {

	private final String title;

	public EntryItem(String title) {
		this.title = title;
	}

    @Override
    public View inflateView(LayoutInflater layoutInflater) {
        View v = layoutInflater.inflate(R.layout.list_item_entry, null);

        TextView titleView = (TextView)v.findViewById(R.id.list_item_entry_title);
        if (titleView != null) titleView.setText(title);

        return v;
    }

}
