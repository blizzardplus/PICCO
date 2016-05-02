/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.mobileassistantbackend.models;

import com.google.appengine.api.datastore.GeoPt;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


/**
 * Place entity.
 */
@Entity
public class Product {

        /**
         * * Unique identifier of this Entity in the database.
         */
        @Id
        private Long id;

        /**
         * The name of the product.
         */
        private String name;

        /**
         * The description of the product.
         */
        private String description;

        /**
         * The category of the product.
         */
        private String category;

        /**
         * The weight of the product.
         */
        private long weight;

        /**
         * The price of the product.
         */
        private long price;


        /**
         * The GPS coordinates of the product, as a GeoPt.
         */
        private GeoPt location;

        /**
         * Returns the unique identifier of this product.
         * @return the unique identifier associated to this place in the database.
         */
        public final Long getProductId() {
                return id;
        }

        /**
         * Sets the unique identifier of this product.
         * @param pProductId the identifier to set for this place.
         */
        public final void setProductId(final Long pProductId) {
                this.id = pProductId;
        }

        /**
         * Returns the name of the product.
         * @return the name of the product.
         */
        public final String getName() {
                return name;
        }

        /**
         * Sets the name of the product.
         * @param pName the name to set for this product.
         */
        public final void setName(final String pName) {
                this.name = pName;
        }

        /**
         * Returns the description of the product.
         * @return the description of the product.
         */
        public final String getDescription() {
                return description;
        }

        /**
         * Sets the description of the product.
         * @param pDescription the name to set for this description.
         */
        public final void setDescription(final String pDescription) {
                this.description = pDescription;
        }

        /**
         * Returns the category of the product.
         * @return the category of the product.
         */
        public final String getCategory() {
                return category;
        }

        /**
         * Sets the category of the product.
         * @param pCategory the name to set for this category.
         */
        public final void setCategory(final String pCategory) {
                this.category = pCategory;
        }


        /**
         * Returns the weight of the product.
         * @return the weight of the product.
         */
        public final long getWeight() {
                return weight;
        }

        /**
         * Sets the weight of the product.
         * @param pWeight the value to set for this weight.
         */
        public final void setWeight(final long pWeight) {
                this.weight = weight;
        }

        /**
         * Returns the price of the product.
         * @return the price of the product.
         */
        public final long getPrice() {
                return price;
        }

        /**
         * Sets the price of the product.
         * @param pPrice the value to set for this price.
         */
        public final void setPrice(final long pPrice) {
                this.price = price;
        }

        /**
         * Returns the location of the product.
         * @return the GPS location of this product, as a GeoPt.
         */
        public final GeoPt getLocation() {
                return location;
        }

        /**
         * Sets the location of the product.
         * @param pLocation the GPS location to set for this product, as a GeoPt.
         */
        public final void setLocation(final GeoPt pLocation) {
                this.location = pLocation;
        }
}
