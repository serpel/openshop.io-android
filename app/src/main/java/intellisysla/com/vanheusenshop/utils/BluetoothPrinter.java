package intellisysla.com.vanheusenshop.utils;

/**
 * Created by turupawn on 3/24/17.
 */

import android.content.Context;
import android.content.res.Resources;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.order.OrderItem;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.SplashActivity;

public class BluetoothPrinter {

    public static void print(Context context,
                             String bluetooth_mac_address,
                             String date, Client client, String seller,
                             List<OrderItem> products,
                             double sub_total, double discount, double total_after_discount, double IVA, double total)
    {
        if(client==null || products==null)
        {
            return;
        }

        byte[] printData = {0};

        DocumentExPCL_LP docExPCL_LP = new DocumentExPCL_LP(3);
        ParametersExPCL_LP paramExPCL_LP = new ParametersExPCL_LP();


        paramExPCL_LP.setFontIndex(1);
        paramExPCL_LP.setIsBold(true);
        docExPCL_LP.writeText(context.getString(R.string.CompanyName), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(5);
        paramExPCL_LP.setIsBold(false);
        docExPCL_LP.writeText(context.getString(R.string.CompanyAddress), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.Phone) + ": " + context.getString(R.string.CompanyPhone) + " " + context.getString(R.string.Fax)   + ": " + context.getString(R.string.CompanyFax), paramExPCL_LP);

        paramExPCL_LP.setFontIndex(3);
        docExPCL_LP.writeText(context.getString(R.string.RTN) + ": " + context.getString(R.string.CompanyRTN), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(2);
        docExPCL_LP.writeText(context.getString(R.string.SalesOrder), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setFontIndex(5);
        docExPCL_LP.writeText(context.getString(R.string.Date) + ": " + date, paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.ClientCode) + ": " + client.getCardCode(), paramExPCL_LP);
        docExPCL_LP.writeText(client.getName(), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.Address) + ": ", paramExPCL_LP);
        docExPCL_LP.writeText(client.getAddress(), paramExPCL_LP);

        docExPCL_LP.writeText(context.getString(R.string.Phone) + ": " + client.getPhone(), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.RTN) + ": " + client.getRTN(), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.Seller) + ": " + seller, paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        docExPCL_LP.writeText(context.getString(R.string.Product) + "\n" +
                                                    context.getString(R.string.Amount) + "\t" +
                                                    context.getString(R.string.Price) + "\t" +
                                                    context.getString(R.string.Discount) + "\t" +
                                                    context.getString(R.string.Total), paramExPCL_LP);


        DecimalFormat df = new DecimalFormat("0.00");
        if(products!=null) {
            for (int i = 0; i < products.size(); i++) {
                int product_quantity = products.get(i).getQuantity();
                double product_price = products.get(i).getPrice();
                double product_discount = products.get(i).getDiscount();

                docExPCL_LP.writeText(products.get(i).getCode() +
                        '\n' + product_quantity +
                        '\t' + df.format(product_price) +
                        '\t' + df.format(product_discount) +
                        '\t' + df.format(product_quantity * (product_price - product_discount)));
            }
        }

        paramExPCL_LP.setIsBold(false);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.SubTotal) + ": \t\t\t"+df.format(sub_total), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.Discount) + ": \t\t\t"+ df.format(discount), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.TotalAfterDiscount) + ": \t"+ df.format(total_after_discount), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.IVA) + ": \t\t\t"+ df.format(IVA), paramExPCL_LP);
        docExPCL_LP.writeText(context.getString(R.string.Total) + ": \t\t\t"+ df.format(total), paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);
        docExPCL_LP.writeText("", paramExPCL_LP);

        paramExPCL_LP.setIsUnderline(true);
        docExPCL_LP.writeText("                                            ", paramExPCL_LP);
        paramExPCL_LP.setIsUnderline(false);
        docExPCL_LP.writeText("                "+ context.getString(R.string.NameAndSignature), paramExPCL_LP);
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
