package prueba;

import modelo.PrimeraParte;

public class Prueba {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrimeraParte primeraparte = new PrimeraParte();
		
		//primeraparte.leeArchivo();
		//primeraparte.procesamiento();
		//primeraparte.huffman();
		//primeraparte.descomprimirArchivo("tp2_grupo1.huf", "hufDescomprimido.txt");
		primeraparte.descomprimirArchivo("tp2_grupo1.fan", "fanDescomprimido.txt");
	}

}
