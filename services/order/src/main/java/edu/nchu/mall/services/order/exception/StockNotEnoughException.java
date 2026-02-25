package edu.nchu.mall.services.order.exception;

import edu.nchu.mall.components.exception.CustomException;

public class StockNotEnoughException extends CustomException {
    public StockNotEnoughException(String message) {
        super(message);
    }
}
