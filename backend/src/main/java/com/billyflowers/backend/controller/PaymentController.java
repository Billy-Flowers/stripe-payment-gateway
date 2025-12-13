package com.billyflowers.backend.controller;

import com.billyflowers.backend.DAO.ProductDAO;
import com.billyflowers.backend.DTO.RequestDTO;
import com.billyflowers.backend.Util.CustomerUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${client.base.url}")
    private String clientBaseURL;

    @PostMapping("/checkout/hosted")
    String hostedCheckout(@RequestBody RequestDTO requestDTO) throws StripeException {


        if (stripeApiKey == null || stripeApiKey.isEmpty()) {
            throw new RuntimeException("STRIPE_API_KEY environment variable is not set");
        }

        if (clientBaseURL == null || clientBaseURL.isEmpty()) {
            throw new RuntimeException("CLIENT_BASE_URL environment variable is not set");
        }
        
        Stripe.apiKey = stripeApiKey;

        // Start by finding an existing customer record from Stripe or creating a new one if needed
        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());

        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(clientBaseURL + "/failure");

        for (Product product : requestDTO.getItems()) {
            Product productData = ProductDAO.getProduct(product.getId());
            if (productData != null && productData.getDefaultPriceObject() != null) {
                paramsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        PriceData.builder()
                                                .setProductData(
                                                        PriceData.ProductData.builder()
                                                                .putMetadata("app_id", product.getId())
                                                                .setName(product.getName())
                                                                .build()
                                                )
                                                .setCurrency(productData.getDefaultPriceObject().getCurrency())
                                                .setUnitAmountDecimal(productData.getDefaultPriceObject().getUnitAmountDecimal())
                                                .build())
                                .build());
            }
        }

        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }
}
