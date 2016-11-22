package shopon.com.shopon.view.customers.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shopon.com.shopon.datamodel.customer.Customers;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CustomerContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Customers> ITEMS = new ArrayList<Customers>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, Customers> ITEM_MAP = new HashMap<Integer, Customers>();

    private static final int COUNT = 0;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Customers item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static Customers createDummyItem(int position) {
        return new Customers();
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


}
