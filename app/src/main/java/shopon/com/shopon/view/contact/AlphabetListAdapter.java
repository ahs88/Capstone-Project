package shopon.com.shopon.view.contact;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            Item item = (Item)o;
            if (item.fpath.equals(fpath))
            {
                Log.d(TAG,"equals true:"+text);
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
    
    //public void setItems(List<Item> items){this.items = items;}

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

    public void addSelectedItem(Item item){
        selectedItems.add(item);
    }

    public void removeSelectedItem(Item item){
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
        }else{
            //String viewTag = (String) view.getTag();

            //if (viewTag != null ) {
                itemHolder = (ItemHolder)view.getTag();
            //}
        }


        if (getItemViewType(position) == 0) { // Item
            Item item = (Item) getItem(position);
            //TextView textView = (TextView) view.findViewById(R.id.ccname);
            itemHolder.textView.setText(item.text);
            //TextView contact_number = (TextView) view.findViewById(R.id.ccnumber);
            itemHolder.contact_number.setText(item.fpath);
            if(selectedItems.contains(item)){
                itemHolder.select_deselect.setImageResource(R.drawable.ok_filled);
            }
            else {
                itemHolder.select_deselect.setImageResource(R.drawable.b_circlethin_2x);
            }
            /*ImageView imgView = (ImageView) view.findViewById(R.id.ccflag);

            try {
            	String fpath = "staticdata/cc_flags/" + item.fpath;
                imgView.setImageBitmap(BitmapFactory.decodeStream(asm.open("staticdata/cc_flags/" + item.fpath)));
                imgView.setTag(fpath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/


        } else { // Section
            Section section = (Section) getItem(position);
            //TextView textView = (TextView) view.findViewById(R.id.ccname);
            itemHolder.textView.setText(section.text);
            //TextView contact_number = (TextView) view.findViewById(R.id.ccnumber);
            itemHolder.contact_number.setVisibility(View.GONE);
            //ImageView select_deselect = (ImageView) view.findViewById(R.id.select_deselect);
            itemHolder.select_deselect.setVisibility(View.GONE);
        }
        view.setTag(itemHolder);
        return view;
    }

    public class ItemHolder {
        TextView textView;
        TextView contact_number;
        ImageView select_deselect;

        public ItemHolder(View view) {
            textView = (TextView) view.findViewById(R.id.ccname);

            contact_number = (TextView) view.findViewById(R.id.ccnumber);

            select_deselect = (ImageView) view.findViewById(R.id.select_deselect);
        }
    }
}
