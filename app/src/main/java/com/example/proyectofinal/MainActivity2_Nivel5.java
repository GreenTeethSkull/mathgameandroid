package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2_Nivel5 extends AppCompatActivity {

    private TextView tv_nombre,tv_score;
    private ImageView iv_Auno,iv_Ados,iv_vidas;
    private EditText et_respuesta;
    private MediaPlayer mp,mp_great,mp_bad;

    int score,numAleatorio_uno,numAleatorio_dos,resultado,vidas=3;
    String nombre_jugador,string_score,string_vidas;

    String numero [] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2__nivel5);

        Toast.makeText(this,"Nivel 5 - Multiplicaciones",Toast.LENGTH_SHORT).show();

        tv_nombre = (TextView)(findViewById(R.id.tv_nombre));
        tv_score = (TextView)(findViewById(R.id.tv_score));
        iv_vidas = (ImageView)(findViewById(R.id.iv_vidas));
        iv_Auno = (ImageView) (findViewById(R.id.iv_num1));
        iv_Ados = (ImageView) (findViewById(R.id.iv_num2));
        et_respuesta = (EditText) (findViewById(R.id.et_resultado));

        nombre_jugador = getIntent().getStringExtra("jugador");
        tv_nombre.setText("Jugador: "+nombre_jugador);

        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tv_score.setText("Score: "+score);

        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if(vidas == 3) {
            iv_vidas.setImageResource(R.drawable.tresvidas);
        }
        if(vidas == 2) {
            iv_vidas.setImageResource(R.drawable.dosvidas);
        }
        if(vidas == 1) {
            iv_vidas.setImageResource(R.drawable.unavida);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mp = MediaPlayer.create(this,R.raw.goats);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this,R.raw.wonderful);
        mp_bad = MediaPlayer.create(this,R.raw.bad);

        NumAleatorio();
    }

    public void Comparar (View view2) {
        String respuesta = et_respuesta.getText().toString();

        if(!respuesta.equals("")) {

            int respuesta_jugador = Integer.parseInt(respuesta);
            if (resultado == respuesta_jugador) {

                mp_great.start();
                score++;
                tv_score.setText("Score: "+score);
                et_respuesta.setText("");
                BaseDeDatos();

            } else {

                mp_bad.start();
                vidas--;
                BaseDeDatos();

                switch (vidas){
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this,"Te quedan 2 manzanas",Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this,"Te quedan 1 manzana",Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this,"Has perdido todas tus manzanas",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }

                et_respuesta.setText("");

            }
            NumAleatorio();

        } else {
            Toast.makeText(this,"Escribe tu respuesta",Toast.LENGTH_SHORT).show();
        }
    }

    public void NumAleatorio() {
        if (score <= 49) {

            numAleatorio_uno = (int) (Math.random()*10);
            numAleatorio_dos = (int) (Math.random()*10);

            resultado = numAleatorio_uno * numAleatorio_dos;

            for (int i = 0; i < numero.length;i++) {
                int id = getResources().getIdentifier(numero[i],"drawable",getPackageName());
                if (numAleatorio_uno == i) {
                    iv_Auno.setImageResource(id);
                }
                if (numAleatorio_dos == i) {
                    iv_Ados.setImageResource(id);
                }
            }

        } else {
            Intent intent = new Intent(this,MainActivity2_Nivel6.class);

            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jugador",nombre_jugador);
            intent.putExtra("score",string_score);
            intent.putExtra("vidas",string_vidas);

            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }

    public void BaseDeDatos () {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"db",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor consulta = db.rawQuery("select * from puntaje where score = (select max(score) from puntaje)",null);
        if (consulta.moveToFirst()) {

            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int bestScore = Integer.parseInt(temp_score);

            if (score > bestScore) {
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre",nombre_jugador);
                modificacion.put("score",score);

                db.update("puntaje",modificacion,"score="+bestScore,null);
            }
            db.close();

        } else {
            ContentValues insertar = new ContentValues();

            insertar.put("nombre",nombre_jugador);
            insertar.put("score",score);

            db.insert("puntaje",null,insertar);
            db.close();
        }
    }

    @Override
    public void onBackPressed() {

    }
}