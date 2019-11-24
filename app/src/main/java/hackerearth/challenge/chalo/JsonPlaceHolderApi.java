package hackerearth.challenge.chalo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("metadata")
    Call<List<Post>> getPosts();

}
