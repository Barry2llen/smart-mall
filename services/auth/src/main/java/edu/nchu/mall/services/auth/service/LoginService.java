package edu.nchu.mall.services.auth.service;

public interface LoginService {

    boolean sendCode(String emailOrPhone);

    boolean register(String username, String password, String emailOrPhone, String code);

    default boolean logout() {
        return false;
    }

    boolean login(String username, String password);
}
