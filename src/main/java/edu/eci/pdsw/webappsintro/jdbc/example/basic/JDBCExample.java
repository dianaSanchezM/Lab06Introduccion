/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=2109734;
            //registrarNuevoProducto(con, suCodigoECI, "Daniela Gonzalez", 99999999); 
            //registrarNuevoProducto(con, 2108310, "Diana Sanchez", 99999999);
            con.commit();
                     
            consultaNombres(con);
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        PreparedStatement insertProductos = null;
        //Asignar parámetros
        String updateStatement = "insert " + "ORD_PRODUCTOS " + "set codigo=?, nombre=?, precio=?"; 
        insertProductos=con.prepareStatement(updateStatement);
        insertProductos.setInt(1, codigo);
        insertProductos.setString(2, nombre);
        insertProductos.setInt(3,precio);
        //usar 'execute'
        insertProductos.executeUpdate();
        
        
        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido){
        List<String> np=new LinkedList<>();
        
       //Crear prepared statement
        PreparedStatement consultNombres = null;
        //asignar parámetros
        String updateStatement =
        "select ORD_PRODUCTOS.nombre " +
        "from " + "(ORD_PEDIDOS JOIN ORD_DETALLES_PEDIDO ON pedido_fk=ORD_PEDIDOS.codigo) JOIN ORD_PRODUCTOS ON producto_fk=ORD_PRODUCTOS.codigo";
   
        try{
            consultNombres=con.prepareStatement(updateStatement);
            //usar executeQuery
            ResultSet rs=consultNombres.executeQuery();
            while (rs.next()) {
                //Sacar resultado del ResultSet
                String nombre=rs.getString("nombre");
                //Llenar la lista y retornarla
                np.add(nombre);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        
        
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido){
        int total=0;
        
        //Crear prepared statement
        PreparedStatement updateTotal = null;
        //asignar parámetros
        String updateStatement =
        "select ORD_DETALLES_PEDIDO.cantidad, ORD_PRODUCTOS.precio " +
        "from " + "(ORD_PEDIDOS JOIN ORD_DETALLES_PEDIDO ON pedido_fk=ORD_PEDIDOS.codigo) JOIN ORD_PRODUCTOS ON producto_fk=ORD_PRODUCTOS.codigo";
   
        try{
            updateTotal=con.prepareStatement(updateStatement);
            //usar executeQuery
            ResultSet rs=updateTotal.executeQuery();
            while (rs.next()) {
                //Sacar resultado del ResultSet
                int cantidad = rs.getInt("cantidad");
                int precio=rs.getInt("precio");
                total+=cantidad*precio;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }
    
    public static void consultaNombres(Connection con){
        //Crear prepared statement
        PreparedStatement consultNombres = null;
        //asignar parámetros
        String consulta =
        "select nombre " +
        "from " + "ORD_PRODUCTOS ";
   
        try{
            consultNombres=con.prepareStatement(consulta);
            //usar executeQuery
            ResultSet rs=consultNombres.executeQuery();
            while (rs.next()) {
                //Sacar resultado del ResultSet
                String nom=rs.getString("nombre");
                System.out.println(nom);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    
    
    
}
