package com.accelerate.napknbook.api;

import com.accelerate.napknbook.models.AuthResponse;
import com.accelerate.napknbook.models.Character;
import com.accelerate.napknbook.models.Comment;
import com.accelerate.napknbook.models.Convo;
import com.accelerate.napknbook.models.CreateTaskRequestBody;
import com.accelerate.napknbook.models.GoogleLoginRequest;
import com.accelerate.napknbook.models.GoogleRegisterRequest;
import com.accelerate.napknbook.models.Inventory;
import com.accelerate.napknbook.models.InventoryItem;
import com.accelerate.napknbook.models.InventoryRequestBody;
import com.accelerate.napknbook.models.LoginRequest;
import com.accelerate.napknbook.models.PurchaseVerificationRequest;
import com.accelerate.napknbook.models.RegisterRequest;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.models.SkillRequestBody;
import com.accelerate.napknbook.models.TaskRequestBody;
import com.accelerate.napknbook.models.UpdateTaskRequestBody;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.models.TaskCategory;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import okhttp3.ResponseBody;
import retrofit2.http.Query;

public interface NapknbookService {


    @GET("/skill/{characterPk}/{skillPk}")
    Call<Skill> getSkill(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk,
            @Path("skillPk") String skillPk);

    @GET("/skills/{characterPk}")
    Call<List<Skill>> getSkills(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk);

    @GET("/inventoryItem/{characterPk}/{inventoryItemPk}")
    Call<InventoryItem> getInventoryItem(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk,
            @Path("inventoryItemPk") String inventoryItemPk);

    @GET("/inventory/{characterPk}")
    Call<List<InventoryItem>> getInventory(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk);


    @GET("/convo/{pk}")
    Call<Convo> getConvo(
            @Header("Authorization") String token,
            @Path("pk") int convoPk);

    @GET("/convoList/{skillPk}")
    Call<List<Convo>> getConvos(
            @Header("Authorization") String token,
            @Path("skillPk") String skillPk);

    @GET("/comment/{pk}")
    Call<Comment> getComment(
            @Header("Authorization") String token,
            @Path("pk") int commentPk);

    @GET("/commentList/{convoPk}")
    Call<List<Comment>> getComments(
            @Header("Authorization") String token,
            @Path("convoPk") String convoPk);

    @POST("/generateSkill/")
    Call<Skill> generateSkill(
            @Header("Authorization") String token,
            @Header("X-CSRFToken") String csrfToken,
            @Body SkillRequestBody request);

    @POST("/generateInventory/")
    Call<Inventory> generateInventory(
            @Header("Authorization") String token,
            @Header("X-CSRFToken") String csrfToken,
            @Body InventoryRequestBody request);

    @POST("/generateTaskList/")
    Call<List<Task>> generateTaskList(
            @Header("Authorization") String token,
            @Header("X-CSRFToken") String csrfToken,
            @Body TaskRequestBody request);

    // --- TASKS ---

    @GET("/tasks/")
    Call<List<Task>> getTasks(
            @Header("Authorization") String token,
            @Query("characterPk") String characterPk
    );

    @GET("/tasks/{taskPk}/")
    Call<Task> getTask(
            @Header("Authorization") String token,
            @Query("characterPk") String characterPk,
            @Path("taskPk") String taskPk
    );

    @POST("/tasks/")
    Call<Task> createTask(
            @Header("Authorization") String token,
            @Body CreateTaskRequestBody createTaskRequestBody
    );

    @PUT("/tasks/{taskPk}/")
    Call<Task> updateTask(
            @Header("Authorization") String token,
            @Path("taskPk") String taskPk,
            @Body UpdateTaskRequestBody updateTaskRequestBody
    );

    @DELETE("/tasks/{taskPk}/")
    Call<ResponseBody> deleteTask(
            @Header("Authorization") String token,
            @Path("taskPk") String taskPk
    );

    @DELETE("/tasks/completed/{categoryPk}/")  // <-- notice the trailing slash
    Call<ResponseBody> deleteCompletedTasks(
            @Header("Authorization") String token,
            @Path("categoryPk") String categoryPk
    );

    @PATCH("/tasks/{taskPk}/priority")
    Call<ResponseBody> updateTaskPriority(
            @Header("Authorization") String token,
            @Path("taskPk") String taskPk,
            @Body Map<String, String> body
    );

