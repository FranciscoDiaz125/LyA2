package compilador;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class ColorCeldas extends DefaultTableCellRenderer {
private int columna ;
ArrayList<Integer> erroresSintacticos = new ArrayList<Integer>();
ArrayList<Integer> erroresSemanticos= new ArrayList<Integer>();

public ColorCeldas(int Colpatron)
{
    this.columna = Colpatron;
}

@Override
public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column)
{        
    setBackground(Color.white);
    table.setForeground(Color.black);
    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
    if(erroresSintacticos.contains(table.getValueAt(row, columna)))
    {
        this.setBackground(Color.RED);
        return this;
    }else if(erroresSemanticos.contains(table.getValueAt(row, columna))){
        this.setBackground(Color.BLUE);
        return this;
    }
    else 
        table.setForeground(Color.black);

    return this;
  }
  }