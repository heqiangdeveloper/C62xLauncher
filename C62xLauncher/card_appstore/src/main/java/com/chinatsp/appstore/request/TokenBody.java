package com.chinatsp.appstore.request;

public class TokenBody {
    private String grant_type;
    private String client_id;
    private String client_secret;

    public TokenBody(String grant_type, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

}
