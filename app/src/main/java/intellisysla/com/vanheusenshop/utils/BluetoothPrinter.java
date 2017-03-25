package intellisysla.com.vanheusenshop.utils;

/**
 * Created by turupawn on 3/24/17.
 */

import android.content.res.Resources;

import java.text.DecimalFormat;
import java.util.ArrayList;

import datamaxoneil.connection.ConnectionBase;
import datamaxoneil.connection.Connection_Bluetooth;
import datamaxoneil.connection.Connection_TCP;
import datamaxoneil.printer.DocumentDPL;
import datamaxoneil.printer.DocumentDPL.*;
import datamaxoneil.printer.DocumentEZ;
import datamaxoneil.printer.DocumentLP;
import datamaxoneil.printer.DocumentExPCL_LP;
import datamaxoneil.printer.DocumentExPCL_PP;
import datamaxoneil.printer.DocumentExPCL_PP.*;
import datamaxoneil.printer.ParametersDPL;
import datamaxoneil.printer.ParametersDPL.*;
import datamaxoneil.printer.ParametersEZ;
import datamaxoneil.printer.ParametersExPCL_LP;
import datamaxoneil.printer.ParametersExPCL_LP.*;
import datamaxoneil.printer.ParametersExPCL_PP;
import datamaxoneil.printer.ParametersExPCL_PP.*;
import datamaxoneil.printer.UPSMessage;
import datamaxoneil.printer.configuration.dpl.*;
import datamaxoneil.printer.configuration.dpl.MemoryModules_DPL.FileInformation;
import datamaxoneil.printer.configuration.ez.*;
import datamaxoneil.printer.configuration.expcl.*;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.SplashActivity;

public class BluetoothPrinter {

    public static void print(String bluetooth_mac_address,
                             String fecha, String folio_movil, String codigo_cliente, String nombre_cliente,
                             String direccion_cliente, String telefono_cliente, String rtn_cliente, String vendedor,
                             ArrayList<String> productos, ArrayList<Integer> cantidades, ArrayList<Double> precios_unitarios, ArrayList<Double> totales,
                             double sub_total, double descuento, double total_despues_descuento, double isv, double gran_total)
    {
        byte[] printData = {0};

        DocumentExPCL_LP docExPCL_LP = new DocumentExPCL_LP(3);
        ParametersExPCL_LP paramExPCL_LP = new ParametersExPCL_LP();


        paramExPCL_LP.setFontIndex(1);
        paramExPCL_LP.setIsBold(true);
        docExPCL_LP.writeText(SplashActivity.resources.getString(R.string.CompanyName), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(5);
        paramExPCL_LP.setIsBold(false);
        docExPCL_LP.writeText(SplashActivity.resources.getString(R.string.CompanyAddress), paramExPCL_LP);
        docExPCL_LP.writeText("Tel. " + SplashActivity.resources.getString(R.string.CompanyPhone) + " Fax. " + SplashActivity.resources.getString(R.string.CompanyFax), paramExPCL_LP);

        paramExPCL_LP.setFontIndex(3);
        docExPCL_LP.writeText("RTN: " + SplashActivity.resources.getString(R.string.CompanyRTN), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(2);
        docExPCL_LP.writeText("*ORDEN DE VENTA*", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(5);
        docExPCL_LP.writeText("Fecha: " + fecha, paramExPCL_LP);
        docExPCL_LP.writeText("Folio Movil: " + folio_movil, paramExPCL_LP);
        docExPCL_LP.writeText("Codigo del Cliente: " + codigo_cliente, paramExPCL_LP);
        docExPCL_LP.writeText(nombre_cliente, paramExPCL_LP);
        docExPCL_LP.writeText("Direccion: ", paramExPCL_LP);
        docExPCL_LP.writeText(direccion_cliente, paramExPCL_LP);

        docExPCL_LP.writeText("Telefono: " + telefono_cliente, paramExPCL_LP);
        docExPCL_LP.writeText("RTN: " + rtn_cliente, paramExPCL_LP);
        docExPCL_LP.writeText("Vendedor: " + vendedor, paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        docExPCL_LP.writeText("PRODUCTO\nCANTIDAD\tPECIO UNIT\tTOTAL", paramExPCL_LP);


        DecimalFormat df = new DecimalFormat("0.00");
        for(int i=0;i<productos.size();i++)
        {
            docExPCL_LP.writeText(productos.get(i)+'\n'+cantidades.get(i)+'\t'+df.format(precios_unitarios.get(i))+'\t'+df.format(totales.get(i)));
        }

        paramExPCL_LP.setIsBold(false);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("Subtotal:\t\t\t"+df.format(sub_total), paramExPCL_LP);
        docExPCL_LP.writeText("% Descuento:\t\t\t"+ df.format(descuento), paramExPCL_LP);
        docExPCL_LP.writeText("Total despues del descuento:\t"+ df.format(total_despues_descuento), paramExPCL_LP);
        docExPCL_LP.writeText("ISV:\t\t\t"+ df.format(isv), paramExPCL_LP);
        docExPCL_LP.writeText("Gran Total:\t\t\t"+ df.format(gran_total), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setIsUnderline(true);
        docExPCL_LP.writeText("                                            ", paramExPCL_LP);
        paramExPCL_LP.setIsUnderline(false);
        docExPCL_LP.writeText("                Nombre y Firma", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        printData = docExPCL_LP.getDocumentData();

        try {
            ConnectionBase conn = null;
            conn = Connection_Bluetooth.createClient(bluetooth_mac_address);
            conn.open();
            conn.write(printData);
            Thread.sleep(2000);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
