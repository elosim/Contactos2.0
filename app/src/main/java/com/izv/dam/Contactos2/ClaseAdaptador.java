package com.izv.dam.Contactos2;

import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.izv.dam.Contactos2.Datos.ConnectContact;
import com.izv.dam.Contactos2.Datos.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ClaseAdaptador extends ArrayAdapter<Contacto>{


    private Context ctx;
    private int res;
    private LayoutInflater lInflator;
    private ArrayList<Contacto> valores;
    private List<String> Tlf;
    private DownloadManager contentResolver;
    private ConnectContact gc;

    public DownloadManager getContentResolver() {
        return contentResolver;
    }

    static class ViewHolder {
        public TextView tv1, tv2;
        public ImageView iv;
    }

    public ClaseAdaptador(Context context, int resource, List<Contacto> lista) {
        super(context, resource, lista);
        this.ctx = context;
        this.res = resource;
        this.valores = (ArrayList<Contacto>) lista;
        this.lInflator = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public void borrar(int position) {

        valores.remove(position);
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1
        ViewHolder gv = new ViewHolder();


        if(convertView==null){
            convertView = lInflator.inflate(res, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.ivFlor);
            TextView tv = (TextView) convertView.findViewById(R.id.tvSuperior);
            gv.tv1 = tv;
            tv = (TextView) convertView.findViewById(R.id.tvInferior);
            gv.tv2 = tv;

            gv.iv = iv;
            convertView.setTag(gv);
        } else {
            gv = (ViewHolder) convertView.getTag();
        }


        Tlf= valores.get(position).getlTelf();
        valores.get(position).setlTelf(Tlf);
        if(valores.get(position).getlTelf().size()>1){
            gv.iv.setImageResource(R.drawable.pos);
        }else{
            gv.iv.setImageResource(R.drawable.neg);
        }
        gv.tv1.setText(valores.get(position).getNombre().toString());
        gv.tv2.setText(valores.get(position).toStringMod());
        valores.get(position).setlTelf(Tlf);
        return convertView;
    }


}