package tn.iteam.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerResponseStatus {
    CREATED("201", "Customer created successfully"),
    SUCCESS("200", "Request processed successfully"),
    BAD_REQUEST("400", "Bad request, please check the input parameters"),
    SERVER_ERROR("500", "An error occurred while processing the request");

    private final String code;
    private final String message;
}
