package com.example.pokeapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeInterface {
    @GET("pokemon")
    Call<PokemonRespuesta> obtenerPokemonRespuesta(@Query("limit") String limit);

    @GET("pokemon/{number_pok}")
    Call<PokemonDetalle> obteberPokemonDetalla(@Path("number_pok") String num_pok);
}
