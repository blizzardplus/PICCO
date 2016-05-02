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

package com.google.sample.mobileassistantbackend.apis;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;
import com.google.sample.mobileassistantbackend.Constants;
import com.google.sample.mobileassistantbackend.models.Product;
import com.google.sample.mobileassistantbackend.utils.EndpointUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.sample.mobileassistantbackend.OfyService.ofy;

/**
 * Exposes REST API over Product resources.
 */
@Api(name = "shoppingAssistant", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "products",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class ProductEndpoint {

    /**
     * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(ProductEndpoint.class.getName());

    /**
     * Maximum number of products to return.
     */
    private static final int MAXIMUM_NUMBER_PRODUCTS = 100;

    

    /**
     * Gets the entity having primary key id.
     * @param id the primary key of the java bean.
     * @param user the user that requested the entities.
     * @return The entity with primary key id.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(path="get_product", httpMethod = "GET")
    public final Product getProduct(@Named("id") final Long id, final User user) {
          //  throws ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        return findProduct(id);
    }

    @ApiMethod(path="get_product_by_barcode", httpMethod = "GET")
    public final List<Product> getProductByBarcode(@Named("barcode_format") final String barcode_format,
                                             @Named("barcode_content") final String barcode_content,
                                             final User user) {
            //throws ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        return findProductByBarcode(barcode_format, barcode_content);
    }

    // Gets all products in a store
    @ApiMethod(path="get_store_products", httpMethod = "GET")
    public final List<Product> getStoreProducts(@Named("storeid") final Long storeid, final User user) {
           // throws ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        return findStoreProducts(storeid);
    }

    // Search for a product in all nearby stores
    @ApiMethod(path="search_product", httpMethod = "GET")
    public final List<Product> searchProducts(@Named("product_name") final String product_name,
                                              @Named("store_list") final List<Long> store_list,
                                              final User user) {
        //    throws ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        return searchProducts(product_name, store_list);
    }

    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
     * @param product the entity to be inserted.
     * @param user the user that requested to insert the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "POST")
    public final Product insertProduct(final Product product, final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(product).now();

        return product;
    }

    /**
     * Updates an entity. It uses HTTP PUT method.
     * @param product the entity to be updated.
     * @param user the user that requested the update to the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final Product updateProduct(final Product product, final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(product).now();

        return product;
    }

    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user that requested the deletion of the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeProduct(@Named("id") final Long id, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        Product product = findProduct(id);
        if (product == null) {
            LOG.info(
                    "Product " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(product).now();
    }

    /**
     * Searches an entity by ID.
     * @param id the product ID to search
     * @return the Product associated to id
     */
    private Product findProduct(final Long id) {
        return ofy().load().type(Product.class).id(id).now();
    }

    private List<Product> findProductByBarcode(final String barcode_format, final String barcode_content) {
        LOG.info("barcode_format "+barcode_format+ " barcode_content "+barcode_content);
        return ofy().load().type(Product.class).filter("barcode_content ==", barcode_content).list();
    }


    private List<Product> findStoreProducts(final Long storeid) {
        return ofy().load().type(Product.class).filter("storeid ==", storeid).list();
    }

    private List<Product> searchProducts(final String product_name, final List<Long> store_list) {

        List<Product> product_list = new ArrayList<Product>();
        for (Long storeid_cur : store_list) {
            product_list.addAll(ofy().load().type(Product.class).filter("name ==", product_name).list());
        }
        return product_list;
    }

}
