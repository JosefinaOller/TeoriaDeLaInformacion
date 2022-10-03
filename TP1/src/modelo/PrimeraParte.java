package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import excepciones.SumaException;

public class PrimeraParte
{
	private static final int CANTCARACTERES = 10000;
	private char datos[] = new char[CANTCARACTERES];
	private char alfabeto[] = {'A','B','C'};
	private double matrizPasaje[][] = new double[3][3];
	private int apariciones2[][] = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
	private HashMap<Character, Integer> apariciones= new HashMap<Character, Integer>();
	private HashMap<Character, Double> probabilidades= new HashMap<Character, Double>();
	private HashMap<Character, Double> informacion= new HashMap<Character, Double>();
	
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
	public void procesamiento()
	{
		for (int i = 0; i < CANTCARACTERES; i++)
		{
			if(this.apariciones.containsKey(this.datos[i])){
				this.apariciones.put(this.datos[i], this.apariciones.get(this.datos[i])+1);
			}else {
				this.apariciones.put(this.datos[i], 1);
			}
		}
		System.out.println("apariciones: " + this.apariciones.toString());
		for (char i : this.apariciones.keySet())
			this.probabilidades.put(i, (double) this.apariciones.get(i)/CANTCARACTERES);
		System.out.println(this.probabilidades.toString());
	}
	public void generaMatriz()
	{
		for (int i = 1; i < CANTCARACTERES; i++)
		{
			switch (this.datos[i])
			{
			case 'A':
			{
				switch (this.datos[i-1])
				{
				case 'A':
				{
					this.apariciones2[0][0]++;
					break;
				}
				case 'B':
				{
					this.apariciones2[0][1]++;
					break;
				}
				case 'C':
				{
					this.apariciones2[0][2]++;
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + this.datos[i-1]);
				}
				break;
			}
			case 'B':
			{
				switch (this.datos[i-1])
				{
				case 'A':
				{
					this.apariciones2[1][0]++;
					break;
				}
				case 'B':
				{
					this.apariciones2[1][1]++;
					break;
				}
				case 'C':
				{
					this.apariciones2[1][2]++;
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + this.datos[i-1]);
				}
				break;
			}
			case 'C':
			{
				switch (this.datos[i-1])
				{
				case 'A':
				{
					this.apariciones2[2][0]++;
					break;
				}
				case 'B':
				{
					this.apariciones2[2][1]++;
					break;
				}
				case 'C':
				{
					this.apariciones2[2][2]++;
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + this.datos[i-1]);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + this.datos[i]);
			}
		}
	}
	public void procesamientoMatriz() throws SumaException
	{
		for (int i = 0; i < this.apariciones2.length; i++)
		{
			double columnas = 0;
			for (int j = 0; j < this.apariciones2.length; j++)
			{	
				this.matrizPasaje[j][i] = (double) this.apariciones2[j][i]/this.apariciones.get(alfabeto[i]);
				System.out.print(this.matrizPasaje[j][i]+" ");
				columnas+= this.matrizPasaje[j][i];
			}
			System.out.println("| "+columnas);
			if(columnas<0.9)
				throw new SumaException("Suma menor a 1");
			if(columnas>1.0)
				throw new SumaException("Suma mayor a 1");
		}
	}
	public void memoriaNula()
	{
		double dMayor = 0.0;
		double dMenor = 1.0;
		for (int j = 0; j < this.apariciones2.length; j++)
		{
			double mayor = 0.0;
			double menor = 1.0;
			double diferencja = 0.0;
			for (int i = 0; i < this.apariciones2.length; i++)
			{	
				if (this.matrizPasaje[j][i]>mayor)
					mayor=this.matrizPasaje[j][i];
				if (this.matrizPasaje[j][i]<menor)
					menor=this.matrizPasaje[j][i];
			}
			diferencja = mayor-menor;
			if (diferencja>dMayor)
				dMayor=diferencja;
			if (diferencja<dMenor)
				dMenor=diferencja;
		}
		if(dMayor-dMenor<=0.02)
			System.out.println("Es una fuente de memoria nula");
		else
			System.out.println("Es una fuente de memoria no nula");
	}
	public void entropia() {
		double entropia = 0;
		for (Character i : this.probabilidades.keySet()) {
			this.informacion.put(i, (Math.log(1.0/this.probabilidades.get(i))/Math.log(2.0)));
			entropia += this.probabilidades.get(i)*this.informacion.get(i);
		}
		System.out.println("Inforamci\u00f3n: " + this.informacion.toString());
		System.out.println("Entrop\u00eda: " + entropia);
	}
}
