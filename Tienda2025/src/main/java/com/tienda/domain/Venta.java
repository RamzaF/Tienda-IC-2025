package com.tienda.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Venta")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private long idVenta;
    private long idFactura;
    private long idProducto;
    private double precio;
    private int cantidad;


    public Venta() {
    }

    public Venta(long idFactura, long idProducto, double precio) {
        this.idFactura = idFactura;
        this.idProducto = idProducto;
        this.precio = precio;
    }

    public Venta(long idFactura, long idProducto, double precio, int cantidad) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
