package com.surya.empsync.exception;

public class EmpSyncException extends RuntimeException{

    private static final long serialVersionId = 1L;

    public EmpSyncException(){

    }

    public EmpSyncException(String message){
        super(message);
    }
}
