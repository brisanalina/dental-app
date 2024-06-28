package com.example.demo.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class BadRequestMessage {
    private String errorMessage;
}
