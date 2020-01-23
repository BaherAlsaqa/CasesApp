package im.ehab.casesapp.retrofit;

import im.ehab.casesapp.models.CategoryBody;
import im.ehab.casesapp.models.ErrorBody;
import im.ehab.casesapp.models.MessageBody;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by baher on 09/01/17.
 */

public interface APIInterface {

    @GET("api/v1/categories")
    Call<CategoryBody> getCategory(@Query("page") int nextpage);

    @GET("api/v1/category/{c_id}")
    Call<MessageBody> getCategoryMessages(@Path("c_id") int c_id, @Query("page") int nextpage);

    @GET("api/v1/messages")
    Call<MessageBody> getMessages(@Query("page") int nextpage);

    @FormUrlEncoded
    @POST("api/v1/devicetoken")
    Call<ErrorBody> deviceToken(@Field("device_token") String deviceToken);

    /*@POST("/api/users")
    Call<User> createUser(@CategoryBody User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}
