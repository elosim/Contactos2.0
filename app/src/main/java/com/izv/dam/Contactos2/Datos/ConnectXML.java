package com.izv.dam.Contactos2.Datos;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 2dam on 04/11/2015.
 */
 public class ConnectXML  {
    public static void escribir(List<Contacto> x, Context ctx) {
        Random r = new Random();
        try {
            FileOutputStream fosxml = new FileOutputStream(new File(ctx.getExternalFilesDir(null), "contactos.xml"));
            XmlSerializer docxml = Xml.newSerializer();
            docxml.setOutput(fosxml, "UTF-8");
            docxml.startDocument(null, Boolean.valueOf(true));
            docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            ArrayList<String> l = new ArrayList<>();
            docxml.startTag(null, "contactos");
            for (int i = 0; i < x.size(); i++) {
                docxml.startTag(null, "contacto");
                docxml.startTag(null, "nombre");
                docxml.attribute(null, "id", String.valueOf(x.get(i).getId()));
                docxml.text(x.get(i).getNombre().toString());
                docxml.endTag(null, "nombre");
                for (int j = 0; j < x.get(i).getlTelf().size(); j++) {
                    docxml.startTag(null, "telefono");
                    docxml.text(x.get(i).getTelefono(j).toString());
                    docxml.endTag(null, "telefono");
                }
                docxml.endTag(null, "contacto");
            }
            docxml.endDocument();
            docxml.flush();
            fosxml.close();
        }catch (Exception e){
        }
    }

    public static List<Contacto> leer(Context ctx)  {
        List<Contacto> lis = new ArrayList();
        Contacto c = null;
        int id = 0;
        ArrayList<String> telf = new ArrayList<>();
        String nom = "";
        int evento=0;
        XmlPullParser lectorxml = Xml.newPullParser();
        try{
            lectorxml.setInput(new FileInputStream(new File(ctx.getExternalFilesDir(null), "contactos.xml")), "utf-8");
            evento = lectorxml.getEventType();

        int atrib = 0;
            while (evento != XmlPullParser.END_DOCUMENT) {

                if (evento == XmlPullParser.START_TAG) {

                    String etiqueta = lectorxml.getName();
                    Log.v("etiqueta", etiqueta);
                    if (etiqueta.compareTo("contacto") == 0) {
                        telf = new ArrayList<>();
                        c = null;
                        atrib = 0;
                        nom = "";
                    }
                    if (etiqueta.compareTo("nombre") == 0) {
                        atrib = Integer.parseInt(lectorxml.getAttributeValue(null, "id"));
                        nom = lectorxml.nextText();

                    } else if (etiqueta.compareTo("telefono") == 0) {
                        String texto = lectorxml.nextText();
                        telf.add(texto);
                    }
                }
                if (evento == XmlPullParser.END_TAG) {
                    String etiqueta = lectorxml.getName();
                    if (etiqueta.compareTo("contacto") == 0) {
                        c = new Contacto(atrib, telf, nom);
                        lis.add(c);
                    }
                }

                evento = lectorxml.next();
            }
        }catch (Exception e){
        }
        return lis;
    }

}
