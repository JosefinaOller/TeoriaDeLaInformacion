package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class SegundaParte {
	private static final int CANTCARACTERES = 10000;
	private static final int CANTSIMBOLOSDIFERENTES = 3;
	private char datos[] = new char[CANTCARACTERES];
	private HashMap<String, Integer> apariciones= new HashMap<String, Integer>();
	private HashMap<String, Double> probabilidades= new HashMap<String, Double>();
	private HashMap<String, Double> informacion= new HashMap<String, Double>();
	private int cantidadCodigos;
	DecimalFormat df = new DecimalFormat("#.####");
	
	
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
			this.informacion.put(i, (Math.log(1.0/this.probabilidades.get(i))/Math.log(2.0)));
			entropia += this.probabilidades.get(i)*this.informacion.get(i);
			System.out.println("Entropia: " +entropia);
		}
		System.out.println("Inforamcion: " + this.informacion.toString());
		System.out.println("Entropia: " +entropia);
	
	}
	
	public void kraft(){
		
		double desigualdad_de_kraft=0;
		
		for (String i : this.apariciones.keySet()) {
			desigualdad_de_kraft +=Math.pow(CANTSIMBOLOSDIFERENTES,-(i.length())); 
		}
		System.out.println("desigualdad de kraft: " + df.format(desigualdad_de_kraft));
		if(desigualdad_de_kraft > 1)
			System.out.println("No cumple la desigualdad de kraft, no es intantaneo");
		else 
			System.out.println("Cumple la desigualdad de kraft");
		
	}
	
	public void longitudMedia() {
		
		double longitud_media=0;
		for (String i : this.probabilidades.keySet()) {
			longitud_media += this.probabilidades.get(i) * i.length();
		}
		System.out.println("longitud media: " + df.format(longitud_media));
	}
	
}
