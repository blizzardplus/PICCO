package com.google.sample.mobileassistantbackend.models;

/**
 * Created by hooman on 01/05/16.
 */


import com.google.appengine.api.datastore.GeoPt;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;



/**
 * Place entity.
 */
@Entity
public class HistoryItem {


    /**
     * * Unique identifier of this Entity in the database.
     */
    @Id
    private Long id;

    /**
     * The product of the item.
     */
    private Product product;

    /**
     * The purchase date of the item.
     */
    private Date purchaseDate;

    /**
     * The purchase price of the item.
     */
    private long purchasePrice;

    /**
     * The Place of purchase of the item .
     */
    private Place place;

    /**
     * The GPS coordinates of the product, as a GeoPt.
     */
    private GeoPt location;

    /**
     * Returns the unique identifier of this item.
     * @return the unique identifier associated to this item in the database.
     */
    public final Long getHisotryItemId() {
        return id;
    }

    /**
     * Sets the unique identifier of this item.
     * @param pHisotryItemId the identifier to set for this item.
     */
    public final void setHisotryItemId(final Long pHisotryItemId) {
        this.id = pHisotryItemId;
    }

    /**
     * Returns the name of the item.
     * @return the name of the item.
     */
    public final Product getProduct() {
        return product;
    }

    /**
     * Sets the product of the item.
     * @param pProduct the product to set for this item.
     */
    public final void setProduct(final Product pProduct) {
        this.product = pProduct;
    }

    /**
     * Returns the purchase date of the item.
     * @return the purchase date of the item.
     */
    public final Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the purchase date of the item.
     * @param pPurchaseDate the purchase date to set for this item.
     */
    public final void setPurchaseDate(final Date pPurchaseDate) {
        this.purchaseDate = pPurchaseDate;
    }

    /**
     * Returns the purchase price of the item.
     * @return the purchase price of the item.
     */
    public final long getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the purchase price of the item.
     * @param pPurchasePrice the purchase price to set for this item.
     */
    public final void setPurchasePrice(final long pPurchasePrice) {
        this.purchasePrice = pPurchasePrice;
    }

    /**
     * Returns the Place of the item.
     * @return the Place price of the item.
     */
    public final Place getPlace() {
        return place;
    }

    /**
     * Sets the Place of the item.
     * @param pPlace the Place to set for this item.
     */
    public final void setPlace(final Place pPlace) {
        this.place = pPlace;
    }

    /**
     * Returns the location of the place.
     * @return the GPS location of this place, as a GeoPt.
     */
    public final GeoPt getLocation() {
        return location;
    }

    /**
     * Sets the location of the place.
     * @param pLocation the GPS location to set for this place, as a GeoPt.
     */
    public final void setLocation(final GeoPt pLocation) {
        this.location = pLocation;
    }
}
