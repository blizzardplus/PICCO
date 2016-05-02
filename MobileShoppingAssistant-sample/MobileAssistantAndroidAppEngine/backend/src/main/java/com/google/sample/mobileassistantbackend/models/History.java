package com.google.sample.mobileassistantbackend.models;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;



import java.util.List;

/**
 * Created by hooman on 01/05/16.
 */

/**
 * History entity.
 */
@Entity
public class History {

    /**
     * * Unique identifier of this Entity in the database.
     */
    @Id
    private Long id;

    /**
     * The items of the HistoryTransaction.
     */
    private List<HistoryItem> items;

    /**
     * Returns the items of the HistoryTransaction.
     * @return the items of the HistoryTransaction.
     */
    public final List<HistoryItem> getItems() {
        return items;
    }

    /**
     * Sets the items of the HistoryTransaction.
     * @param pItems the items to set for this HistoryTransaction.
     */
    public final void setItems(final  List<HistoryItem> pItems) {
        this.items = pItems;
    }

}
