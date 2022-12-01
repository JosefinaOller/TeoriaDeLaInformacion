package prueba;

import javax.swing.JOptionPane;
import modelo.PrimeraParte;

public class Prueba
{

	public static void main(String[] args)
	{
		PrimeraParte primeraparte = new PrimeraParte();
		primeraparte.leeArchivo();
		primeraparte.procesamiento();
		String[] options = new String[] { "Huffman", "Shannon-Fano", "Terminar" };
		int option = JOptionPane.showOptionDialog(null, "\u00bfQu\u00e9 hacemos?", "Elegir", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		do
		{
			switch (option)
			{
			case 0:
				primeraparte.huffman();
				break;
			case 1:
				primeraparte.ShannonFano();
				break;
			default:
				break;
			}
			option = JOptionPane.showOptionDialog(null, "\u00bfQu\u00e9 hacemos?", "Elegir", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		} while (option != 2);
	}

}
