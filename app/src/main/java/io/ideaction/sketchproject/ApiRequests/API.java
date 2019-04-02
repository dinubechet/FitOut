package io.ideaction.sketchproject.ApiRequests;


import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Models.CommentModel;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.NotificationModel;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    //-------------------------------Authentication--------------------------------------------------

    @POST("/api/auth/login")
    @FormUrlEncoded
    Call<HashMap> getToken(@Field("email") String email, @Field("password") String pass);

    @Headers("Accept: application/json")
    @GET("/api/auth/user")
    Call<HashMap> getMe(@Header("Authorization") String token);

    @POST("/api/auth/register")
    @FormUrlEncoded
    Call<HashMap> register(@Field("email") String email, @Field("password") String pass, @Field("password_confirmation") String repeatPass,
                           @Field("name") String name, @Field("username") String username);

    @POST("/api/v1/social_auth")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<HashMap> social_auth(@Field("name") String name, @Field("email") String email, @Field("provider") String provider,
                              @Field("provider_user_id") String provider_user_id);

    //-------------------------------ForgotPassword--------------------------------------------------

    @POST("/api/v1/password/create")
    @FormUrlEncoded
    Call<HashMap> forgotPassword1(@Field("email") String email);

    @POST("/api/v1/password/reset")
    @FormUrlEncoded
    Call<HashMap> forgotPassword2(@Field("email") String email, @Field("token") String token);

    @POST("/api/v1/password/reset")
    @FormUrlEncoded
    Call<HashMap> forgotPassword3(@Field("email") String email, @Field("token") String token,
                                  @Field("password") String password, @Field("password_confirmation") String passConfirmation);

    //-------------------------------Posts and Feeds-------------------------------------------------

    @GET("/api/v1/getMyPosts")
    Call<List<SportFeedModel>> getMyPosts(@Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getMyLikedPosts")
    Call<List<SportFeedModel>> getMyLikedPosts(@Query("page") String page, @Header("Authorization") String token);

    @POST("/api/v1/uploadAvatar")
    @Multipart
    @Headers("Accept: application/json")
    Call<HashMap> upload(@Part MultipartBody.Part image, @Header("Authorization") String token);

    @POST("/api/v1/likeUnlikePost/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> likeUnlike(@Path("id") String id, @Header("Authorization") String token);

    @GET("/api/v1/getMyAttendingEvents")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getMyAttendingEvents(@Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getMySavedEvents")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getMySavedEvents(@Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getMyEvents")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getMyEvents(@Query("page") String page, @Header("Authorization") String token);

    @POST("/api/v1/saveUnsaveEvent/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> saveUnsave(@Path("id") String id, @Header("Authorization") String token);

    @POST("/api/v1/attendUnattendEvent/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> attentUnattend(@Path("id") String id, @Header("Authorization") String token);

    @GET("/api/v1/getPosts")
    @Headers("Accept: application/json")
    Call<List<SportFeedModel>> getSportFeed(@Query("page") String page, @Query("category_id") String category, @Header("Authorization") String token, @Query("orderBy") String order, @Query("lat") String lat, @Query("long") String lng);

    @POST("/api/v1/setDeviceToken")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<Void> addToken(@Field("device_token") String device_token, @Header("Authorization") String token);

    @POST("/api/v1/addPost")
    @Headers("Accept: application/json")
    @Multipart
    Call<HashMap> createNewPost(@Part("text") RequestBody description, @Part("category_id") RequestBody category,
                                @Part List<MultipartBody.Part> files, @Header("Authorization") String token);

    @GET("/api/v1/getUsers")
    Call<List<User>> getPeople(@Query("keyWord") String searchText, @Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getEvents")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getEvents(@Query("page") String page, @Query("category_id") String category,
                                     @Header("Authorization") String token, @Query("orderBy") String order,
                                     @Query("lat") String lat, @Query("long") String lng);

    @POST("/api/v1/addEvent")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<EventModel> createNewEvent(@Field("name") String name, @Field("address") String address, @Field("description") String description, @Field("category_id") String category,
                                    @Field("when") String when, @Header("Authorization") String token);

    @POST("/api/v1/updateProfile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<User> sendSettings(@Field("name") String name, @Field("username") String username, @Field("description") String description,
                            @Field("city") String city, @Field("country") String country, @Field("training_place") String training_place,
                            @Field("training_hours") String training_hours, @Header("Authorization") String token);

    @POST("/api/v1/updateEvent/{id}")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<EventModel> updateEvent(@Path("id") String id, @Field("name") String name, @Field("address") String address, @Field("description") String description,
                                 @Field("when") String when, @Header("Authorization") String token);


    @POST("/api/v1/changePassword")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<HashMap> changePassword(@Field("old_password") String old_password, @Field("password") String password,
                                 @Field("password_confirmation") String password_confirmation, @Header("Authorization") String token);

    @GET("/api/v1/getUser/{id}")
    @Headers("Accept: application/json")
    Call<User> getPerson(@Path("id") String id, @Header("Authorization") String token);

    @GET("/api/v1/getMyPosts/{id}")
    Call<List<SportFeedModel>> getForeignPosts(@Path("id") Integer id, @Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getMyLikedPosts/{id}")
    Call<List<SportFeedModel>> getForeignLikedPosts(@Path("id") Integer id, @Query("page") String page, @Header("Authorization") String token);

    @POST("/api/v1/follow/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> follow(@Path("id") Integer id, @Header("Authorization") String token);

    @POST("/api/v1/unFollow/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> unFollow(@Path("id") Integer id, @Header("Authorization") String token);

    @POST("/api/v1/commentPost/{id}")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<HashMap> addComment(@Path("id") Integer postID, @Field("message") String commentBody, @Header("Authorization") String token);

    @POST("/api/v1/sendFriendRequest/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> friendRequest(@Path("id") Integer id, @Header("Authorization") String token);

    @POST("/api/v1/denyFriendRequest/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> denyFriendRequest(@Path("id") Integer id, @Header("Authorization") String token);

    @POST("/api/v1/acceptFriendRequest/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> acceptFriendRequest(@Path("id") Integer id, @Header("Authorization") String token);

    @POST("/api/v1/unFriend/{id}")
    @Headers("Accept: application/json")
    Call<HashMap> unFriend(@Path("id") Integer id, @Header("Authorization") String token);

    @GET("/api/v1/apiPostComments/{id}")
    @Headers("Accept: application/json")
    Call<List<CommentModel>> getComments(@Path("id") Integer id, @Query("page") Integer page, @Header("Authorization") String token);

    @GET("/api/v1/getMyEvents/{id}")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getForeignEvents(@Path("id") Integer id, @Query("page") String page,
                                            @Header("Authorization") String token);

    @GET("/api/v1/getMyAttendingEvents/{id}")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getForeignAttendingEvents(@Path("id") Integer id, @Query("page") String page,
                                                     @Header("Authorization") String token);

    @GET("/api/v1/getMySavedEvents/{id}")
    @Headers("Accept: application/json")
    Call<List<EventModel>> getForeignSavedEvents(@Path("id") Integer id, @Query("page") String page,
                                                 @Header("Authorization") String token);

    @GET("/api/v1/getNotifications")
    @Headers("Accept: application/json")
    Call<List<NotificationModel>> getNotifications(@Header("Authorization") String token);

    @GET("/api/v1/getPartners/{id}")
    Call<List<User>> getPartners(@Path("id") String id, @Query("keyWord") String searchText,
                                 @Query("page") String page, @Header("Authorization") String token);

    @GET("/api/v1/getPartners")
    Call<List<User>> getMyPartners(@Query("keyWord") String searchText, @Query("page") String page,
                                   @Header("Authorization") String token);

    @GET("/api/v1/getPost/{id}")
    Call<SportFeedModel> getPost(@Path("id") Integer id, @Header("Authorization") String token);

    @GET("/api/v1/getEvent/{id}")
    Call<EventModel> getEvent(@Path("id") Integer id, @Header("Authorization") String token);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/v1/sendNotificationMessage")
    Call<Void> sendNotification(@Header("Authorization") String token, @Field("email") String email, @Field("text") String text, @Field("title") String title);

    @GET("/api/v1/getUserByEmail/{email}")
    @Headers("Accept: application/json")
    Call<User> getPersonByEmail(@Path("email") String email, @Header("Authorization") String token);

    @GET("api/v1/getFollowers/{id}")
    @Headers("Accept: application/json")
    Call<List<User>> getFollowersList(@Path("id") Integer id, @Header("Authorization") String token);

    @GET("api/v1/getFollowings/{id}")
    @Headers("Accept: application/json")
    Call<List<User>> getFollowingsList(@Path("id") Integer id, @Header("Authorization") String token);

    @DELETE("/api/v1/deletePost/{id}")
    @Headers("Accept: application/json")
    Call<SportFeedModel>deletePost(@Path("id") Integer id, @Header("Authorization") String token);

    @DELETE("/api/v1/deleteEvent/{id}")
    @Headers("Accept: application/json")
    Call<EventModel>deleteEvent(@Path("id") String id, @Header("Authorization") String token);

    @DELETE("/api/v1/suspendAccount")
    @Headers("Accept: application/json")
    Call<User> deleteAccount(@Header("Authorization") String token);

    @POST("/api/v1/updatePost/{id}")
    @Headers("Accept: application/json")
    @Multipart
    Call<HashMap> updatePost(@Part("text") RequestBody description,
                             @Part List<MultipartBody.Part> files, @Header("Authorization") String token, @Path("id") String id);

    @GET("/api/v1/blockedFriends")
    @Headers("Accept: application/json")
    Call<List<User>> getBlockedUsers(@Header("Authorization") String token);


    @Headers("Accept: application/json")
    @POST("/api/v1/blockFriend/{id}")
    Call<Void> blockuser(@Header("Authorization") String token, @Path("id") String id);


    @Headers("Accept: application/json")
    @POST("/api/v1/unblockFriend/{id}")
    Call<Void> unblockuser(@Header("Authorization") String token, @Path("id") String id);


}
