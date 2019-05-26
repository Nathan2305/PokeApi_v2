package com.example.pokeapi;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Circle;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button buscar, limpiar;
    TextInputEditText pokemon_name;
    TextView nombrePk_txt, heightPk_txt, weightPk_txt;
    ImageView fotoPk;
    ProgressBar pbar;
    String num = "";
    Circle circle;
    PokeInterface pokeApiInterface = null;
    String namePok = "";
    private static final String PHOTO_POK = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = findViewById(R.id.buscar);
        pokemon_name = findViewById(R.id.pokemon_name);
        nombrePk_txt = findViewById(R.id.nombrePk_txt);
        heightPk_txt = findViewById(R.id.heightPk_txt);
        weightPk_txt = findViewById(R.id.weightPk_txt);
        fotoPk = findViewById(R.id.fotoPk);
        limpiar = findViewById(R.id.limpiar);
        pbar = findViewById(R.id.pbar);
        circle = new Circle();
        pbar.setProgressDrawable(circle);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namePok = pokemon_name.getText().toString();
                if (!namePok.isEmpty()) {
                    pbar.setVisibility(View.VISIBLE);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://pokeapi.co/api/v2/")
                            .addConverterFactory(GsonConverterFactory.create())  //Como se va a formatear la respuesta JSON
                            .build();
                    pokeApiInterface = retrofit.create(PokeInterface.class);
                    Call<PokemonRespuesta> respuestaCall = pokeApiInterface.obtenerPokemonRespuesta("300"); // 300 primeros
                    respuestaCall.enqueue(new Callback<PokemonRespuesta>() {
                        @Override
                        public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                            PokemonRespuesta pokemonRespuesta = response.body();
                            boolean existsPokemon = false;
                            for (Pokemon aux_pok : pokemonRespuesta.getResults()) {
                                if (namePok.equalsIgnoreCase(aux_pok.getName())) {
                                    existsPokemon = true;
                                    String[] numberPok = aux_pok.getUrl().split("/");
                                    num = numberPok[numberPok.length - 1];
                                    Call<PokemonDetalle> pokemonDetalleCall = pokeApiInterface.obteberPokemonDetalla(num);
                                    pokemonDetalleCall.enqueue(new Callback<PokemonDetalle>() {
                                        @Override
                                        public void onResponse(Call<PokemonDetalle> call, Response<PokemonDetalle> response) {
                                            if (response.isSuccessful()) {
                                                PokemonDetalle pokemonDetalle = response.body();
                                                nombrePk_txt.setText(namePok);
                                                weightPk_txt.setText(pokemonDetalle.getWeight());
                                                heightPk_txt.setText(pokemonDetalle.getHeight());
                                                Picasso.with(getApplicationContext()).load(PHOTO_POK + num + ".png").into(fotoPk);
                                                pbar.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PokemonDetalle> call, Throwable throwable) {
                                        }
                                    });
                                }
                                if (existsPokemon) {
                                    break;
                                }
                            }
                            if (!existsPokemon) {
                                Toast.makeText(getApplicationContext(), "No existe el pokemon " + namePok, Toast.LENGTH_SHORT).show();
                                cleanViews();
                                pbar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<PokemonRespuesta> call, Throwable throwable) {

                        }
                    });
                }
            }
        });
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanViews();
            }
        });

    }

    public void cleanViews() {
        pokemon_name.setText("");
        nombrePk_txt.setText("");
        weightPk_txt.setText("");
        heightPk_txt.setText("");
        fotoPk.setImageResource(android.R.color.transparent);
        pokemon_name.requestFocus();
    }
}





