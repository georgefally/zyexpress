package net.zyexpress.site.api;


public class RestfulResponse {
    public enum ResponseStatus {
        SUCCESS,
        FAILED,
    }

    private ResponseStatus responseStatus;
    private Object payload;

    public RestfulResponse(ResponseStatus status, Object payload) {
        this.responseStatus = status;
        this.payload = payload;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public Object getPayload() {
        return payload;
    }
}