    @PATCH("/tasks/{taskPk}/status")
    Call<ResponseBody> updateTaskStatus(
            @Header("Authorization") String token,
            @Path("taskPk") String taskPk,
            @Body Map<String, Boolean> body
    );

    @PATCH("/tasks/{taskPk}/")
    Call<ResponseBody> updateTaskField(
            @Header("Authorization") String token,
            @Path("taskPk") String taskPk,
            @Body Map<String, Object> body
    );


    // --- TASK CATEGORIES ---
    @GET("/categories/")
    Call<List<TaskCategory>> getTaskCategories(
            @Header("Authorization") String token,
            @Query("characterPk") String characterPk
    );

    @POST("/categories/")
    Call<TaskCategory> createTaskCategory(
            @Header("Authorization") String token,
            @Body Map<String, String> body
    );


    @PUT("/categories/{categoryPk}/")
    Call<TaskCategory> updateTaskCategory(
            @Header("Authorization") String token,
            @Path("categoryPk") String categoryPk,
            @Body Map<String, String> body
    );

    @DELETE("/categories/{categoryPk}/")
    Call<ResponseBody> deleteTaskCategory(
            @Header("Authorization") String token,
            @Path("categoryPk") String categoryPk
    );


    @GET("/categories/{categoryPk}/tasks/")
    Call<List<Task>> getTasksByTaskCategory(
            @Header("Authorization") String token,
            @Path("categoryPk") String categoryPk
    );

    // --- USERS ---

    @GET("/users/")
    Call<List<User>> getUsers(
            @Header("Authorization") String token
    );

    @GET("/users/{userPk}")
    Call<User> getUser(
            @Header("Authorization") String token,
            @Path("userPk") String userPk
    );

    @POST("/entities/")
    Call<User> createEntity(
            @Header("Authorization") String token,
            @Body User newUser
    );

    @PUT("/entities/{entityPk}/")
    Call<User> updateEntity(
            @Header("Authorization") String token,
            @Path("entityPk") String entityPk,
            @Body User updatedUser
    );

    @DELETE("/entities/{entityPk}/")
    Call<ResponseBody> deleteEntity(
            @Header("Authorization") String token,
            @Path("entityPk") String entityPk
    );

    // --- CHARACTERS ---

    @GET("/characters/")
    Call<List<Character>> getCharacters(
            @Header("Authorization") String token
    );




    @GET("/characters/{characterPk}")
    Call<Character> getCharacter(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk
    );

    @POST("/characters/")
    Call<Character> createCharacter(
            @Header("Authorization") String token,
            @Body Character character
    );

    @PUT("/characters/{characterPk}/")
    Call<Character> updateCharacter(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk,
            @Body Character character
    );

    @DELETE("/characters/{characterPk}/")
    Call<ResponseBody> deleteCharacter(
            @Header("Authorization") String token,
            @Path("characterPk") String characterPk
    );

    @POST("/generateCharacter/")
    Call<Character> generateCharacter(
            @Header("Authorization") String token,
            @Header("X-CSRFToken") String csrfToken);

    @POST("/wallet/balance")
    Call<ResponseBody> getBalance(
            @Header("Authorization") String token
    );

    @GET("/api/get-csrf-token/")
    Call<ResponseBody> getCsrfToken();

    @POST("/register/")
    Call<AuthResponse> registerUser(
            @Body RegisterRequest request);

    @POST("/login/")
    Call<AuthResponse> loginUser(
            @Body LoginRequest request);

    @POST("/google_register/")
    Call<AuthResponse> googleRegisterUser(
            @Body GoogleRegisterRequest request);

    @POST("/google_login/")
    Call<AuthResponse> googleLoginUser(
            @Body GoogleLoginRequest request);

    @POST("/api/verify-purchase/")
    Call<ResponseBody> verifyPurchase(
            @Header("Authorization") String token,
            @Body PurchaseVerificationRequest request
    );

    @Multipart
    @POST("/process-image/")
    Call<ResponseBody> uploadImage(
            @Header("Authorization") String token,
            @Part("characterPk") RequestBody characterPk,
            @Part MultipartBody.Part image
    );

}
