package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class PrimeraParte {

    private HashMap<String, Integer> apariciones = new HashMap<String, Integer>();
    private HashMap<String, Double> probabilidades = new HashMap<String, Double>();
    private LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
    private ArrayList<Double> list = new ArrayList<>();
    private HashMap<String, ElementoShannonFano> datosSF = new HashMap<String, ElementoShannonFano>();
    private int total_palabras;
    private HashMap<String, String> codigosHuf = new HashMap<String, String>();
    private HashMap<String, String> codigosSF = new HashMap<String, String>();
    private HashMap<String, Double> informacionHuffman = new HashMap<String, Double>();
    private String[] datos = new String[17000];
    private long largoArchivoOriginal;
    private long largoArchivoHuffman;
    private long largoArchivoShanonFano;
    static DecimalFormat df = new DecimalFormat("#.########");

    public void leeArchivo() {
    	Path path = Paths.get("tp2_grupo1.txt"); //para el largo original del archivo
        File arch = new File("tp2_grupo1.txt");
        Charset.forName("UTF-8").newDecoder();
        try {
            char letra;
            this.total_palabras = 0;
            this.largoArchivoOriginal=Files.size(path);
            try (BufferedReader lector =
                 new BufferedReader(new InputStreamReader(new FileInputStream(arch), StandardCharsets.UTF_8))) {
                String palabra = "";
                int i = 0;
                while ((letra = (char) lector.read()) != 65535) {
                    if (letra != ' ' && letra != '\n' && letra != '\r') {
                        palabra += letra;

                    } else {
                        if (palabra != "") {
                            this.total_palabras += 1;
                            if (this.apariciones.containsKey(palabra)) {
                                this.apariciones.put(palabra, this.apariciones.get(palabra) + 1);
                            } else {
                                this.apariciones.put(palabra, 1);
                            }
                            datos[i] = palabra;
                            i++;
                        }
                        palabra = "";
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error de lectura de archivo");
        }
    }

    public void procesamiento() {

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
    }

    public void huffman() {
        ArrayList<NodoArbol> aux = new ArrayList<NodoArbol>();
        ArrayList<NodoArbol> aux2 = new ArrayList<NodoArbol>();
        // Genero una lista de probabilidades y la ordeno, esta mal, ordena de forma ascendente, tiene que ser descendente
        
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
                                   aux2.get(i).getProbabilidad() + aux2.get(j).getProbabilidad(), aux2.get(i),
                                   aux2.get(j)));
            if (i < j) {
                aux2.remove(aux2.get(j));
                aux2.remove(aux2.get(i));
            } else {
                aux2.remove(aux2.get(i));
                aux2.remove(aux2.get(j));
            }
        }
        recorrido(aux2.get(0));

        this.codificacionHuffman();
        this.decodificacion("huf");
        double entropiaHuffman = entropia(this.codigosHuf);
        double longitudMediaHuffman = longitudMedia(this.codigosHuf);
        double rendimiento = entropiaHuffman / longitudMediaHuffman;
        double tasacompresion = (double) this.largoArchivoOriginal / this.largoArchivoHuffman;

        JOptionPane.showMessageDialog(null,
                                      String.format("<html><body width='%1s'>Entrop\u00eda: " +
                                                    df.format(entropiaHuffman) + "<p>Longitud Media: " +
                                                    df.format(longitudMediaHuffman) + "</p><p>Rendimiento: " +
                                                    df.format(rendimiento) + "</p><p>Redundancia: " +
                                                    df.format(1.0 - rendimiento) + "</p><p>Largo archivo original: " +
                                                    this.largoArchivoOriginal + "</p><p>Largo archivo huffman: " +
                                                    this.largoArchivoHuffman + "</p><p>Tasa de compresi\u00f3n: " +
                                                    tasacompresion, 200, 200));

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


    public void codificacionHuffman() {
        try (InputStream in = new FileInputStream("tp2_grupo1.txt");
             Reader reader = new InputStreamReader(in)) {

            File archivo = new File("hufCodificado.huf");
            FileOutputStream archivo2 = new FileOutputStream(archivo);
            ObjectOutputStream escribir = new ObjectOutputStream(archivo2);
            escribir.writeObject(this.codigosHuf);

            ArrayList<Boolean> valoresEnBits = new ArrayList<Boolean>();
            System.out.println("\nf" + this.datos);
            for (String palabra : this.datos) {

                String codigohuf = this.codigosHuf.get(palabra);
                if (codigohuf != null) {
                    System.out.println(palabra);
                    System.out.println("l" + codigohuf);
                    for (char c : codigohuf.toCharArray()) {
                        if (c == '0') {
                            valoresEnBits.add(false);
                        } else {
                            valoresEnBits.add(true);
                        }
                    }
                    palabra = "";
                }

            }

            byte bytee = 0;
            int contador = 0;
            Iterator valoresIterator = valoresEnBits.iterator();
            while (valoresIterator.hasNext()) {
                boolean valor = (Boolean) valoresIterator.next();
              
                if (valor) {
                    bytee = (byte) (bytee | (1 << 7 - contador));
                }
                contador++;
                
                if (contador == 8) {
                    archivo2.write(bytee);
                    this.largoArchivoHuffman += 1; //no se si es correcto
                    contador = 0;
                    bytee = 0;
                }
            }
            if (contador < 0) 
            {
                archivo2.write(bytee);
            }
            escribir.close();
            archivo2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void codificacionShannonFano() {
        try (InputStream in = new FileInputStream("tp2_grupo1.txt");
             Reader reader = new InputStreamReader(in)) {

            File archivo = new File("sfCodificado.fan");
            FileOutputStream archivo2 = new FileOutputStream(archivo);
            ObjectOutputStream escribir = new ObjectOutputStream(archivo2);
            escribir.writeObject(this.codigosSF);

            ArrayList<Boolean> valoresEnBits = new ArrayList<Boolean>();
            System.out.println("\nf" + this.datos);
            for (String palabra : this.datos) {

                String codigosf = this.codigosSF.get(palabra);
                if (codigosf != null) {
                    System.out.println(palabra);
                    System.out.println("l" + codigosf);
                    for (char c : codigosf.toCharArray()) {
                        if (c == '0') {
                            valoresEnBits.add(false);
                        } else {
                            valoresEnBits.add(true);
                        }
                    }
                    palabra = "";
                }

            }

            byte bytee = 0;
            int contador = 0;
            Iterator valoresIterator = valoresEnBits.iterator();
            while (valoresIterator.hasNext()) {
                boolean valor = (Boolean) valoresIterator.next();
              
                if (valor) {
                    bytee = (byte) (bytee | (1 << 7 - contador));
                }
                contador++;
                
                if (contador == 8) {
                    archivo2.write(bytee);
                    this.largoArchivoShanonFano +=1; //no se si es correcto
                    contador = 0;
                    bytee = 0;
                }
            }
            if (contador < 0) 
            {
                archivo2.write(bytee);
            }
            escribir.close();
            archivo2.close();

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
    
    public void ShannonFano() {
        // Genero una lista de probabilidades y la ordeno
        List<Entry<String, Double>> list = new ArrayList<>(probabilidades.entrySet());
        list.sort(Entry.comparingByValue());
        
        ArrayList<Double> prob = new ArrayList<Double>();
        
        for (int i = 0; i < list.size(); i++) {
        	datosSF.put(list.get(i).getKey(), new ElementoShannonFano(list.get(i).getKey(), list.get(i).getValue()));
            prob.add(list.get(i).getValue());
        } 
        try {
            FileWriter myWriter = new FileWriter("datosShannonFano.txt");
            Collection<ElementoShannonFano> values = datosSF.values();

            // Creating an ArrayList of values
            ArrayList<ElementoShannonFano> arraySF = new ArrayList<>(values);
            
            //Genera codigos de Shannon-Fano
            //this.proceso(arraySF, prob,prob.size(),0);
            this.proceso(arraySF,"");
            
            for (ElementoShannonFano dato : this.datosSF.values()) {

                myWriter.write(dato.toString());
                myWriter.write("\n");

                System.out.println(dato);

                this.codigosSF.put(dato.getClave(), dato.getCodigo());
            }
            myWriter.close();
            
            this.codificacionShannonFano();
            this.decodificacion("fan");

            double entropiaShannonFano = entropia(this.codigosSF);
            double longitudMediaShannonFanon = longitudMedia(this.codigosSF);
            double rendimiento = entropiaShannonFano / longitudMediaShannonFanon;
            double tasacompresion = (double) this.largoArchivoOriginal / this.largoArchivoShanonFano;

            JOptionPane.showMessageDialog(null,
                                          String.format("<html><body width='%1s'>Datos Shanon-Fano<p>Entrop\u00eda: " +
                                                        df.format(entropiaShannonFano) + "</p><p>Longitud Media: " +
                                                        df.format(longitudMediaShannonFanon) + "</p><p>Rendimiento: " +
                                                        df.format(rendimiento) + "</p><p>Redundancia: " +
                                                        df.format(1.0 - rendimiento) +
                                                        "</p><p>Largo archivo original: " + this.largoArchivoOriginal +
                                                        "</p><p>Largo archivo ShannonFanon: " +
                                                        this.largoArchivoShanonFano +
                                                        "</p><p>Tasa de compresi\u00f3n: " + tasacompresion, 200, 200));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

     private void proceso(ArrayList<ElementoShannonFano> arrayEntrada,String codigo) {
        if (arrayEntrada.size() > 2){
            ArrayList<ElementoShannonFano> arrayIzq = new ArrayList<ElementoShannonFano>();
            ArrayList<ElementoShannonFano> arrayDer = new ArrayList<ElementoShannonFano>();
            double probabilidadTotal=0.0f,probabilidadIzq=0.0f;

            int i=0;
            while(i<arrayEntrada.size()){
                probabilidadTotal += arrayEntrada.get(i).getProbabilidad();
                i++;
            }
            i=0;
            boolean ladoIzquierdo=true;
            while(i<arrayEntrada.size() &&  ladoIzquierdo){
                
                Double probabilidad = arrayEntrada.get(i).getProbabilidad();
                if (probabilidadIzq + probabilidad < (float)probabilidadTotal / 2) 
                {
                    arrayIzq.add(arrayEntrada.get(i));
                    probabilidadIzq += probabilidad;
                }
                //al meter la entrada en el grupo menor este tendría mas del 50% de probabilidad
                else 
                {
                    //Si al agregar la ultima entrada al grupo menor quedaria mas cerca de 50/50, agregarla. Caso contrario, se corta y pasa el resto al grupo derecho        
                    if (Math.abs(probabilidadIzq + probabilidad - (float)probabilidadTotal / 2) < Math.abs(probabilidadIzq - (float)probabilidadTotal / 2)) 
                    {
                       arrayIzq.add(arrayEntrada.get(i));
                    }
                    else 
                    {
                        arrayDer.add(arrayEntrada.get(i));
                    }
                    ladoIzquierdo=false;
                }
                i++;
            }
            while(i<arrayEntrada.size()){
                arrayDer.add(arrayEntrada.get(i));
                i++;
            }
            proceso(arrayIzq,codigo+"0");
            proceso(arrayDer,codigo+"1");


        }else{
            if (arrayEntrada.size() == 1){
                this.datosSF.get(arrayEntrada.get(0).getClave()).setCodigo(codigo);
            }
            else{
                this.datosSF.get(arrayEntrada.get(0).getClave()).setCodigo(codigo+"0");
                this.datosSF.get(arrayEntrada.get(1).getClave()).setCodigo(codigo+"1");
            }
        }
     }



   /*  private void proceso(ArrayList<ElementoShannonFano> arraySF, ArrayList<Double> probs, int size, int posInicio) {
    	ArrayList<Double> probs1 = new ArrayList<Double>();
    	ArrayList<Double> probs2 = new ArrayList<Double>();
    	int puntoMedio = puntoMedio(probs,size);
    	ElementoShannonFano elemento;
    	
    	for (int i= 0; i < puntoMedio; i++) {
    		probs1.add(probs.get(i));
    		elemento = arraySF.get(posInicio+i);
    		elemento.setCodigo(elemento.getCodigo() + "1");
    		arraySF.set(posInicio + i, elemento);
    	}
    	for(int i=puntoMedio; i<size;i++) {
    		probs2.add(probs.get(i));
    		elemento = arraySF.get(posInicio+i);
    		elemento.setCodigo(elemento.getCodigo() + "0");
    		arraySF.set(posInicio + i, elemento);
    	}
    	if(probs1.size() > 1)
    		proceso(arraySF,probs1,probs1.size(),posInicio);
    	if(probs2.size() > 1)
    		proceso(arraySF,probs2,probs2.size(),posInicio + puntoMedio);
    	return;
		
		
	}

	private int puntoMedio(ArrayList<Double> probs, int size) {
		int medio;
		double primeraSuma, segundaSuma, resultadoAnterior;
		
		primeraSuma = probs.get(0);
		segundaSuma = 0;
		for (int i=1; i<size; i++)
			segundaSuma += probs.get(i);
		medio = 1;
		
		do {
			medio++;
			resultadoAnterior= Math.abs(primeraSuma - segundaSuma);
			primeraSuma=0;
			segundaSuma=0;
			for(int i=0; i < medio; i++)
				primeraSuma +=probs.get(i);
			for(int j=medio; j<size;j++)
				segundaSuma +=probs.get(j);
		} while (Math.abs(primeraSuma - segundaSuma) <= resultadoAnterior );
		return (int) Math.ceil(medio - 1);
	} */

    @SuppressWarnings("unchecked")
	public void decodificacion(String tipo) {
        File archivo;
        if(tipo.equals("huf"))
            archivo = new File("hufCodificado.huf");
        else
            archivo = new File("sfCodificado.fan");
        HashMap<String, String> map = new HashMap<String, String>();
        if (archivo.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(archivo);
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                map = (HashMap<String, String>) objectStream.readObject();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println("palabra: " + entry.getKey() + "codigo: " + entry.getValue());
                }
                File archivo2 = new File(tipo + "Decodificado.txt");
                if (!archivo2.exists())
                    archivo2.createNewFile();


                FileWriter writer = new FileWriter(archivo2);

                
                BufferedWriter bufWriter = new BufferedWriter(writer);

             
                int read = 0;
                byte bit;
                
                ArrayList<Boolean> binario = new ArrayList<Boolean>();
                read = (byte) inputStream.read();
                while (read != -1) {
                    byte lecturaEnByte = (byte) read;
                    for (int i = 0; i < 8; i++) {
                        bit = (byte) (lecturaEnByte & (1 << (7 - i)));
                        binario.add(bit!=0);
                    }
                    read = inputStream.read();
                }
                String codigo = "";
                Iterator iteradorbinario = binario.iterator();
                while (iteradorbinario.hasNext()) {
                    boolean bitt = (Boolean) iteradorbinario.next();
                    if(bitt)
                        codigo = codigo.concat("1");
                    else
                        codigo = codigo.concat("0");

                    if (map.containsValue(codigo)) {
                        bufWriter.write(this.buscarKey(map, codigo) + " ");
                        codigo = "";
                    }
                }


                bufWriter.close();
                writer.close();
                objectStream.close();
                inputStream.close();


            } catch (Exception e) {}

        }
    }

    public String buscarKey(HashMap<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return "";
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
            try (BufferedReader lector =
                 new BufferedReader(new InputStreamReader(new FileInputStream(arch), StandardCharsets.UTF_8))) {
                String codigo = "";
                String palabra = "";
                while ((letra = (char) lector.read()) != 65535) {

                    if (!finDiccionario) {

                        if (lecturaPalabra) {
                            if (letra == '|')
                                finDiccionario = true;
                            else if (letra == '>')
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
                    }

                    if (inicioArchivo) {
                        if (letra == ' ') {
                            myWriter.write(palabras.get(codigo) + " ");
                            codigo = "";
                        } else if (letra != '\n') {
                            codigo += letra;
                        } else {
                            myWriter.write('\n');
                            codigo = "";
                        }
                    } else if (finDiccionario && letra == '\n') {
                        inicioArchivo = true;
                    }
                }
                myWriter.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error de lectura de archivo");
        }
    }
    
}