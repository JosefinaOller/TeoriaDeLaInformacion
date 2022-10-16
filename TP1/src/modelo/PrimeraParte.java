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
	private static final int N = 3; /*Cantidad de simbolos*/
	private char datos[] = new char[CANTCARACTERES];
	private char alfabeto[] = {'A','B','C'};
	private double matrizPasaje[][] = new double[N][N];
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
			double diferencia = 0.0;
			for (int i = 0; i < this.apariciones2.length; i++)
			{	
				if (this.matrizPasaje[j][i]>mayor)
					mayor=this.matrizPasaje[j][i];
				if (this.matrizPasaje[j][i]<menor)
					menor=this.matrizPasaje[j][i];
			}
			diferencia = mayor-menor;
			if (diferencia>dMayor)
				dMayor=diferencia;
			if (diferencia<dMenor)
				dMenor=diferencia;
		}
		if(dMayor-dMenor<=0.02)
			JOptionPane.showMessageDialog(null, "Es una fuente de memoria nula");
		else
			JOptionPane.showMessageDialog(null, "Es una fuente de memoria no nula");
	}
	public void entropia() {
		double entropia = 0;
		for (Character i : this.probabilidades.keySet()) {
			this.informacion.put(i, (Math.log(1.0/this.probabilidades.get(i))/Math.log(2.0)));
			entropia += this.probabilidades.get(i)*this.informacion.get(i);
		}
		System.out.println("Informaci\u00f3n: " + this.informacion.toString());
		JOptionPane.showMessageDialog(null, "Entrop\u00eda inicial : " + entropia + "\nEntrop\u00eda de orden 20: " + extension());
	}
	
	public double extension() {
		double probabilidad = 0;
		double entropia = 0;
		double[] Prob = this.probabilidades.values().stream().mapToDouble(a -> a).toArray();
		for(int i1=0; i1<N; i1++) 
		{
			for(int i2=0; i2<N; i2++) 
			{
				for(int i3=0; i3<N; i3++) 
				{
					for(int i4=0; i4<N; i4++) 
					{
						for(int i5=0; i5<N; i5++) 
						{
							for(int i6=0; i6<N; i6++) 
							{
								for(int i7=0; i7<N; i7++) 
								{
									for(int i8=0; i8<N; i8++) 
									{
										for(int i9=0; i9<N; i9++) 
										{
											for(int i10=0; i10<N; i10++) 
											{
												for(int i11=0; i11<N; i11++) 
												{
													for(int i12=0; i12<N; i12++) 
													{
														for(int i13=0; i13<N; i13++) 
														{
															for(int i14=0; i14<N; i14++) 
															{
																for(int i15=0; i15<N; i15++) 
																{
																	for(int i16=0; i16<N; i16++) 
																	{
																		for(int i17=0; i17<N; i17++) 
																		{
																			for(int i18=0; i18<N; i18++) 
																			{
																				for(int i19=0; i19<N; i19++) 
																				{
																					for(int i20=0; i20<N; i20++) 
																					{
																						probabilidad=Prob[i1]*Prob[i2]*Prob[i3]*Prob[i4]*Prob[i5]*Prob[i6]*Prob[i7]*Prob[i8]*Prob[i9]*Prob[i10]*Prob[i11]*Prob[i12]*Prob[i13]*Prob[i14]*Prob[i15]*Prob[i16]*Prob[i17]*Prob[i18]*Prob[i19]*Prob[i20];
																	                    entropia=entropia + probabilidad * (Math.log(1.0/probabilidad)/Math.log(2.0));
																	             
																					} /*i20*/
																				} /*i19*/
																			} /*i18*/
																		} /*i17*/
																	} /*i16*/
																} /*i15*/
															} /*i14*/
														} /*i13*/
													} /*i12*/
												} /*i11*/
											} /*i10*/
										} /*i9*/
									} /*i8*/
								} /*i7*/
							} /*i6*/
						} /*i5*/
					} /*i4*/
				} /*i3*/
			} /*i2*/
		} /*i1*/
		return entropia;
	}
}
