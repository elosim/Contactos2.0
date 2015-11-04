package com.izv.dam.Contactos2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.izv.dam.Contactos2.Datos.ConnectContact;
import com.izv.dam.Contactos2.Datos.ConnectXML;
import com.izv.dam.Contactos2.Datos.Contacto;
import com.izv.dam.Contactos2.Datos.OrdenaNombresAsc;
import com.izv.dam.Contactos2.Datos.OrdenaNombresDesc;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Principal extends AppCompatActivity {
    private ClaseAdaptador cl;
    private ClaseAdaptador cn;
    private List<Contacto> lista;
    private ArrayList<String> Tlf;
    private int idaux = 1500;
    private ConnectContact conc;
    private Button btA, btEdit;
    private ImageButton iBc2;
    private ListView lv;
    private SharedPreferences pc;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ListView lv = (ListView) findViewById(R.id.lvlista);
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else {
            if (id == R.id.menu_refresh) {
                copiarefresh();
            }
            if (id == R.id.menu_incremental) {

                incremental();

            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        TextView tvUp = (TextView) findViewById(R.id.tvUp);
        lv = (ListView) findViewById(R.id.lvlista);
        pc = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        ctx = this;

        String aux = pc.getString("start", "no");
        if (aux.equals("no")) {
            SharedPreferences.Editor ed = pc.edit();
            ed.putString("start", "si");
            ed.commit();
            refrescarFecha();
            lista = new ArrayList<>();
            lista = (ArrayList<Contacto>) conc.getLista(this);
            ConnectXML.escribir(lista, this);

        } else {

            lista = new ArrayList<>();
            lista = ConnectXML.leer(this);

        }

        tvUp.setText("Última actualización  " + pc.getString("lastupdate", "fail"));


        cl = new ClaseAdaptador(this, R.layout.item, lista);
        lv.setAdapter(cl);
        lv.setTag(lista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactDialog(position);
            }
        });

        registerForContextMenu(lv);
        Collections.sort(lista, new OrdenaNombresAsc());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = item.getItemId();
        AdapterView.AdapterContextMenuInfo vistaInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = vistaInfo.position;

        if (id == R.id.menu_borrar) {
            borrar(posicion);

            return true;
        } else if (id == R.id.menu_editar) {
            editDialog(posicion);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void contactDialog(final int posicion) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.title);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo, null);
        final TextView tv, tv1, tvTlf;
        String nom;
        int id;
        ArrayList<String> telf = new ArrayList<>();
        String aux = "";


        nom = lista.get(posicion).getNombre();
        id = (int) lista.get(posicion).getId();
        telf = (ArrayList<String>) lista.get(posicion).getlTelf();
        tv = (TextView) vista.findViewById(R.id.tvNomDialog);
        tv1 = (TextView) vista.findViewById(R.id.tvID);
        tvTlf = (TextView) vista.findViewById(R.id.tvTlf);
        iBc2 = (ImageButton) vista.findViewById(R.id.iBc2);


        alert.setView(vista);
        alert.setPositiveButton(R.string.llam,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        llamar(posicion);
                    }
                });

        alert.setNegativeButton(R.string.dial_back, null);
        iBc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarDialog(posicion);
            }
        });

        int i = 0;
        while (i < telf.size()) {
            aux += telf.get(i).toString() + "\n";
            i++;
        }

        tvTlf.setText(aux);
        tv.setText(nom);
        tv1.setText("ID:  " + id);

        alert.show();
    }

    public void borrarDialog(final int posicion) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.bor);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogodel, null);


        alert.setView(vista);
        alert.setPositiveButton(R.string.si,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        borrar(posicion);

                    }
                });


        alert.setView(vista);
        alert.setNegativeButton(R.string.no, null);
        alert.show();
    }

    public void borrar(final int posicion) {
        lista.remove(posicion);
        ConnectXML.escribir(lista, this);
        cl.notifyDataSetChanged();
    }

    public void addDialog(final View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.addtitle);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogoadd, null);
        alert.setView(vista);


        alert.setPositiveButton(R.string.añc,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final EditText etNom, etTel, etTel2, etTel3;
                        ListView lv;

                        etNom = (EditText) vista.findViewById(R.id.etNom);
                        etTel = (EditText) vista.findViewById(R.id.etN1);
                        etTel2 = (EditText) vista.findViewById(R.id.etN2);
                        etTel3 = (EditText) vista.findViewById(R.id.etN3);
                        List<String> telf = new ArrayList<String>();

                        if (!(etTel.getText().toString().equals(""))) {
                            telf.add(etTel.getText().toString());
                        }

                        if (!(etTel2.getText().toString().equals(""))) {
                            telf.add(etTel2.getText().toString());
                        }

                        if (!(etTel3.getText().toString().equals(""))) {
                            telf.add(etTel3.getText().toString());
                        }


                        Contacto c = new Contacto(idaux, telf, etNom.getText().toString());
                        idaux++;
                        lista.add(c);
                        ConnectXML.escribir(lista, ctx);

                        c.setlTelf(telf);

                        cl = new ClaseAdaptador(Principal.this, R.layout.item, lista);
                        lv = (ListView) findViewById(R.id.lvlista);
                        lv.setAdapter(cl);


                    }
                });

        alert.setView(vista);
        alert.setNegativeButton(R.string.dial_back, null);
        alert.show();

    }

    public void editDialog(final int posicion) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.edittitle);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogoedit, null);
        final EditText etNome, etNume, etNume2, etNume3;
        String nom, num = "", num2 = "Añade un nuevo número", num3 = "Añade un nuevo número";

        etNome = (EditText) vista.findViewById(R.id.etNome);
        etNume = (EditText) vista.findViewById(R.id.etNume);
        etNume2 = (EditText) vista.findViewById(R.id.etNume2);
        etNume3 = (EditText) vista.findViewById(R.id.etNume3);

        nom = lista.get(posicion).getNombre().toString();
        if (lista.get(posicion).getlTelf().size() < 2) {
            num = lista.get(posicion).getTelefono(0).toString();

        } else {
            if (lista.get(posicion).getlTelf().size() < 3) {
                num = lista.get(posicion).getTelefono(0).toString();
                num2 = lista.get(posicion).getTelefono(1).toString();

            } else {
                num = lista.get(posicion).getTelefono(0).toString();
                num2 = lista.get(posicion).getTelefono(1).toString();
                num3 = lista.get(posicion).getTelefono(2).toString();
            }
        }

        etNome.setHint(nom);
        etNume.setHint(num);
        etNume2.setHint(num2);
        etNume3.setHint(num3);


        alert.setView(vista);
        alert.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int idd = (int) lista.get(posicion).getId();
                        lista.remove(posicion);
                        EditText etnom, etnum;
                        ArrayList<String> telf = new ArrayList<String>();

                        if (!(etNume.getText().toString().equals(""))) {
                            telf.add(etNume.getText().toString());
                        }

                        if (!(etNume2.getText().toString().equals(""))) {
                            telf.add(etNume2.getText().toString());
                        }
                        if (!(etNume3.getText().toString().equals(""))) {
                            telf.add(etNume3.getText().toString());
                        }
                        Contacto c = new Contacto(idd, telf, etNome.getText().toString());
                        lista.add(c);
                        ConnectXML.escribir(lista, ctx);

                        cl.notifyDataSetChanged();
                    }
                });

        alert.setView(vista);
        alert.setNegativeButton(R.string.dial_back, null);
        alert.show();

    }

    public void ordenaNombresAsc(View view) {
        Collections.sort(lista, new OrdenaNombresAsc());
        cl.notifyDataSetChanged();
    }

    public void ordenaNombresDesc(View view) {
        Collections.sort(lista, new OrdenaNombresDesc());
        cl.notifyDataSetChanged();
    }

    public void llamar(int posicion) {
        String numt = lista.get(posicion).getTelefono(0);
        Uri numero = Uri.parse("tel:" + numt.toString());
        Intent llamar = new Intent(Intent.ACTION_CALL, numero);
        startActivity(llamar);
    }


    public void incremental() {
        List<Contacto> aux = conc.getLista(this);
        int j = 0;
        for (int i = 0; i < lista.size(); i++) {
            if (j == aux.size()) {
                break;
            }
            if (lista.get(i).getId() != aux.get(j).getId()) {
                lista.add(aux.get(j));
            }
            j++;
        }
        ConnectXML.escribir(lista, ctx);
        cl = new ClaseAdaptador(Principal.this, R.layout.item, lista);
        lv = (ListView) findViewById(R.id.lvlista);
        lv.setAdapter(cl);
        lv.setTag(lista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactDialog(position);
            }
        });
        registerForContextMenu(lv);
        refrescarFecha();
    }

    public void copiarefresh() {
        lista = new ArrayList<>();
        lista = (ArrayList<Contacto>) conc.getLista(this);
        ConnectXML.escribir(lista, ctx);

        cl = new ClaseAdaptador(this, R.layout.item, lista);
        lv.setAdapter(cl);
        lv.setTag(lista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactDialog(position);
            }
        });
        registerForContextMenu(lv);
        refrescarFecha();


    }

    public void refrescarFecha() {
        TextView tvUp = (TextView) findViewById(R.id.tvUp);
        pc = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        SharedPreferences.Editor ed = pc.edit();
        ed.putString("lastupdate", mydate);
        ed.commit();

        tvUp.setText("Última actualización  " + pc.getString("lastupdate", "no fecha"));

    }


}
