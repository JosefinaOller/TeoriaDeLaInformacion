package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class PrimeraParte {

	private HashMap<String, Integer> apariciones = new HashMap<String, Integer>();
	private HashMap<String, Double> probabilidades = new HashMap<String, Double>();
	private LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
	private ArrayList<Double> list = new ArrayList<>();
	ArrayList<ElementoShannonFano> datosSF = new ArrayList<ElementoShannonFano>();
	private int total_palabras;
	private HashMap<String, String> codigosHuf = new HashMap<String, String>();
	private HashMap<String, String> codigosSF = new HashMap<String, String>();
	private HashMap<String, Double> informacionHuffman = new HashMap<String, Double>();
	private String[] datos = new String[15000];
	private int largoArchivoOriginal;
	private int largoArchivoHuffman;
	private int largoArchivoShanonFano;
	static DecimalFormat df = new DecimalFormat("#.########");

	public void leeArchivo() {
		File arch = new File("tp2_grupo1.txt");
		Charset.forName("UTF-8").newDecoder();
		try {
			char letra;
			this.total_palabras=0;
			try (BufferedReader lector = new BufferedReader(new InputStreamReader(new FileInputStream(arch), StandardCharsets.UTF_8)))
			{
				String palabra="";
				int i=0;
				while ((letra = (char) lector.read()) != 65535) {
					if(letra != ' '  && letra != '\n') {
						palabra += letra;
						if(letra =='\r')//Caso del enter
							palabra += '\n';
					}else{
						if(letra == '\n' && palabra != "") {
							String espacio= "";
							espacio+=letra;
							this.total_palabras += 1;
							if (this.apariciones.containsKey(espacio)) {
								this.apariciones.put(espacio, this.apariciones.get(espacio) + 1);
							} else {
								this.apariciones.put(espacio, 1);
							}
							datos[i] = espacio;
							i++;
						}
						this.total_palabras += 1;
						if (this.apariciones.containsKey(palabra)) {
							this.apariciones.put(palabra, this.apariciones.get(palabra) + 1);
						} else {
							this.apariciones.put(palabra, 1);
						}
						datos[i] = palabra;
						i++;
						palabra = "";
					}
				}
				this.largoArchivoOriginal = i;
				System.out.println("apariciones: " + this.apariciones.toString());
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error de lectura de archivo");
		}
	}

	public void procesamiento() {

		// System.out.println(sortedMap);

		for (String i : this.apariciones.keySet()) {
			this.probabilidades.put(i, (double) (this.apariciones.get(i)) / this.total_palabras);
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
		// System.out.println("probabilidades: " + this.sortedMap.toString());
	}

	public void huffman() {
		ArrayList<NodoArbol> aux = new ArrayList<NodoArbol>();
		ArrayList<NodoArbol> aux2 = new ArrayList<NodoArbol>();
		// Genero una lista de probabilidades y la ordeno
		List<Entry<String, Double>> list = new ArrayList<>(probabilidades.entrySet());
		list.sort(Entry.comparingByValue());
		for (int i = 0; i < list.size(); i++) {
			aux.add(new NodoArbol(list.get(i).getKey(), list.get(i).getValue(), null, null));
			aux2.add(new NodoArbol(list.get(i).getKey(), list.get(i).getValue(), null, null));
		}
		while (aux2.size() != 1) {
			int i = 0;
			int j = 1;
			double min1 = 1;
			double min2 = 1;
			for (int k = 0; k < aux2.size(); k++) {
				if (aux2.get(k).getProbabilidad() < min1) {
					min2 = min1;
					j = i;
					min1 = aux2.get(k).getProbabilidad();
					i = k;
				} else if (aux2.get(k).getProbabilidad() < min2) {
					min2 = aux2.get(k).getProbabilidad();
					j = k;
				}
			}
			aux2.add(new NodoArbol(aux2.get(i).getClave() + aux2.get(j).getClave(),
					aux2.get(i).getProbabilidad() + aux2.get(j).getProbabilidad(), aux2.get(i), aux2.get(j)));
			if (i < j) {
				aux2.remove(aux2.get(j));
				aux2.remove(aux2.get(i));
			} else {
				aux2.remove(aux2.get(i));
				aux2.remove(aux2.get(j));
			}
		}
		recorrido(aux2.get(0));
		System.out.println("Codigos de cada palabra\n" + this.codigosHuf.toString());

		this.generarArchivoHuffman();
		double entropiaHuffman = entropia(this.codigosHuf);
		double longitudMediaHuffman = longitudMedia(this.codigosHuf);
		double rendimiento = entropiaHuffman / longitudMediaHuffman;
		double tasacompresion = (double) this.largoArchivoOriginal / this.largoArchivoHuffman;

		JOptionPane.showMessageDialog(null,
				String.format("<html><body width='%1s'>Entrop\u00eda: " + df.format(entropiaHuffman) + "<p>Longitud Media: "
						+ df.format(longitudMediaHuffman) + "</p><p>Rendimiento: " + df.format(rendimiento)
						+ "</p><p>Redundancia: " + df.format(1.0 - rendimiento) + "</p><p>Largo archivo original: "
						+ this.largoArchivoOriginal + "</p><p>Largo archivo huffman: " + this.largoArchivoHuffman
						+ "</p><p>Tasa de compresi\u00f3n: " + tasacompresion, 200, 200));

	}

	private void recorrido(NodoArbol arbol) {
		if (!arbol.equals(null)) {
			if (arbol.getIzquierda() != null) {
				if (arbol.getCodigo() == null)
					arbol.getIzquierda().setCodigo("0");
				else
					arbol.getIzquierda().setCodigo(arbol.getCodigo() + "0");
				recorrido(arbol.getIzquierda());
			}
			if (arbol.getDerecha() != null) {
				if (arbol.getCodigo() == null)
					arbol.getDerecha().setCodigo("1");
				else
					arbol.getDerecha().setCodigo(arbol.getCodigo() + "1");
				recorrido(arbol.getDerecha());
			}
			if (arbol.getIzquierda() == null && arbol.getDerecha() == null)
				this.codigosHuf.put(arbol.getClave(), arbol.getCodigo());
		}
	}

	private void generarArchivoHuffman() {
		try {
			this.largoArchivoHuffman = 0;
			FileWriter myWriter = new FileWriter("tp2_grupo1.huf");
			this.codigosHuf.forEach((palabra, codigo) -> {
				try {
					myWriter.write(palabra + ":" + codigo+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			myWriter.write("|-------FIN DICCIONARIO-------\n");
			for (String palabra : this.datos) {
				if (palabra != null) {
					myWriter.write(this.codigosHuf.get(palabra) + " ");
					this.largoArchivoHuffman += (this.codigosHuf.get(palabra).length() + 1);
				}
			}
			myWriter.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private double entropia(HashMap<String, String> codigos) {
		double entropia = 0;
		for (String i : codigos.keySet()) {
			this.informacionHuffman.put(i, (Math.log(1.0 / this.probabilidades.get(i)) / Math.log(2)));
			entropia += this.probabilidades.get(i) * this.informacionHuffman.get(i);
		}
		return entropia;
	}

	private double longitudMedia(HashMap<String, String> codigos) {
		double longitud_media = 0;
		for (String i : this.probabilidades.keySet())
			longitud_media += this.probabilidades.get(i) * codigos.get(i).length();

		return longitud_media;
	}

	private void generarArchivoShannonFano() {
		try {
			this.largoArchivoShanonFano = 0;
			FileWriter myWriter = new FileWriter("tp2_grupo1.fan");
			this.codigosSF.forEach((palabra, codigo) -> {
				try {
					myWriter.write(palabra + ":" + codigo+'\n');
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			myWriter.write("|-------FIN DICCIONARIO-------\n");
			for (String palabra : this.datos) {
				if (palabra != null) {
					myWriter.write(this.codigosSF.get(palabra) + " ");
					this.largoArchivoShanonFano += (this.codigosSF.get(palabra).length() + 1);
				}
			}
			myWriter.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	public void ShannonFano() {

		// ArrayList<ElementoShannonFano> auxSF2 = new ArrayList<ElementoShannonFano>();

		// Genero una lista de probabilidades y la ordeno
		List<Entry<String, Double>> list = new ArrayList<>(probabilidades.entrySet());
		list.sort(Entry.comparingByValue());
		for (int i = 0; i < list.size(); i++) {
			datosSF.add(new ElementoShannonFano(list.get(i).getKey(), list.get(i).getValue()));
		}

		try {
			FileWriter myWriter = new FileWriter("datosShannonFano.txt");
			this.recorrido(datosSF);

			for (ElementoShannonFano dato : datosSF) {

				myWriter.write(dato.toString());
				myWriter.write("\n");

				System.out.println(dato);

				this.codigosSF.put(dato.getClave(), dato.getCodigo());
			}
			myWriter.close();
			generarArchivoShannonFano();

			double entropiaShannonFano = entropia(this.codigosSF);
			double longitudMediaShannonFanon = longitudMedia(this.codigosSF);
			double rendimiento = entropiaShannonFano / longitudMediaShannonFanon;
			double tasacompresion = (double) this.largoArchivoOriginal / this.largoArchivoShanonFano;

			JOptionPane.showMessageDialog(null,
					String.format(
							"<html><body width='%1s'>Datos Shanon-Fano<p>Entrop\u00eda: " + df.format(entropiaShannonFano)
									+ "</p><p>Longitud Media: " + df.format(longitudMediaShannonFanon)
									+ "</p><p>Rendimiento: " + df.format(rendimiento) + "</p><p>Redundancia: "
									+ df.format(1.0 - rendimiento) + "</p><p>Largo archivo original: "
									+ this.largoArchivoOriginal + "</p><p>Largo archivo ShannonFanon: "
									+ this.largoArchivoShanonFano + "</p><p>Tasa de compresi\u00f3n: " + tasacompresion,
							200, 200));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void recorrido(ArrayList<ElementoShannonFano> arraySF) {
		double diff, min = 1.0;

		// System.out.println(arraySF.size());
		// System.out.println(arraySF.size());
		ElementoShannonFano elementSF, elementSF2;
		ArrayList<ElementoShannonFano> conjunto1 = new ArrayList<ElementoShannonFano>();

		ArrayList<ElementoShannonFano> conjunto2 = new ArrayList<ElementoShannonFano>();

		if (arraySF != null && arraySF.size() > 1) {

			if (arraySF.size() == 2) {
				if (arraySF.get(0).getProbabilidad() > arraySF.get(1).getProbabilidad()) {
					anadirCodigo(arraySF.get(0).getClave(), "1");
					anadirCodigo(arraySF.get(1).getClave(), "0");
				} else {
					anadirCodigo(arraySF.get(0).getClave(), "0");
					anadirCodigo(arraySF.get(1).getClave(), "1");
				}
			} else {
				if (arraySF.size() == 3) {
					ArrayList<ElementoShannonFano> auxSF = (ArrayList<ElementoShannonFano>) arraySF.clone();
					ArrayList<ElementoShannonFano> auxSF2 = new ArrayList<ElementoShannonFano>();

					for (int i = 0; i < arraySF.size(); i++) {

						elementSF = (ElementoShannonFano) auxSF.get(i);
						auxSF2.add(auxSF.get(i));
						auxSF.remove(elementSF);

						for (int j = 0; j < auxSF.size(); j++) {

							elementSF2 = (ElementoShannonFano) auxSF.get(j);
							auxSF2.add(elementSF2);
							auxSF.remove(elementSF2);

							diff = sumatoria(auxSF) - sumatoria(auxSF2);
							if (sumatoria(auxSF) - sumatoria(auxSF2) < min) {
								min = diff;
								conjunto1 = (ArrayList<ElementoShannonFano>) auxSF.clone();
								conjunto2 = (ArrayList<ElementoShannonFano>) auxSF2.clone();

							}
							auxSF2.remove(elementSF2);
							auxSF.add(j, elementSF2);

						}
						auxSF2.remove(elementSF);
						auxSF.add(i, elementSF);

					}

				} else {
					ArrayList<ElementoShannonFano> auxSF = (ArrayList<ElementoShannonFano>) arraySF.clone();
					conjunto1 = (ArrayList<ElementoShannonFano>) auxSF.clone();
					for (int i = 0; i < 2; i++) {
						elementSF = conjunto1.get(conjunto1.size() - 1);

						conjunto2.add(elementSF);
						conjunto1.remove(elementSF);
					}

				}
				// System.out.println(conjunto1.size());
				conjunto2.forEach((elemento) -> {
					// System.out.println(elemento);
					anadirCodigo(elemento.getClave(), "1");
				});
				conjunto1.forEach((elemento) -> {
					anadirCodigo(elemento.getClave(), "0");
				});

				recorrido(conjunto2);
				recorrido(conjunto1);

			}
		}
	}

	private void anadirCodigo(String clave, String valor) {

		datosSF.forEach((elemento) -> {
			if (elemento.getClave().equals(clave)) {

				elemento.setCodigo(elemento.getCodigo() + valor);

			}
		});
	}

	private double sumatoria(ArrayList<ElementoShannonFano> arraySF) {
		double sum = 0;
		for (int i = 0; i < arraySF.size(); i++) {
			sum += arraySF.get(i).getProbabilidad();
		}
		return sum;
	}

	public void descomprimirArchivo(String archivoADescomprimir, String nombreArchivoDescompirimido) {
		boolean finDiccionario = false;
		boolean inicioArchivo = false;
		boolean lecturaPalabra = true;
		HashMap<String, String> palabras = new HashMap<String, String>();
		File arch = new File(archivoADescomprimir);

		Charset.forName("UTF-8").newDecoder();
		try {

			char letra;
			this.total_palabras = 0;
			FileWriter myWriter = new FileWriter(nombreArchivoDescompirimido);
			try (BufferedReader lector = new BufferedReader(
					new InputStreamReader(new FileInputStream(arch), StandardCharsets.UTF_8))) {
				String codigo = "";
				String palabra = "";
				int i = 0;
				while ((letra = (char) lector.read()) != 65535) {

					if (!finDiccionario) {

						if (lecturaPalabra) {
							if (letra == '|')
								finDiccionario = true;
							else if (letra == ':')
								lecturaPalabra = false;
							else
								palabra += letra;

						} else {
							if (letra == '\n') {	
								palabras.put(codigo, palabra);
								codigo = "";
								palabra = "";
								lecturaPalabra = true;
							} else {
								codigo += letra;
							}
						}
					} else {
						if (!inicioArchivo && letra == '\n')
							inicioArchivo = true;
					}

					if (inicioArchivo) {
						if (letra == ' ') {
							myWriter.write(palabras.get(codigo)+" ");
							codigo = "";
						} else if(letra!='\n') {
							codigo += letra;
						}
					}
				}
				myWriter.close();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error de lectura de archivo");
		}
	}

}
