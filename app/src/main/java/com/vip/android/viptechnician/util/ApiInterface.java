package com.vip.android.viptechnician.util;
import com.vip.android.viptechnician.beans.UpdateTokenResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("Registration/UpdateToken")
    public Call<UpdateTokenResponse> updateToken(
            @Field("UserId") String user_id,
            @Field("Token") String token
    );
}
