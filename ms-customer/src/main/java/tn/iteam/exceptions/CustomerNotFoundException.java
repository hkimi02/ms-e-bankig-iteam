package tn.iteam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found %s and value %s",
                resourceName, fieldName, fieldValue));
    }
}
