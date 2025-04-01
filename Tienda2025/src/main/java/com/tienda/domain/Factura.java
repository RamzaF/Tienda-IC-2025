package com.tienda.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Table(name = "Factura")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")

    private long idFactura;
    private long idUsuario;
    private Date fecha;
    private double total;
    private int estado;


    public Factura() {
    }

    public Factura(long idUsuario) {
        this.idUsuario = idUsuario;
        this.fecha = Calendar.getInstance().getTime();
        this.estado = 1;
    }
    
    
}
