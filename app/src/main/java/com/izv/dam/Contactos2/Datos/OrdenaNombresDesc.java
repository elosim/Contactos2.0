package com.izv.dam.Contactos2.Datos;

import java.util.Comparator;

/**
 * Created by 2dam on 20/10/2015.
 */
public class OrdenaNombresDesc implements Comparator<Contacto>{
    @Override
    public int compare(Contacto c1, Contacto c2) {
        if (c1.getNombre().toString().compareToIgnoreCase(c2.getNombre()) > 0)
            return -1;
        if (c1.getNombre().toString().compareToIgnoreCase(c2.getNombre()) < 0)
            return 1;
        return 0;
    }
}
