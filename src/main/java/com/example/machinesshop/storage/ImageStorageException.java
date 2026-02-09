package com.example.machinesshop.storage;

/**
 * Exception khi upload/delete image thất bại.
 */
public class ImageStorageException extends RuntimeException {

    public ImageStorageException(String message) {
        super(message);
    }

    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
