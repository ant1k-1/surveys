package com.example.surveys.enums;

public enum Role {
    ROLE_ADMIN, ROLE_BUSINESS, ROLE_USER;

    public String toString(Role role) {
        return switch (role){
            case ROLE_ADMIN -> "Администратор";
            case ROLE_USER -> "Пользователь";
            case ROLE_BUSINESS -> "Бизнес";
        };
    }
}
