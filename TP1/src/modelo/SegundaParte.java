package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class SegundaParte {
	private static final int CANTCARACTERES = 10000;
	private char datos[] = new char[CANTCARACTERES];
	private HashMap<String, Integer> apariciones= new HashMap<String, Integer>();
	private HashMap<String, Double> probabilidades= new HashMap<String, Double>();
	private HashMap<String, Double> informacion= new HashMap<String, Double>();
	private int cantidadCodigos;
	
	public void leeArchivo()
	{
		try
		{
			FileReader arch = new FileReader("tp1_grupo1.txt");
			try
			{
				if (arch.ready())
				{
					try (BufferedReader lector = new BufferedReader(arch))
					{
						for (int i = 0; i < CANTCARACTERES; i++)
							this.datos[i] = (char) lector.read();
					}
				}
			} catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, "Error de lectura de archivo");
			}
			
		} catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Error de apertura de archivo");
		}
	}
	
	public void procesamiento(int cantCaracteresCodigo){
		this.cantidadCodigos=(int) (CANTCARACTERES/cantCaracteresCodigo);//el casteo int hace redondeo asi abajo
		for (int i = 0; i < CANTCARACTERES; i+=cantCaracteresCodigo) {
			String palabra="";

			for(int j=i;j<(i+cantCaracteresCodigo);j++) {
				if(j<CANTCARACTERES)
					palabra = palabra + this.datos[j];
				
			}
			if(palabra.length()==cantCaracteresCodigo) {
				if(this.apariciones.containsKey(palabra)){
					this.apariciones.put(palabra, this.apariciones.get(palabra)+1);
				}else {
					this.apariciones.put(palabra, 1);
				}
			}
		}
		System.out.println("apariciones: " + this.apariciones.toString());
		
		for (String i : this.apariciones.keySet()) {
			this.probabilidades.put(i,   (double) (this.apariciones.get(i))/this.cantidadCodigos);
		}
		
		System.out.println("probabilidades: " + this.probabilidades.toString());
		System.out.println("cantidadCodigos: "+ this.cantidadCodigos);
		
	}
	
	public void entropia() {
		double entropia = 0;
		for (String i : this.probabilidades.keySet()) {
			this.informacion.put(i, this.probabilidades.get(i)*(Math.log(1.0/this.probabilidades.get(i))/Math.log(2.0)));
			entropia += this.informacion.get(i);
		}
		System.out.println("Inforamcion: " + this.informacion.toString());
		System.out.println("Entropia: " + entropia);
		
	}
	
	
}
