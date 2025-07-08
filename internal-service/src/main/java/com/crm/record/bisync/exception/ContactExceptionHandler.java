package com.crm.record.bisync.exception;

import com.crm.record.bisync.model.ApiResponse;
import com.crm.record.bisync.model.ContactResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ContactExceptionHandler {

    // Handle Record Not Found
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRecordNotFound(RecordNotFoundException ex) {
        ApiResponse response = new ContactResponse("Record not found: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle Schema Validation Failure
    @ExceptionHandler(SchemaValidationException.class)
    public ResponseEntity<ApiResponse> handleSchemaValidationException(SchemaValidationException ex) {
        ApiResponse response = new ContactResponse("Schema validation failed: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle General Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        ApiResponse response = new ContactResponse("Internal server error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
