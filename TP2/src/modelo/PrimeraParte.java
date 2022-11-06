package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import modelo.NodoArbol;

public class PrimeraParte {
	
	private HashMap<String, Integer> apariciones= new HashMap<String, Integer>();
	private HashMap<String, Double> probabilidades= new HashMap<String, Double>();
    private LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
    private ArrayList<Double> list = new ArrayList<>();
    ArrayList<ElementoShannonFano> datosSF = new ArrayList<ElementoShannonFano>();
	private int total_palabras;
	private HashMap<String, String> codigos= new HashMap<String, String>();
	private String[] datos =new String[15000];
	
	public void leeArchivo()
	{
		try
		{
			FileReader arch = new FileReader("tp2_grupo1.txt");
			try
			{
				if (arch.ready())
				{
					char letra;
					this.total_palabras=0;
					try (BufferedReader lector = new BufferedReader(arch))
					{
						String palabra="";
						int i=0;
						while ((letra = (char) lector.read()) != 65535) {
							if(letra != ' ' && letra !='\n'  ) {
								if(letra!=',' && letra !='!' && letra!='.' && letra !='?' && letra!='¡' && letra !='¿' && letra !='('&& letra !=')' && letra !=':' )
									palabra += letra;
							}else{
								this.total_palabras += 1;
								if(this.apariciones.containsKey(palabra)){
									this.apariciones.put(palabra, this.apariciones.get(palabra)+1);
								}else {
									this.apariciones.put(palabra, 1);
								}
								datos[i]=palabra;	
								i++;
								palabra="";
							}
						}
						//System.out.println("apariciones: " + this.apariciones.toString());
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
	
	public void procesamiento() {
  
       // System.out.println(sortedMap);
		
		
		for (String i : this.apariciones.keySet()) {
			this.probabilidades.put(i,   (double) (this.apariciones.get(i))/this.total_palabras);
		}
		
	      for (Entry<String, Double> entry : probabilidades.entrySet()) {
	            list.add(entry.getValue());
	        }
	        Collections.sort(list); 
	        for (Double num : list) {
	            for (Entry<String, Double> entry : probabilidades.entrySet()) {
	                if (entry.getValue().equals(num)) {
	                    sortedMap.put(entry.getKey(), num);
	                }
	            }
	        }
		//System.out.println("probabilidades: " + this.sortedMap.toString());
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
			int i = 0;
			int j = 1;
			double min1=1;
			double min2=1;
			for (int k = 0; k < aux2.size(); k++)
			{
				if (aux2.get(k).getProbabilidad()<min1)
				{
					min2 = min1;
					j = i;
					min1 = aux2.get(k).getProbabilidad();
					i = k;
				} else if (aux2.get(k).getProbabilidad()<min2) {
					min2 = aux2.get(k).getProbabilidad();
					j = k;
				}
			}
			aux2.add(new NodoArbol(aux2.get(i).getClave()+aux2.get(j).getClave(), aux2.get(i).getProbabilidad()+aux2.get(j).getProbabilidad(), aux2.get(i), aux2.get(j)));
			if (i < j)
			{
				aux2.remove(aux2.get(j));
				aux2.remove(aux2.get(i));
			} else
			{
				aux2.remove(aux2.get(i));
				aux2.remove(aux2.get(j));
			}
		}
		recorrido(aux2.get(0));
		System.out.println("Codigos de cada palabra\n" + this.codigos.toString());
		this.generarArchivoHuffman();
		
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
	
	private void generarArchivoHuffman(){
		  try {
		      FileWriter myWriter = new FileWriter("tp2_grupo1.huf");
		      for(String palabra:this.datos) {
		      	if(palabra != null)
		    	  myWriter.write(this.codigos.get(palabra)+ " ");
		      }
		      myWriter.close();
		      
		    } catch (IOException e) {
		    	
		      e.printStackTrace();
		    }
		
	}

	
	public void ShannonFano() {
		
		//ArrayList<ElementoShannonFano> auxSF2 = new ArrayList<ElementoShannonFano>();
		
		//Genero una lista de probabilidades y la ordeno
		List <Entry<String,Double>> list = new ArrayList<>(probabilidades.entrySet());
		list.sort(Entry.comparingByValue());
		for(int i=0;i<list.size();i++) {
			//datosSF.add(new ElementoShannonFano(list.get(i).getKey(),list.get(i).getValue()));
		 //auxSF2.add(new ElementoShannonFano(list.get(i).getKey(),list.get(i).getValue()));
		}
		/* Datos de prueba como los de la clase 6*/
		datosSF.add(new ElementoShannonFano("s1",0.4));
		datosSF.add(new ElementoShannonFano("s2",0.2));
		datosSF.add(new ElementoShannonFano("s3",0.15));
		datosSF.add(new ElementoShannonFano("s4",0.1));
		datosSF.add(new ElementoShannonFano("s5",0.06));
		datosSF.add(new ElementoShannonFano("s6",0.04));
		datosSF.add(new ElementoShannonFano("s7",0.03));
		datosSF.add(new ElementoShannonFano("s8",0.02));
		
		this.recorrido(datosSF);
		System.out.println(datosSF);
		
	}
	private void recorrido(ArrayList<ElementoShannonFano> arraySF) {
		double diff,min=1.0;
		
		//System.out.println(arraySF.size());
		//System.out.println(arraySF.size());
		ElementoShannonFano elementSF,elementSF2;
		ArrayList<ElementoShannonFano> conjunto1=new ArrayList<ElementoShannonFano>();;
		ArrayList<ElementoShannonFano> conjunto2 = new ArrayList<ElementoShannonFano>();;
		if(arraySF!=null && arraySF.size()>1) {
		if(arraySF.size()==2) {
			if(arraySF.get(0).getProbabilidad()>arraySF.get(1).getProbabilidad()) {
				añadirCodigo(arraySF.get(0).getClave(),1);
				añadirCodigo(arraySF.get(1).getClave(),0);
			}else {
				añadirCodigo(arraySF.get(0).getClave(),0);
				añadirCodigo(arraySF.get(1).getClave(),1);
			}
		}else {
			ArrayList<ElementoShannonFano> auxSF=(ArrayList<ElementoShannonFano>) arraySF.clone();
			ArrayList<ElementoShannonFano> auxSF2 = new ArrayList<ElementoShannonFano>();
			
			for(int i=0;i<arraySF.size();i++) {
				
				elementSF=(ElementoShannonFano) auxSF.get(i);
				auxSF2.add(auxSF.get(i));
				auxSF.remove(elementSF);
				
				for(int j=0;j<auxSF.size();j++) {
					
					elementSF2=(ElementoShannonFano) auxSF.get(j);
					auxSF2.add(elementSF2);
					auxSF.remove(elementSF2);
					
					diff =sumatoria(auxSF)-sumatoria(auxSF2);
					if(sumatoria(auxSF)-sumatoria(auxSF2)<min) {
						min=diff;
						conjunto1=(ArrayList<ElementoShannonFano>) auxSF.clone();
						conjunto2=(ArrayList<ElementoShannonFano>) auxSF2.clone();
					

					}
					auxSF2.remove(elementSF2);
					auxSF.add(j,elementSF2);

				}
				auxSF2.remove(elementSF);
				auxSF.add(i,elementSF);
			
			}

			conjunto2.forEach((elemento)->{
				añadirCodigo(elemento.getClave(),1);
			}
			);
			conjunto1.forEach((elemento)->{
				añadirCodigo(elemento.getClave(),0);
			}
			);
		
			recorrido(conjunto1);
			recorrido(conjunto2);
		}
		}
	}
	
	private void añadirCodigo(String clave,int valor) {

		datosSF.forEach((elemento)->{
			if(elemento.getClave().equals(clave)) {
				elemento.setCodigo(elemento.getCodigo()+valor);
			}
		});
	}
	
	private double sumatoria(ArrayList<ElementoShannonFano> arraySF) {
		double sum=0;
		for(int i=0;i<arraySF.size();i++) {	
			sum+=arraySF.get(i).getProbabilidad();
		}
		return sum;
	}
	
	
}
