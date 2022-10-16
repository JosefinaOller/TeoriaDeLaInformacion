package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
	
	public double entropia() {
		double entropia = 0;
		for (String i : this.probabilidades.keySet()) {
			this.informacion.put(i, (Math.log(1.0/this.probabilidades.get(i))/Math.log(2.0)));
			entropia += this.probabilidades.get(i)*this.informacion.get(i);
		}
		System.out.println("Inforamci\u00f3n: " + this.informacion.toString());
		System.out.println("Entrop\u00eda: " +entropia);
		return entropia;
	
	}
	
	public double kraft(){
		double desigualdad_de_kraft=0;
		for (String i : this.apariciones.keySet())
			desigualdad_de_kraft +=Math.pow(CANTSIMBOLOSDIFERENTES,-(i.length()));
		System.out.println("Desigualdad de kraft: " + df.format(desigualdad_de_kraft));
		return desigualdad_de_kraft;
	}
	
	public String mcMillan(){
		double kraft = kraft();
		if(kraft <= 1)
			return df.format(kraft)+"/Cumple la desigualdad de kraft, ergo es instant\u00e1neo.";
		else 
			return df.format(kraft)+"/No cumple la desigualdad de kraft, no es instant\u00e1neo.";
	}
	
	public double longitudMedia() {
		double longitud_media=0;
		for (String i : this.probabilidades.keySet())
			longitud_media += this.probabilidades.get(i) * i.length();
		System.out.println("Longitud media: " + df.format(longitud_media));
		return longitud_media;
	}
	public HashMap<String,Integer> orderMap(HashMap<String,Integer> map){
        LinkedHashMap<String,Integer> descendingMap = new LinkedHashMap<>();
           map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
           .forEachOrdered(x -> descendingMap.put(x.getKey(), x.getValue()));
        return descendingMap;
    }

	public HashMap<String,Double> orderMap2(HashMap<String,Double> map){
        LinkedHashMap<String,Double> descendingMap = new LinkedHashMap<>();
           map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
           .forEachOrdered(x -> descendingMap.put(x.getKey(), x.getValue()));
        return descendingMap;
    }
	
	public void ordenacion(){
		this.apariciones = orderMap(this.apariciones);
		this.informacion = orderMap2(this.informacion);
		this.probabilidades = orderMap2(this.probabilidades);
        System.out.println(this.apariciones.toString());
        System.out.println(this.informacion.toString());
        System.out.println(this.probabilidades.toString());
    }
	public void huffman(){
		  HashMap<String, Double> aux = new HashMap<String, Double>();
    }
	public void generaArchivoBinario(String nombreArchivo)
	{
		File arch = new File(nombreArchivo);
		try
		{
			if (arch.canWrite())
			{
				try (FileOutputStream salida = new FileOutputStream(arch))
				{
					ObjectOutputStream escribe = new ObjectOutputStream(salida);
					//escribe.write();
				}
			}
		} catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error de escritura de archivo");
		}
	}
}
