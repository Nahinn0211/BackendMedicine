package hunre.edu.vn.backend.payload;

public class SocialLoginRequest {
    private String accessToken;

    public SocialLoginRequest() {}

    public SocialLoginRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}