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
	private char datos[] = new char[CANTCARACTERES];//Manera 1
	private char alfabeto[] = {'A','B','C'};//Manera 1
	private double probabilidades[] = new double[3];//Manera 1
	private double informacion[]= new double[3];
	private double matrizPasaje[][] = new double[3][3];//Manera 1
	private int apariciones[] = {0, 0, 0};//Manera 1
	private int apariciones2[][] = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};//Manera 1
	private HashMap<Character, Double> probabilidades2= new HashMap<Character, Double>();//Manera 2
	
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
			switch (this.datos[i])
			{
			case 'A':
			{
				this.apariciones[0]++;
				break;
			}
			case 'B':
			{
				this.apariciones[1]++;
				break;
			}
			case 'C':
			{
				this.apariciones[2]++;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + this.datos[i]);
			}
		}
		for (int i = 0; i < this.apariciones.length; i++)
		{
			this.probabilidades[i] = (double) this.apariciones[i]/CANTCARACTERES;
			System.out.println(this.alfabeto[i] + " " + this.apariciones[i] + " " + this.probabilidades[i]);
		}
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
				this.matrizPasaje[j][i] = (double) this.apariciones2[j][i]/this.apariciones[i];
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
		for (int i = 0; i < this.probabilidades.length; i++)
		{
			this.informacion[i] =this.probabilidades[i]*( Math.log(1.0/this.probabilidades[i])/Math.log(2.0));
			entropia += this.informacion[i];
		}
		System.out.println("Entropia: " + entropia);
	}
	

	
}
