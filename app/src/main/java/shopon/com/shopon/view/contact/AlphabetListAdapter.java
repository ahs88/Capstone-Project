package shopon.com.shopon.view.contact;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import shopon.com.shopon.R;


public class AlphabetListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private static final String TAG = AlphabetListAdapter.class.getName();
    private List<Item> selectedItems = new ArrayList<>();

    public Collection getSelectedContacts() {

        return selectedItems;
    }

    public static abstract class Row {
    }

    public static final class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {

        public final String text;
        public final String fpath;

        public Item(String text, String fpath) {
            this.text = text;
            this.fpath = fpath;
        }

        @Override
        public String toString() {
            return fpath;
        }

        @Override
        public boolean equals(Object o) {
            Item item = (Item) o;
            if (item.fpath.equals(fpath)) {
                Log.d(TAG, "equals true:" + text);
                return true;
            }
            return super.equals(o);
        }
    }

    private List<Row> rows;

    public AlphabetListAdapter(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void clearAll() {
        mInflater = null;
        mContext = null;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }

    public void addSelectedItem(Item item) {
        selectedItems.add(item);
    }

    public void removeSelectedItem(Item item) {
        selectedItems.remove(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        AssetManager asm = mContext.getAssets();
        ItemHolder itemHolder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.cc_row_item, parent, false);
            itemHolder = new ItemHolder(view);
        } else {
            itemHolder = (ItemHolder) view.getTag();

        }

        final ItemHolder itemHolder1 = itemHolder;

        if (getItemViewType(position) == 0) { // Item
            final Item item = (Item) getItem(position);

            itemHolder1.textView.setText(item.text);

            itemHolder1.contact_number.setText(item.fpath);
            if (selectedItems.contains(item)) {
                itemHolder1.select_deselect.setImageResource(R.drawable.ok_filled);
                itemHolder1.select_deselect.setContentDescription(mContext.getString(R.string.customer_selected, item.text));
            } else {
                itemHolder1.select_deselect.setImageResource(R.drawable.b_circlethin_2x);
                itemHolder1.select_deselect.setContentDescription(mContext.getString(R.string.customer_deselected, item.text));
            }

            itemHolder1.itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick");

                    if (!getSelectedContacts().contains(item)) {
                        itemHolder1.select_deselect.setImageResource(R.drawable.ok_filled);
                        itemHolder1.select_deselect.setContentDescription(mContext.getString(R.string.customer_selected, item.text));
                        addSelectedItem(item);
                    } else {
                        itemHolder1.select_deselect.setImageResource(R.drawable.b_circlethin_2x);
                        itemHolder1.select_deselect.setContentDescription(mContext.getString(R.string.customer_deselected, item.text));
                        removeSelectedItem(item);
                    }
                }
            });


        } else { // Section
            Section section = (Section) getItem(position);

            itemHolder1.textView.setText(section.text);

            itemHolder1.contact_number.setVisibility(View.GONE);

            itemHolder1.select_deselect.setVisibility(View.GONE);
        }
        view.setTag(itemHolder1);
        return view;
    }

    public class ItemHolder {
        TextView textView;
        TextView contact_number;
        ImageView select_deselect;
        View itemContainer;

        public ItemHolder(View view) {

            itemContainer = view;
            textView = (TextView) view.findViewById(R.id.ccname);

            contact_number = (TextView) view.findViewById(R.id.ccnumber);

            select_deselect = (ImageView) view.findViewById(R.id.select_deselect);
        }
    }
}
