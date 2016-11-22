package shopon.com.shopon.view.offer.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shopon.com.shopon.R;
import shopon.com.shopon.datamodel.offer.Offer;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class OfferContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Offer> ITEMS = new ArrayList<Offer>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Offer> ITEM_MAP = new HashMap<String, Offer>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
       /* for (int i = 1; i <= COUNT; i++) {
            addItem(createOfferItem(i));
        }*/
    }

    private static void addItem(Offer item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.getOfferId(), item);
    }

    private static Offer createOfferItem(int position) {
        return new Offer();
    }

    private static String offerText(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Offer Label: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append(ITEMS.get(position).getOfferText());
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */



}
