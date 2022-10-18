package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class SegundaParte {
	private static final int CANTCARACTERES = 1000;
	private static final int CANTSIMBOLOSDIFERENTES = 3;
	private char datos[] = new char[CANTCARACTERES];
	private int cantCaracteresCodigo;
	private HashMap<String, Integer> apariciones= new HashMap<String, Integer>();
	private HashMap<String, Double> probabilidades= new HashMap<String, Double>();
	private HashMap<String, Double> informacion= new HashMap<String, Double>();
	private HashMap<String, String> codigos= new HashMap<String, String>();
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
	
	public void procesamiento(int cant){
		this.cantCaracteresCodigo = cant;
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
			this.informacion.put(i, (Math.log(1.0/this.probabilidades.get(i))/Math.log(CANTSIMBOLOSDIFERENTES)));
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
			return df.format(kraft)+"/Cumple la desigualdad de kraft, por lo tanto es instant\u00e1neo.";
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
		ArrayList<NodoArbol> aux = new ArrayList<NodoArbol>();
		ArrayList<NodoArbol> aux2 = new ArrayList<NodoArbol>();
		//Genero una lista de probabilidades y la ordeno
		List <Entry<String,Double>> list = new ArrayList<>(probabilidades.entrySet());
		list.sort(Entry.comparingByValue());
		for(int i=0;i<list.size();i++) {
			aux.add(new NodoArbol(list.get(i).getKey(),list.get(i).getValue(),null,null));
			aux2.add(new NodoArbol(list.get(i).getKey(),list.get(i).getValue(),null,null));
		}
		
		while (aux2.size()!=1)
		{
			int l=0;
			int i=aux2.size()-2;
			int j=aux2.size()-1;
			while (i>=0 && j>=0 && aux2.get(i).getProbabilidad()<aux2.get(j).getProbabilidad())
			{
				i--;
				j--;
				l++;
			}
			if (i<0)
			{
				i = 0;
				j = 1;
				while (i>=aux2.size() && j>=aux2.size() && aux2.get(i).getProbabilidad()<aux2.get(j).getProbabilidad())
				{
					i++;
					j++;
					l--;
				}
			}
			aux2.add(new NodoArbol(aux2.get(i).getClave()+aux2.get(j).getClave(), aux2.get(i).getProbabilidad()+aux2.get(j).getProbabilidad(), aux2.get(i), aux2.get(j)));
			for (int k = 0; k < 2; k++)
				if (2+l > aux2.size())
					aux2.remove(aux2.get(0));
				else
					aux2.remove(aux2.size()-(2+l));
		}
		recorrido(aux2.get(0));
		System.out.println("Codigos de cada simbolo\n" + this.codigos.toString());
		System.out.println("La codificacion Huffman es:" + codificacion(cantCaracteresCodigo));
		
    }
	private void recorrido(NodoArbol arbol){
		if (!arbol.equals(null)){
			if (arbol.getIzquierda()!=null)
			{
				if (arbol.getCodigo()==null)
					arbol.getIzquierda().setCodigo("0");
				else
					arbol.getIzquierda().setCodigo(arbol.getCodigo()+"0");
				recorrido(arbol.getIzquierda());
			}if (arbol.getDerecha()!=null)
			{
				if (arbol.getCodigo()==null)
					arbol.getDerecha().setCodigo("1");
				else
					arbol.getDerecha().setCodigo(arbol.getCodigo()+"1");
				recorrido(arbol.getDerecha());
			}
			if (arbol.getIzquierda()==null && arbol.getDerecha()==null)
				this.codigos.put(arbol.getClave(), arbol.getCodigo());
		}
    }
	
	private String codificacion(int cant){
		String codificacion = "";
		int i=0;
		this.cantidadCodigos=(int) (CANTCARACTERES/cant);//el casteo int hace redondeo asi abajo
		while (i<this.cantidadCodigos) {
			String aux="";
			for(int j=0;j<cant;j++) {
				aux += this.datos[i];
				i++;
			}
			codificacion += this.codigos.get(aux);
			
		}
		generaArchivoBinario("Codificacion"+cantCaracteresCodigo+"Caracteres.bin",codificacion);
		return codificacion;
		
    }
	
	public void generaArchivoBinario(String nombreArchivo,String codificacion)
	{
		 ObjectOutputStream salida = null;
		try {
			salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     try {
			salida.writeObject(codificacion);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     try {
			salida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//No me anduvo,BORRAR
		/*File arch = new File(nombreArchivo);
		try
		{
			if (arch.canWrite())
			{
				try (FileOutputStream salida = new FileOutputStream(arch))
				{
					ObjectOutputStream escribe = new ObjectOutputStream(salida);
					escribe.writeObject(codificacion);
					escribe.close();
				}
			}
		} catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error de escritura de archivo");
		}*/ 
	}
}
