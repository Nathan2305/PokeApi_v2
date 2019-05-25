package com.example.pokeapi;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button buscar;
    TextInputEditText pokemon_name;
    TextView nombrePk_txt,heightPk_txt,weightPk_txt;
    ImageView fotoPk;
    private static final String PHOTO_POK = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = findViewById(R.id.buscar);
        pokemon_name = findViewById(R.id.pokemon_name);
        nombrePk_txt = findViewById(R.id.nombrePk_txt);
        heightPk_txt=findViewById(R.id.heightPk_txt);
        weightPk_txt=findViewById(R.id.weightPk_txt);
        fotoPk = findViewById(R.id.fotoPk);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://pokeapi.co/api/v2/")
                        .addConverterFactory(GsonConverterFactory.create())  //Como se va a formatear la respuesta JSON
                        .build();
                final PokeApiInterface pokeApiInterface = retrofit.create(PokeApiInterface.class);
                Call<PokemonRespuesta> respuestaCall = pokeApiInterface.obtenerPokemonRespuesta();
                respuestaCall.enqueue(new Callback<PokemonRespuesta>() {
                    @Override
                    public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                        if (response.isSuccessful()) {
                            String namePok = pokemon_name.getText().toString();
                            if (namePok != null || !"".equalsIgnoreCase(namePok)) {
                                PokemonRespuesta pokemonRespuesta = response.body();
                                for (Pokemon aux_pok : pokemonRespuesta.getResults()) {
                                    if (namePok.equalsIgnoreCase(aux_pok.getName())) {
                                        nombrePk_txt.setText(aux_pok.getName());
                                        String[] numberPok = aux_pok.getUrl().split("/");
                                        final String num = numberPok[numberPok.length - 1];
                                        Call<PokemonDetalle> pokemonDetalleCall=pokeApiInterface.obteberPokemonDetalla(num);
                                        pokemonDetalleCall.enqueue(new Callback<PokemonDetalle>() {
                                            @Override
                                            public void onResponse(Call<PokemonDetalle> call, Response<PokemonDetalle> response) {
                                                if (response.isSuccessful()){
                                                    PokemonDetalle pokemonDetalle=response.body();
                                                    weightPk_txt.setText(pokemonDetalle.getWeight());
                                                    heightPk_txt.setText(pokemonDetalle.getHeight());
                                                    Picasso.with(getApplicationContext()).load(PHOTO_POK + num + ".png").into(fotoPk);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<PokemonDetalle> call, Throwable throwable) {

                                            }
                                        });
                                        //Picasso.with(getApplicationContext()).load(PHOTO_POK + num + ".png").into(fotoPk);
                                        break;
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Algo salio mal "+ response.getClass().toString(),Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<PokemonRespuesta> call, Throwable throwable) {
                        Toast.makeText(getApplicationContext(),"Algo salio mal "+ throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
