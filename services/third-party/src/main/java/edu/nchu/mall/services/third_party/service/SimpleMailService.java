package edu.nchu.mall.services.third_party.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface SimpleMailService extends AsyncValidationService<SimpleMailService.MailMessage, SimpleMailService.Validation, Boolean, Boolean>{

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MailMessage {
        @NotNull private String username;
        @NotNull private String to;
        @NotNull private String subject;
        @NotNull private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Validation{
        @NotNull private String target;
        @NotNull private String code;
    }
}
