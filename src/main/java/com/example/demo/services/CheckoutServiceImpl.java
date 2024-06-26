package com.example.demo.services;

import com.example.demo.dao.*;
import com.example.demo.entities.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Set;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService{
    private CartRepository cartRepository;
    @Autowired
    public CheckoutServiceImpl(CartRepository cartRepository)
    {
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {

        Cart cart = purchase.getCart();

        String orderTrackingNumber = generateOrderTrackingNumber();
        cart.setOrderTrackingNumber(orderTrackingNumber);

        Set<CartItem> cartItems = purchase.getCartItems();
        if (cartItems.isEmpty()) {
            return new PurchaseResponse("Error: Cart Empty");
        }

        cartItems.forEach(item -> cart.add(item));

        cart.setStatus(Cart.StatusType.ordered);

        Customer customer = purchase.getCustomer();
        customer.add(cart);
        cartRepository.save(cart);

        return new PurchaseResponse(orderTrackingNumber);
    }


    private String generateOrderTrackingNumber() {

        return UUID.randomUUID().toString();

    }

}