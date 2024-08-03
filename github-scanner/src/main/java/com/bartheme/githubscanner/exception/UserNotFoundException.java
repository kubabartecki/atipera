package com.bartheme.githubscanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends Exception {

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Could not find user with given username";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
